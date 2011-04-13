/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.transformers.impl;

import javassist.*;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.InnerClassesAttribute;
import javassist.expr.*;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.TransformStrategy.*;
import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;

public class MainMockTransformer implements MockTransformer {

    private static final String VOID = "";

    private TransformStrategy strategy;

    public MainMockTransformer() {
        this(CLASSLOADER);
    }

    public MainMockTransformer(TransformStrategy strategy) {
        this.strategy = strategy;
    }

    public CtClass transform(final CtClass clazz) throws Exception {
        if (clazz.isFrozen()) {
            clazz.defrost();
        }
        /*
         * Set class modifier to public to allow for mocking of package private
         * classes. This is needed because we've changed to CgLib naming policy
         * to allow for mocking of signed classes.
         */
        final String name = allowMockingOfPackagePrivateClasses(clazz);

        suppressStaticInitializerIfRequested(clazz, name);

        if (clazz.isInterface()) {
            return clazz;
        }

        // This should probably be configurable
        removeFinalModifierFromClass(clazz);

        allowMockingOfStaticAndFinalAndNativeMethods(clazz);

        // Convert all constructors to public
        setAllConstructorsToPublic(clazz);

        // Remove final from all static final fields. Not possible if using a java agent.
        removeFinalModifierFromAllStaticFinalFields(clazz);

        if(strategy != INST_TRANSFORM) {
            clazz.instrument(new PowerMockExpressionEditor(clazz));
        }

        /*
         * ClassPool may cause huge memory consumption if the number of CtClass
         * objects becomes amazingly large (this rarely happens since Javassist
         * tries to reduce memory consumption in various ways). To avoid this
         * problem, you can explicitly remove an unnecessary CtClass object from
         * the ClassPool. If you call detach() on a CtClass object, then that
         * CtClass object is removed from the ClassPool.
         */
        clazz.detach();
        return clazz;
    }

    private String allowMockingOfPackagePrivateClasses(final CtClass clazz) {
        final String name = clazz.getName();
        if(strategy != INST_REDEFINE) {
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

    private void suppressStaticInitializerIfRequested(final CtClass clazz, final String name) throws CannotCompileException {
        if(strategy == CLASSLOADER) {
            if (MockGateway.staticConstructorCall(name) != MockGateway.PROCEED) {
                CtConstructor classInitializer = clazz.makeClassInitializer();
                classInitializer.setBody("{}");
            }
        }
    }

    private void removeFinalModifierFromClass(final CtClass clazz) {
        if(strategy != INST_REDEFINE) {
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

    private void allowMockingOfStaticAndFinalAndNativeMethods(final CtClass clazz) throws NotFoundException, CannotCompileException {
        if(strategy != INST_TRANSFORM) {
            for (CtMethod m : clazz.getDeclaredMethods()) {
                modifyMethod(m);
            }
        }
    }

    private void removeFinalModifierFromAllStaticFinalFields(final CtClass clazz) {
        if(strategy != INST_REDEFINE) {
            for (CtField f : clazz.getDeclaredFields()) {
                final int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                    f.setModifiers(modifiers ^ Modifier.FINAL);
                }
            }
        }
    }

    private void setAllConstructorsToPublic(final CtClass clazz) {
        if(strategy == CLASSLOADER) {
            for (CtConstructor c : clazz.getDeclaredConstructors()) {
                final int modifiers = c.getModifiers();
                if (!Modifier.isPublic(modifiers)) {
                    c.setModifiers(Modifier.setPublic(modifiers));
                }
            }
        }
    }

    public void modifyMethod(final CtMethod method) throws NotFoundException, CannotCompileException {
        if (!Modifier.isAbstract(method.getModifiers())) {
            // Lookup the method return type
            final CtClass returnTypeAsCtClass = method.getReturnType();
            final String returnTypeAsString = getReturnTypeAsString(method);

            if (Modifier.isNative(method.getModifiers())) {
                String methodName = method.getName();
                String returnValue = "($r)value";

                if (returnTypeAsCtClass.equals(CtClass.voidType)) {
                    returnValue = VOID;
                }

                String classOrInstance = "this";
                if (Modifier.isStatic(method.getModifiers())) {
                    classOrInstance = "$class";
                }
                method.setModifiers(method.getModifiers() - Modifier.NATIVE);
                String code = "Object value = " + MockGateway.class.getName() + ".methodCall(" + classOrInstance + ", \"" + method.getName()
                        + "\", $args, $sig, \"" + returnTypeAsString + "\");" + "if (value != " + MockGateway.class.getName() + ".PROCEED) "
                        + "return " + returnValue + "; " + "throw new java.lang.UnsupportedOperationException(\"" + methodName + " is native\");";
                method.setBody("{" + code + "}");
                return;
            }

            final String returnValue = getCorrectReturnValueType(returnTypeAsCtClass);

            String classOrInstance = "this";
            if (Modifier.isStatic(method.getModifiers())) {
                classOrInstance = "$class";
            }

            String code = "Object value = " + MockGateway.class.getName() + ".methodCall(" + classOrInstance + ", \"" + method.getName()
                    + "\", $args, $sig, \"" + returnTypeAsString + "\");" + "if (value != " + MockGateway.class.getName() + ".PROCEED) " + "return "
                    + returnValue + "; ";

            method.insertBefore("{ " + code + "}");
        }
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
     *         type to primitive type if needed.
     */
    private String getCorrectReturnValueType(final CtClass returnTypeAsCtClass) {
        final String returnTypeAsString = returnTypeAsCtClass.getName();
        String returnValue = "($r)value";
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

    private final class PowerMockExpressionEditor extends ExprEditor {
        private final CtClass clazz;

        private PowerMockExpressionEditor(CtClass clazz) {
            this.clazz = clazz;
        }

        @Override
        public void edit(FieldAccess f) throws CannotCompileException {
            if (f.isReader()) {
                CtClass returnTypeAsCtClass;
                try {
                    returnTypeAsCtClass = f.getField().getType();
                } catch (NotFoundException e) {
                    throw new RuntimeException("PowerMock internal error when modifying field.", e);
                }
                StringBuilder code = new StringBuilder();
                code.append("{Object value =  ").append(MockGateway.class.getName()).append(".fieldCall(").append("$0,$class,\"").append(
                        f.getFieldName()).append("\",$type);");
                code.append("if(value == ").append(MockGateway.class.getName()).append(".PROCEED) {");
                code.append("	$_ = $proceed($$);");
                code.append("} else {");
                code.append("	$_ = ").append(getCorrectReturnValueType(returnTypeAsCtClass)).append(";");
                code.append("}}");
                f.replace(code.toString());
            }
        }

        @Override
        public void edit(MethodCall m) throws CannotCompileException {
            try {
                final CtMethod method = m.getMethod();
                final CtClass declaringClass = method.getDeclaringClass();
                if (declaringClass != null) {
                    if (shouldTreatAsSystemClassCall(method, declaringClass)) {
                        StringBuilder code = new StringBuilder();
                        code.append("{Object classOrInstance = null; if($0!=null){classOrInstance = $0;} else { classOrInstance = $class;}");
                        code.append("Object value =  ").append(MockGateway.class.getName()).append(".methodCall(").append("classOrInstance,\"")
                                .append(m.getMethodName()).append("\",$args, $sig,\"").append(getReturnTypeAsString(method)).append("\");");
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
                throw new RuntimeException(e);
            }
        }

        private boolean shouldTreatAsSystemClassCall(CtMethod method, CtClass declaringClass) throws NotFoundException {
            final String className = declaringClass.getName();
            if(!className.startsWith("java.")) {
                return false;
            }
            final int methodModifiers = method.getModifiers();
            final boolean requiredByModifier = Modifier.isFinal(declaringClass.getModifiers()) ||
                    Modifier.isFinal(methodModifiers) ||
                    Modifier.isStatic(methodModifiers) ||
                    Modifier.isNative(methodModifiers);
            return requiredByModifier || isJavaStandardMethod(method);
        }

        private  boolean isJavaStandardMethod(CtMethod method) throws NotFoundException {
            final String methodName = method.getName();
            final CtClass[] sig = method.getParameterTypes();
            return (methodName.equals("equals") && sig.length == 1) || (methodName.equals("hashCode") && sig.length == 0)
                    || (methodName.equals("toString") && sig.length == 0);
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
                CtClass superclass = null;
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
                code.append("{Object value =").append(MockGateway.class.getName()).append(".constructorCall($class, $args, $sig);");
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
         * @param clazz
         *            The class whose super constructor will get a new defer
         *            constructor if it doesn't already have one.
         * @throws CannotCompileException
         *             If an unexpected compilation error occurs.
         */
        private void addNewDeferConstructor(final CtClass clazz) throws CannotCompileException {
            CtClass superClass = null;
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
            CtClass constructorType = null;
            try {
                constructorType = classPool.get(IndicateReloadClass.class.getName());
            } catch (NotFoundException e) {
                throw new IllegalArgumentException("Internal error: failed to get the " + IndicateReloadClass.class.getName()
                        + " when added defer constructor.");
            }
            clazz.defrost();
            if (superClass.getName().equals(Object.class.getName())) {
                try {
                    clazz.addConstructor(CtNewConstructor.make(new CtClass[] { constructorType }, new CtClass[0], "{super();}", clazz));
                } catch (DuplicateMemberException e) {
                    // OK, the constructor has already been added.
                }
            } else {
                addNewDeferConstructor(superClass);
                try {
                    clazz.addConstructor(CtNewConstructor.make(new CtClass[] { constructorType }, new CtClass[0], "{super($$);}", clazz));
                } catch (DuplicateMemberException e) {
                    // OK, the constructor has already been added.
                }
            }
        }

        @Override
        public void edit(NewExpr e) throws CannotCompileException {
            final StringBuilder code = new StringBuilder();
            code.append("Object instance =").append(MockGateway.class.getName()).append(".newInstanceCall($type,$args,$sig);");
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
