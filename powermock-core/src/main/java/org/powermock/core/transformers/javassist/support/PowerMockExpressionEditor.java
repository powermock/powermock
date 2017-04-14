/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers.javassist.support;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.javassist.support.TransformerHelper;

import static org.powermock.core.transformers.TransformStrategy.INST_REDEFINE;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.VOID;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.getCorrectReturnValueType;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.getReturnTypeAsString;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.isNotSyntheticField;

public final class PowerMockExpressionEditor extends ExprEditor {
    
    private final CtClass clazz;
    private final TransformStrategy strategy;
    
    public PowerMockExpressionEditor(final TransformStrategy strategy, final CtClass clazz) {
        this.strategy = strategy;
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
            throw new IllegalArgumentException("Internal error: Failed to get superclass for " + clazz.getName() + " when about to create a new default constructor.");
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
        String code = "Object instance =" +
                          MockGateway.class.getName() +
                          ".newInstanceCall($type,$args,$sig);" +
                          "if(instance != " + MockGateway.class.getName() + ".PROCEED) {" +
                          "	if(instance instanceof java.lang.reflect.Constructor) {"
                          +
                          "		$_ = ($r) sun.reflect.ReflectionFactory.getReflectionFactory().newConstructorForSerialization($type, java.lang.Object.class.getDeclaredConstructor(null)).newInstance(null);" +
                          "	} else {" +
                          "		$_ = ($r) instance;" +
                          "	}" +
                          "} else {" +
                          "	$_ = $proceed($$);" +
                          "}";
        // TODO Change to objenisis instead
        e.replace(code);
    }
    
    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        try {
            final CtMethod method = m.getMethod();
            final CtClass declaringClass = method.getDeclaringClass();
            
            if (declaringClass != null) {
                if (TransformerHelper.shouldTreatAsSystemClassCall(declaringClass)) {
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
                code.append(" super((").append(IndicateReloadClass.class.getName()).append(") null);");
            }
            code.append("} else {");
            code.append("   $proceed($$);");
            code.append("}}");
            c.replace(code.toString());
        }
    }
    
    
}
