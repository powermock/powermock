/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.CLASSLOADER;
import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;
import static org.powermock.core.transformers.TransformStrategy.INST_TRANSFORM;

public abstract class AbstractMainMockTransformer implements MockTransformer {

    private static final String VOID = "";
    private static final int METHOD_CODE_LENGTH_LIMIT = 65536;
    protected final TransformStrategy strategy;

    public AbstractMainMockTransformer(TransformStrategy strategy) {this.strategy = strategy;}

    public CtClass transform(final CtClass clazz) throws Exception {
        if (clazz.isFrozen()) {
            clazz.defrost();
        }
        return transformMockClass(clazz);
    }

    protected abstract CtClass transformMockClass(CtClass clazz) throws CannotCompileException, NotFoundException;

    protected String allowMockingOfPackagePrivateClasses(final CtClass clazz) {
        final String name = clazz.getName();
        if (strategy != INST_REDEFINE) {
            try {
                final int modifiers = clazz.getModifiers();
                if (Modifier.isPackage(modifiers)) {
                    if (!name.startsWith("java.") && !(clazz.isInterface() && clazz.getDeclaringClass() != null)) {
                        clazz.setModifiers(Modifier.setPublic(modifiers));
                    }
                }
            } catch (NotFoundException e) {
                // OK, continue
            }
        }
        return name;
    }

    protected void suppressStaticInitializerIfRequested(final CtClass clazz,
                                                        final String name) throws CannotCompileException {
        if (strategy == CLASSLOADER) {
            if (MockGateway.staticConstructorCall(name) != MockGateway.PROCEED) {
                CtConstructor classInitializer = clazz.makeClassInitializer();
                classInitializer.setBody("{}");
            }
        }
    }

    protected void removeFinalModifierFromClass(final CtClass clazz) {
        if (strategy != INST_REDEFINE) {
            if (Modifier.isFinal(clazz.getModifiers())) {
                clazz.setModifiers(clazz.getModifiers() ^ Modifier.FINAL);
            }

            ClassFile classFile = clazz.getClassFile2();
            AttributeInfo attribute = classFile.getAttribute(InnerClassesAttribute.tag);
            if (attribute != null && attribute instanceof InnerClassesAttribute) {
                InnerClassesAttribute ica = (InnerClassesAttribute) attribute;
                String name = classFile.getName();
                int n = ica.tableLength();
                for (int i = 0; i < n; ++i) {
                    if (name.equals(ica.innerClass(i))) {
                        int accessFlags = ica.accessFlags(i);
                        if (Modifier.isFinal(accessFlags)) {
                            ica.setAccessFlags(i, accessFlags ^ Modifier.FINAL);
                        }
                    }
                }
            }
        }
    }

    protected void allowMockingOfStaticAndFinalAndNativeMethods(final CtClass clazz) throws NotFoundException, CannotCompileException {
        if (strategy != INST_TRANSFORM) {
            for (CtMethod m : clazz.getDeclaredMethods()) {
                modifyMethod(m);
            }
        }
    }

    protected void removeFinalModifierFromAllStaticFinalFields(final CtClass clazz) {
        if (strategy != INST_REDEFINE) {
            for (CtField f : clazz.getDeclaredFields()) {
                final int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                    f.setModifiers(modifiers ^ Modifier.FINAL);
                }
            }
        }
    }

    protected void setAllConstructorsToPublic(final CtClass clazz) {
        if (strategy == CLASSLOADER) {
            for (CtConstructor c : clazz.getDeclaredConstructors()) {
                final int modifiers = c.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    c.setModifiers(Modifier.setPublic(modifiers));
                }
            }
        }
    }

    /**
     * According to JVM specification method size must be lower than 65536 bytes.
     * When that limit is exceeded class loader will fail to load the class.
     * Since instrumentation can increase method size significantly it must be
     * ensured that JVM limit is not exceeded.
     * <p/>
     * When the limit is exceeded method's body is replaced by exception throw.
     * Method is then instrumented again to allow mocking and suppression.
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3">JVM specification</a>
     */
    protected CtClass ensureJvmMethodSizeLimit(CtClass clazz) throws CannotCompileException, NotFoundException {
        for (CtMethod method : clazz.getDeclaredMethods()) {
            if (isMethodSizeExceeded(method)) {
                String code = "{throw new IllegalAccessException(\"" +
                                      "Method was too large and after instrumentation exceeded JVM limit. " +
                                      "PowerMock modified the method to allow JVM to load the class. " +
                                      "You can use PowerMock API to suppress or mock this method behaviour." +
                                      "\");}";
                method.setBody(code);
                modifyMethod(method);
            }
        }
        return clazz;
    }

    private boolean isMethodSizeExceeded(CtMethod method) {
        CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
        return codeAttribute != null && codeAttribute.getCodeLength() >= METHOD_CODE_LENGTH_LIMIT;
    }

    private void modifyMethod(final CtMethod method) throws NotFoundException, CannotCompileException {

        if (!shouldSkipMethod(method)) {

            // Lookup the method return type

            final CtClass returnTypeAsCtClass = method.getReturnType();
            final String returnTypeAsString = getReturnTypeAsString(method);

            if (Modifier.isNative(method.getModifiers())) {
                modifyNativeMethod(method, returnTypeAsCtClass, returnTypeAsString);
            } else {
                modifyMethod(method, returnTypeAsCtClass, returnTypeAsString);
            }
        }
    }

    private boolean shouldSkipMethod(CtMethod method) {
        return isAccessFlagSynthetic(method) || Modifier.isAbstract(method.getModifiers());
    }

    private boolean isAccessFlagSynthetic(CtMethod method) {
        int accessFlags = method.getMethodInfo2().getAccessFlags();
        return ((accessFlags & AccessFlag.SYNTHETIC) != 0) && !isBridgeMethod(method);
    }

    private void modifyMethod(CtMethod method, CtClass returnTypeAsCtClass,
                              String returnTypeAsString) throws CannotCompileException {
        final String returnValue = getCorrectReturnValueType(returnTypeAsCtClass);

        String classOrInstance = classOrInstance(method);

        String code = "Object value = "
                              + MockGateway.class.getName()
                              + ".methodCall("
                                + classOrInstance + ", \""
                                + method.getName()
                                + "\", $args, $sig, \""
                                + returnTypeAsString
                              + "\");"
                              + "if (value != " + MockGateway.class.getName() + ".PROCEED) " + "return "
                              + returnValue + "; ";

        method.insertBefore("{ " + code + "}");
    }

    private boolean isBridgeMethod(CtMethod method) {return (method.getMethodInfo2().getAccessFlags() & AccessFlag.BRIDGE) != 0;}

    private String classOrInstance(CtMethod method) {
        String classOrInstance = "this";
        if (Modifier.isStatic(method.getModifiers())) {
            classOrInstance = "$class";
        }
        return classOrInstance;
    }

    private void modifyNativeMethod(CtMethod method, CtClass returnTypeAsCtClass,
                                    String returnTypeAsString) throws CannotCompileException {
        String methodName = method.getName();
        String returnValue = "($r)value";

        if (returnTypeAsCtClass.equals(CtClass.voidType)) {
            returnValue = VOID;
        }

        String classOrInstance = classOrInstance(method);
        method.setModifiers(method.getModifiers() - Modifier.NATIVE);
        String code = "Object value = "
                              + MockGateway.class.getName()
                              + ".methodCall("
                                + classOrInstance
                                + ", \""
                                + method.getName()
                                + "\", $args, $sig, \""
                                + returnTypeAsString
                              + "\");"
                              + "if (value != "
                                + MockGateway.class.getName() + ".PROCEED) "
                                + "return "
                                + returnValue + "; "
                                    + "throw new java.lang.UnsupportedOperationException(\"" + methodName + " is native\");";
        method.setBody("{" + code + "}");
    }

    private String getReturnTypeAsString(final CtMethod method) throws NotFoundException {
        CtClass returnType = method.getReturnType();
        String returnTypeAsString = VOID;
        if (!returnType.equals(CtClass.voidType)) {
            returnTypeAsString = returnType.getName();
        }
        return returnTypeAsString;
    }

    /**
     * @return The correct return type, i.e. takes care of casting the a wrapper
     * type to primitive type if needed.
     */
    private String getCorrectReturnValueType(final CtClass returnTypeAsCtClass) {
        final String returnTypeAsString = returnTypeAsCtClass.getName();
        final String returnValue;
        if (returnTypeAsCtClass.equals(CtClass.voidType)) {
            returnValue = VOID;
        } else if (returnTypeAsCtClass.isPrimitive()) {
            if (returnTypeAsString.equals("char")) {
                returnValue = "((java.lang.Character)value).charValue()";
            } else if (returnTypeAsString.equals("boolean")) {
                returnValue = "((java.lang.Boolean)value).booleanValue()";
            } else {
                returnValue = "((java.lang.Number)value)." + returnTypeAsString + "Value()";
            }
        } else {
            returnValue = "(" + returnTypeAsString + ")value";
        }
        return returnValue;
    }

    private boolean isNotSyntheticField(FieldInfo fieldInfo) {
        return (fieldInfo.getAccessFlags() & AccessFlag.SYNTHETIC) == 0;
    }

    protected final class PowerMockExpressionEditor extends ExprEditor {
        private final CtClass clazz;

        protected PowerMockExpressionEditor(CtClass clazz) {
            this.clazz = clazz;
        }

        @Override
        public void edit(FieldAccess f) throws CannotCompileException {
            if (f.isReader()) {
                CtClass returnTypeAsCtClass;
                FieldInfo fieldInfo;

                try {
                    CtField field = f.getField();
                    returnTypeAsCtClass = field.getType();
                    fieldInfo = field.getFieldInfo2();
                } catch (NotFoundException e) {
                        /*
                         * If multiple java agents are active (in INST_REDEFINE mode), the types implicitly loaded by javassist from disk
                         * might differ from the types available in memory. Thus, this error might occur.
                         *
                         * It may also happen if PowerMock is modifying an SPI where the SPI require some classes to be available in the classpath
                         * at runtime but they are not! This is valid in some cases such as slf4j.
                         */
                    return;
                }

                if (isNotSyntheticField(fieldInfo)) {
                    String code = "{Object value =  " +
                                          MockGateway.class.getName() +
                                          ".fieldCall(" +
                                          "$0,$class,\"" +
                                          f.getFieldName() +
                                          "\",$type);" +
                                          "if(value == " + MockGateway.class.getName() + ".PROCEED) {" +
                                          "	$_ = $proceed($$);" +
                                          "} else {" +
                                          "	$_ = " + getCorrectReturnValueType(returnTypeAsCtClass) + ";" +
                                          "}}";
                    f.replace(code);
                }
            }
        }

        @Override
        public void edit(MethodCall m) throws CannotCompileException {
            try {
                final CtMethod method = m.getMethod();
                final CtClass declaringClass = method.getDeclaringClass();

                if (declaringClass != null) {
                    if (shouldTreatAsSystemClassCall(declaringClass)) {
                        StringBuilder code = new StringBuilder();
                        code.append("{Object classOrInstance = null; if($0!=null){classOrInstance = $0;} else { classOrInstance = $class;}");
                        code.append("Object value =  ")
                            .append(MockGateway.class.getName())
                            .append(".methodCall(")
                            .append("classOrInstance,\"")
                            .append(m.getMethodName())
                            .append("\",$args, $sig,\"")
                            .append(getReturnTypeAsString(method))
                            .append("\");");
                        code.append("if(value == ").append(MockGateway.class.getName()).append(".PROCEED) {");
                        code.append("	$_ = $proceed($$);");
                        code.append("} else {");
                        final String correctReturnValueType = getCorrectReturnValueType(method.getReturnType());
                        if (!VOID.equals(correctReturnValueType)) {
                            code.append("	$_ = ").append(correctReturnValueType).append(";");
                        }
                        code.append("}}");
                        m.replace(code.toString());
                    }
                }
            } catch (NotFoundException e) {
                    /*
                     * If multiple java agents are active (in INST_REDEFINE mode), the types implicitly loaded by javassist from disk
                     * might differ from the types available in memory. Thus, this error might occur.
                     *
                     * It may also happen if PowerMock is modifying an SPI where the SPI require some classes to be available in the classpath
                     * at runtime but they are not! This is valid in some cases such as slf4j.
                     */
            }
        }

        private boolean shouldTreatAsSystemClassCall(CtClass declaringClass) {
            final String className = declaringClass.getName();
            return className.startsWith("java.");
        }

        @Override
        public void edit(ConstructorCall c) throws CannotCompileException {
            /*
             * Note that constructor call only intercepts calls to super or this
             * from an instantiated class. This means that A a = new A(); will
             * NOT trigger a ConstructorCall for the default constructor in A.
             * If A where to extend B and A's constructor only delegates to
             * super(), the default constructor of B would trigger a
             * ConstructorCall. This means that we need to handle
             * "suppressConstructorCode" both here and in NewExpr.
             */
            if (strategy != INST_REDEFINE && !c.getClassName().startsWith("java.lang")) {
                final CtClass superclass;
                try {
                    superclass = clazz.getSuperclass();
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }

                /*
                 * Create a default constructor in the super class if it doesn't
                 * exist. This is needed because if the code in the current
                 * constructor should be suppressed (which we don't know at this
                 * moment of time) the parent class must have a default
                 * constructor that we can delegate to.
                 */
                addNewDeferConstructor(clazz);
                final StringBuilder code = new StringBuilder();
                code.append("{Object value =")
                    .append(MockGateway.class.getName())
                    .append(".constructorCall($class, $args, $sig);");
                code.append("if (value != ").append(MockGateway.class.getName()).append(".PROCEED){");

                /*
                 * TODO Suppress and lazy inject field (when this feature is ready).
                 */
                if (superclass.getName().equals(Object.class.getName())) {
                    code.append(" super();");
                } else {
                    code.append(" super((" + IndicateReloadClass.class.getName() + ") null);");
                }
                code.append("} else {");
                code.append("   $proceed($$);");
                code.append("}}");
                c.replace(code.toString());
            }
        }

        /**
         * Create a defer constructor in the class which will be called when the
         * constructor is suppressed.
         *
         * @param clazz The class whose super constructor will get a new defer
         *              constructor if it doesn't already have one.
         * @throws CannotCompileException If an unexpected compilation error occurs.
         */
        private void addNewDeferConstructor(final CtClass clazz) throws CannotCompileException {
            final CtClass superClass;
            try {
                superClass = clazz.getSuperclass();
            } catch (NotFoundException e1) {
                throw new IllegalArgumentException("Internal error: Failed to get superclass for " + clazz.getName()
                                                           + " when about to create a new default constructor.");
            }

            ClassPool classPool = clazz.getClassPool();
            /*
             * To make a unique defer constructor we create a new constructor
             * with one argument (IndicateReloadClass). So we get this class a
             * Javassist class below.
             */
            final CtClass constructorType;
            try {
                constructorType = classPool.get(IndicateReloadClass.class.getName());
            } catch (NotFoundException e) {
                throw new IllegalArgumentException("Internal error: failed to get the " + IndicateReloadClass.class.getName()
                                                           + " when added defer constructor.");
            }
            clazz.defrost();
            if (superClass.getName().equals(Object.class.getName())) {
                try {
                    clazz.addConstructor(CtNewConstructor.make(new CtClass[]{constructorType}, new CtClass[0], "{super();}", clazz));
                } catch (DuplicateMemberException e) {
                    // OK, the constructor has already been added.
                }
            } else {
                addNewDeferConstructor(superClass);
                try {
                    clazz.addConstructor(CtNewConstructor.make(new CtClass[]{constructorType}, new CtClass[0], "{super($$);}", clazz));
                } catch (DuplicateMemberException e) {
                    // OK, the constructor has already been added.
                }
            }
        }

        @Override
        public void edit(NewExpr e) throws CannotCompileException {
            final StringBuilder code = new StringBuilder();
            code.append("Object instance =")
                .append(MockGateway.class.getName())
                .append(".newInstanceCall($type,$args,$sig);");
            code.append("if(instance != ").append(MockGateway.class.getName()).append(".PROCEED) {");
            code.append("	if(instance instanceof java.lang.reflect.Constructor) {");
            // TODO Change to objenisis instead
            code
                    .append("		$_ = ($r) sun.reflect.ReflectionFactory.getReflectionFactory().newConstructorForSerialization($type, java.lang.Object.class.getDeclaredConstructor(null)).newInstance(null);");
            code.append("	} else {");
            code.append("		$_ = ($r) instance;");
            code.append("	}");
            code.append("} else {");
            code.append("	$_ = $proceed($$);");
            code.append("}");
            e.replace(code.toString());
        }
    }
}
