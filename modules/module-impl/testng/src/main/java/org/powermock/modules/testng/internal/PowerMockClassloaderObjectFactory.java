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
package org.powermock.modules.testng.internal;

import javassist.util.proxy.ProxyFactory;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.proxyframework.RegisterProxyFramework;
import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.testng.IObjectFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.powermock.tests.utils.impl.StaticConstructorSuppressExtractorImpl;

@SuppressWarnings("serial")
public class PowerMockClassloaderObjectFactory implements IObjectFactory {

	private final List<MockTransformer> mockTransformerChain;
	private final TestClassesExtractor testClassesExtractor;

	private final IgnorePackagesExtractor ignorePackagesExtractor;

	private final StaticConstructorSuppressExtractorImpl staticConstructorSuppressExtractor;

	private final ExpectedExceptionsExtractor expectedExceptionsExtractor;

	public PowerMockClassloaderObjectFactory() {
		List<MockTransformer> mockTransformerChainMutable = new ArrayList<MockTransformer>();
		final MainMockTransformer mainMockTransformer = new MainMockTransformer();
		mockTransformerChainMutable.add(mainMockTransformer);

		mockTransformerChain = Collections.unmodifiableList(mockTransformerChainMutable);
		testClassesExtractor = new PrepareForTestExtractorImpl();
		ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();
		expectedExceptionsExtractor = new PowerMockExpectedExceptionsExtractorImpl();
		staticConstructorSuppressExtractor = new StaticConstructorSuppressExtractorImpl();
	}

	@Override
	public Object newInstance(@SuppressWarnings("rawtypes") Constructor constructor, Object... params) {
		/*
		 * For extra safety clear the MockitoRepository on each new
		 * instantiation of the object factory. This is good in cases where a
		 * previous test has used e.g. PowerMock#createMock(..) to create a mock
		 * without using this factory. That means that there's some state left in
		 * the MockRepository that hasn't been cleared. Currently clearing the
		 * MockRepository from any classloader will clear the previous state but
		 * it's not certain that this is always the case.
		 */
		MockRepository.clear();
		final Class<?> testClass = constructor.getDeclaringClass();
		final MockClassLoader mockLoader = new MockClassLoader(new String[0], new String[0]);
		mockLoader.setMockTransformerChain(mockTransformerChain);
		mockLoader.addIgnorePackage(ignorePackagesExtractor.getPackagesToIgnore(testClass));
		mockLoader.addIgnorePackage(expectedExceptionsExtractor.getPackagesToIgnore(testClass));
		mockLoader.addClassesToModify(testClassesExtractor.getTestClasses(testClass));
		mockLoader.addClassesToModify(staticConstructorSuppressExtractor.getClassesToModify(testClass));
		try {
			registerProxyframework(mockLoader);
			new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
			final Class<?> testClassLoadedByMockedClassLoader = createTestClass(testClass, mockLoader);
			Constructor<?> con = testClassLoadedByMockedClassLoader.getConstructor(constructor.getParameterTypes());
			final Object testInstance = con.newInstance(params);
			if (!extendsPowerMockTestCase(testClass)) {
				setInvocationHandler(testInstance, mockLoader);
			}
			return testInstance;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setInvocationHandler(Object testInstance, MockClassLoader mockLoader) throws Exception {
		Class<?> powerMockTestNGMethodHandlerClass = Class.forName(PowerMockTestNGMethodHandler.class.getName(), false, mockLoader);
		Object powerMockTestNGMethodHandlerInstance = powerMockTestNGMethodHandlerClass.getConstructor(Class.class).newInstance(
				testInstance.getClass());
		Whitebox.invokeMethod(testInstance, "setHandler", powerMockTestNGMethodHandlerInstance);
	}

	/**
	 * We proxy the test class in order to be able to clear state after each
	 * test method invocation. It would be much better to be able to register a
	 * testng listener programmtically but I cannot find a way to do so.
	 */
	private Class<?> createTestClass(Class<?> actualTestClass, MockClassLoader mockLoader) throws Exception {
		final Class<?> testClassLoadedByMockedClassLoader = Class.forName(actualTestClass.getName(), false, mockLoader);
		if (extendsPowerMockTestCase(actualTestClass)) {
			return testClassLoadedByMockedClassLoader;
		} else {

			Class<?> proxyFactoryClass = Class.forName(ProxyFactory.class.getName(), false, mockLoader);
			final Class<?> testNGMethodFilterByMockedClassLoader = Class.forName(TestNGMethodFilter.class.getName(), false, mockLoader);

			Object f = proxyFactoryClass.newInstance();
			Object filter = testNGMethodFilterByMockedClassLoader.newInstance();
			Whitebox.invokeMethod(f, "setFilter", filter);
			Whitebox.invokeMethod(f, "setSuperclass", testClassLoadedByMockedClassLoader);
			Class<?> c = Whitebox.invokeMethod(f, "createClass");
			return c;
		}
	}

	private boolean extendsPowerMockTestCase(Class<?> actualTestClass) {
		return PowerMockTestCase.class.isAssignableFrom(actualTestClass);
	}

	private void registerProxyframework(ClassLoader classLoader) {
		Class<?> proxyFrameworkClass = null;
		try {
			proxyFrameworkClass = Class.forName("org.powermock.api.extension.proxyframework.ProxyFrameworkImpl", false, classLoader);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(
					"Extension API internal error: org.powermock.api.extension.proxyframework.ProxyFrameworkImpl could not be located in classpath.");
		}

		Class<?> proxyFrameworkRegistrar = null;
		try {
			proxyFrameworkRegistrar = Class.forName(RegisterProxyFramework.class.getName(), false, classLoader);
		} catch (ClassNotFoundException e) {
			// Should never happen
			throw new RuntimeException(e);
		}
		try {
			Whitebox.invokeMethod(proxyFrameworkRegistrar, "registerProxyFramework", Whitebox.newInstance(proxyFrameworkClass));
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
