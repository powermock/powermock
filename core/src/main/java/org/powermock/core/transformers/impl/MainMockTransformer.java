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
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import org.powermock.core.MockGateway;
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
					addDefaultConstructorInSuperClass(clazz);

					final StringBuilder code = new StringBuilder();
					code.append("{Object value =").append(
							MockGateway.class.getName()).append(
							".constructorCall($class, $args, $sig);");
					code.append("if (value != ").append(
							MockGateway.class.getName()).append(".PROCEED){");
					/*
					 * TODO Suppress and lazy inject field (when this feature is
					 * ready).
					 */
					code.append("super();");
					code.append("} else {");
					code.append("$proceed($$);");
					code.append("}}");
					c.replace(code.toString());
				}
			}

			/**
			 * Create a default constructor in the super class if it doesn't
			 * exist.
			 * 
			 * @param clazz
			 *            The class whose super constructor will get a new
			 *            default constructor if it doesn't already have one.
			 * @throws CannotCompileException
			 *             If an unexpected compilation error occurs.
			 */
			private void addDefaultConstructorInSuperClass(final CtClass clazz)
					throws CannotCompileException {
				CtClass tempClass = clazz;
				while (!tempClass.getName().equals(Object.class.getName())) {
					CtClass superClass = null;
					try {
						superClass = tempClass.getSuperclass();
					} catch (NotFoundException e1) {
						throw new IllegalArgumentException(
								"Internal error: Failed to get superclass for "
										+ tempClass.getName()
										+ " when about to create a new default constructor.");
					}

					try {
						CtConstructor declaredConstructor = superClass
								.getDeclaredConstructor(new CtClass[] {});
						if (!superClass.getName()
								.equals(Object.class.getName())) {
							declaredConstructor.setBody("{super();}");
						}
						tempClass = superClass;
					} catch (NotFoundException e) {
						superClass.addConstructor(CtNewConstructor.skeleton(
								new CtClass[0], new CtClass[0], superClass));
						break;
					}
				}
			}

			@Override
			public void edit(NewExpr e) throws CannotCompileException {
				final StringBuilder code = new StringBuilder();
				code.append("Object instance =").append(
						MockGateway.class.getName()).append(
						".newInstanceCall($type,$args,$sig);");
				code.append("if(instance != ").append(
						MockGateway.class.getName()).append(".PROCEED) {");
				// code.append("System.out.println(\"instance = \"+instance);");
				code.append("	$_ = ($r) instance;");
				code.append("} else {");
				// code.append("System.out.println(\"proceeding\");");
				code.append("	$_ = $proceed($$);");
				// code.append("System.out.println(\"after proceeding\");");
				code.append("}");
				e.replace(code.toString());
			}
		});
		return clazz;
	}

	public void modifyMethod(CtClass type, final CtMethod method)
			throws NotFoundException, CannotCompileException {

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
			String code = "Object value = " + MockGateway.class.getName()
					+ ".methodCall(" + classOrInstance + ", \""
					+ method.getName() + "\", $args, $sig, \""
					+ returnTypeAsString + "\");" + "if (value != "
					+ MockGateway.class.getName() + ".PROCEED) " + "return "
					+ returnValue + "; "
					+ "throw new java.lang.UnsupportedOperationException(\""
					+ methodName + " is native\");";
			method.setBody("{" + code + "}");
			return;
		}

		if (type.isFrozen()) {
			type.defrost();
		}
		String methodName = method.getName();
		final byte[] byteCode = method.getMethodInfo().getCodeAttribute()
				.getCode();
		CtMethod newMethod = new CtMethod(returnTypeAsCtClass, methodName,
				method.getParameterTypes(), type);
		newMethod.setName(METHOD_PREFIX + methodName);
		newMethod.setModifiers(method.getModifiers());
		type.addMethod(newMethod);
		String returnValue = "($r)value";
		String methodReturn = "value = ";
		if (returnTypeAsCtClass.equals(CtClass.voidType)) {
			returnValue = "";
			methodReturn = "";
		}

		String classOrInstance = "this";
		if (Modifier.isStatic(method.getModifiers())) {
			classOrInstance = "$class";
		}

		String code = "Object value = " + MockGateway.class.getName()
				+ ".methodCall(" + classOrInstance + ", \"" + method.getName()
				+ "\", $args, $sig, \"" + returnTypeAsString + "\");"
				+ "if (value != " + MockGateway.class.getName() + ".PROCEED) "
				+ "return " + returnValue + "; " + methodReturn
				+ newMethod.getName() + "($$);" + "return " + returnValue
				+ "; ";
		method.setBody("{" + code + "}");
		type.removeMethod(newMethod);
		method.instrument(new ExprEditor() {
			public void edit(MethodCall m) throws CannotCompileException {
				if (m.getMethodName().startsWith(METHOD_PREFIX)) {
					int offset = m.indexOfBytecode();
					CodeIterator iterator = method.getMethodInfo()
							.getCodeAttribute().iterator();
					iterator.move(offset);
					try {
						iterator.insert(byteCode);
					} catch (BadBytecode e) {
						throw new CannotCompileException(e);
					}
				}
			}
		});
	}
}
