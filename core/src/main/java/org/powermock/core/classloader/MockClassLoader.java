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
package org.powermock.core.classloader;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import org.powermock.core.transformers.MockTransformer;

/**
 * Mock all classes except system classes.
 * 
 * Notice that there are two different types of classes that are not mocked:
 * <ol>
 * <li>system classes are deferred to another classloader</li>
 * <li>testing framework classes are loaded, but not modified</li>
 * </ol>
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
public final class MockClassLoader extends DeferSupportingClassLoader {

	/**
	 * Pass this string to the constructor to indicate that all classes should
	 * be modified.
	 */
	public static final String MODIFY_ALL_CLASSES = "*";

	private List<MockTransformer> mockTransformerChain;

	private Set<String> modify = Collections.synchronizedSet(new HashSet<String>());

	final private String[] ignore = new String[] { "org.hamcrest.", "org.junit.", "junit.", "org.easymock.", "org.powermock.", "net.sf.cglib.",
			"javassist." };

	// TODO Why is this needed!? We need to find a better solution.
	final private String ignoredClass = "net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$";
	final private String ignoredClass2 = "net.sf.cglib.core.MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB";
	private ClassPool classPool = new ClassPool();

	public MockClassLoader(String... classesToMock) {
		super(MockClassLoader.class.getClassLoader(), new String[] { "org.hamcrest.", "java.", "javax.accessibility.", "sun.", "org.junit.", "junit.",
				"org.powermock.modules.junit4.internal.", "org.powermock.modules.junit4.legacy.internal.",
				"org.powermock.modules.junit4.common.internal.", "org.powermock.modules.junit3.internal." });

		addClassesToModify(classesToMock);
		classPool.appendClassPath(new ClassClassPath(this.getClass()));
	}

	/**
	 * Add classes that will be loaded by the mock classloader, i.e. these
	 * classes will be byte-code manipulated to allow for testing.
	 * 
	 * @param classes
	 *            The fully qualified name of the classes that will be appended
	 *            to the list of classes that will be byte-code modified to
	 *            enable testability.
	 */
	public void addClassesToModify(String... classes) {
		for (String clazz : classes) {
			modify.add(clazz);
		}
	}

	protected Class<?> loadModifiedClass(String s) throws ClassFormatError, ClassNotFoundException {
		Class<?> loadedClass = null;
		// findSystemClass(s);
		deferTo.loadClass(s);
		if (match(ignore, s) || !(match(modify, s) || (modify.size() == 1 && modify.iterator().next().equals(MODIFY_ALL_CLASSES)))) {
			loadedClass = loadUnmockedClass(s);
		} else {
			loadedClass = loadMockClass(s);
		}
		return loadedClass;
	}

	private Class<?> loadUnmockedClass(String name) throws ClassFormatError, ClassNotFoundException {
		byte bytes[] = null;
		try {
			/*
			 * TODO This if-statement is a VERY ugly hack to avoid the
			 * java.lang.ExceptionInInitializerError caused by
			 * "javassist.NotFoundException:
			 * net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$7fb24d72
			 * ". This happens after the
			 * 
			 * se.jayway.examples.tests.privatefield.
			 * SimplePrivateFieldServiceClassTest#testUseService(..) tests has
			 * been run and all other tests will fail if this class is tried to
			 * be loaded. Atm I have found no solution other than this ugly hack
			 * to make it work. We really need to investigate the real cause of
			 * this behavior.
			 */
			if (name.startsWith(ignoredClass) || name.startsWith(ignoredClass2)) {
				// ignore
			} else {
				final CtClass ctClass = classPool.get(name);
				if (ctClass.isFrozen()) {
					ctClass.defrost();
				}
				bytes = ctClass.toBytecode();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bytes == null ? null : defineClass(name, bytes, 0, bytes.length);
	}

	/**
	 * Load a mocked version of the class.
	 */
	private Class<?> loadMockClass(String name) {
		CtClass type = null;
		byte[] clazz = null;

		ClassPool.doPruning = false;
		try {
			// type = ClassPool.getDefault().get(name);
			type = classPool.get(name);
			if (type.isInterface()) {
				return deferTo.loadClass(name);
			} else {
				// Only modify classes, not interfaces.
				for (MockTransformer transformer : mockTransformerChain) {
					type = transformer.transform(type);
				}
				clazz = type.toBytecode();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to transform class with name " + name + ". Reason: " + e.getMessage(), e);
		}

		return defineClass(name, clazz, 0, clazz.length);
	}

	public void setMockTransformerChain(List<MockTransformer> mockTransformerChain) {
		this.mockTransformerChain = mockTransformerChain;
	}
}
