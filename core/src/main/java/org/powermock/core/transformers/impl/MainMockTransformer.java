/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.transformers.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.internal.IndicateReloadClass;
import org.powermock.core.transformers.MockTransformer;

public class MainMockTransformer implements MockTransformer {

	protected static final String METHOD_PREFIX = "____";

	public CtClass transform(final CtClass clazz) throws Exception {
		if (clazz.isFrozen()) {
			clazz.defrost();
		}

		if (MockGateway.staticConstructorCall(clazz.getName()) != MockGateway.PROCEED) {
			CtConstructor classInitializer = clazz.makeClassInitializer();
			classInitializer.setBody("{}");
		}

		// This should probably be configurable
		if (Modifier.isFinal(clazz.getModifiers())) {
			clazz.setModifiers(clazz.getModifiers() ^ Modifier.FINAL);
		}

		for (CtMethod m : clazz.getDeclaredMethods()) {
			modifyMethod(clazz, m);
		}

		// Convert all constructors to public
		for (CtConstructor c : clazz.getDeclaredConstructors()) {
			c.setModifiers(Modifier.PUBLIC);
		}

		clazz.instrument(new ExprEditor() {
			@Override
			public void edit(ConstructorCall c) throws CannotCompileException {
				/*
				 * Note that constructor call only intercepts calls to super or
				 * this from an instantiated class. This means that A a = new
				 * A(); will NOT trigger a ConstructorCall for the default
				 * constructor in A. If A where to extend B and A's constructor
				 * only delegates to super(), the default constructor of B would
				 * trigger a ConstructorCall. This means that we need to handle
				 * "suppressConstructorCode" both here and in NewExpr.
				 */
				if (!c.getClassName().startsWith("java.lang")) {

					/*
					 * Create a default constructor in the super class if it
					 * doesn't exist. This is needed because if the code in the
					 * current constructor should be suppressed (which we don't
					 * know at this moment of time) the parent class must have a
					 * default constructor that we can delegate to.
					 */
					addNewDeferConstructor(clazz);
					final StringBuilder code = new StringBuilder();
					code.append("{Object value =").append(MockGateway.class.getName()).append(".constructorCall($class, $args, $sig);");
					code.append("if (value != ").append(MockGateway.class.getName()).append(".PROCEED){");
					/*
					 * TODO Suppress and lazy inject field (when this feature is
					 * ready).
					 */
					code.append("super((" + IndicateReloadClass.class.getName() + ") null);");
					code.append("} else {");
					code.append("$proceed($$);");
					code.append("}}");
					c.replace(code.toString());
				}
			}

			/**
			 * Create a defer constructor in the class which will be called when
			 * the constructor is suppressed.
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
				 * To make a unique defer constructor we create a new
				 * constructor with one argument (IndicateReloadClass). So we
				 * get this class a Javassist class below.
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
		});
		return clazz;
	}

	public void modifyMethod(CtClass type, final CtMethod method) throws NotFoundException, CannotCompileException {

		// Lookup the method return type
		String returnTypeAsString = null;
		final CtClass returnTypeAsCtClass = method.getReturnType();
		if (!returnTypeAsCtClass.equals(CtClass.voidType)) {
			returnTypeAsString = returnTypeAsCtClass.getName();
		}

		if (Modifier.isNative(method.getModifiers())) {
			String methodName = method.getName();
			String returnValue = "($r)value";

			if (returnTypeAsCtClass.equals(CtClass.voidType)) {
				returnValue = "";
			}

			String classOrInstance = "this";
			if (Modifier.isStatic(method.getModifiers())) {
				classOrInstance = "$class";
			}
			method.setModifiers(method.getModifiers() - Modifier.NATIVE);
			String code = "Object value = " + MockGateway.class.getName() + ".methodCall(" + classOrInstance + ", \"" + method.getName()
					+ "\", $args, $sig, \"" + returnTypeAsString + "\");" + "if (value != " + MockGateway.class.getName() + ".PROCEED) " + "return "
					+ returnValue + "; " + "throw new java.lang.UnsupportedOperationException(\"" + methodName + " is native\");";
			method.setBody("{" + code + "}");
			return;
		}

		String returnValue = "($r)value";
		if (returnTypeAsCtClass.equals(CtClass.voidType)) {
			returnValue = "";
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

		String classOrInstance = "this";
		if (Modifier.isStatic(method.getModifiers())) {
			classOrInstance = "$class";
		}

		String code = "Object value = " + MockGateway.class.getName() + ".methodCall(" + classOrInstance + ", \"" + method.getName()
				+ "\", $args, $sig, \"" + returnTypeAsString + "\");" + "if (value != " + MockGateway.class.getName() + ".PROCEED) " + "return "
				+ returnValue + "; ";
		method.insertBefore("{" + code + "}");
	}
}
