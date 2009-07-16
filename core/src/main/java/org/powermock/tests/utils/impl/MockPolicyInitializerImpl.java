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
package org.powermock.tests.utils.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map.Entry;

import org.powermock.core.MockRepository;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.mockpolicies.impl.MockPolicyClassLoadingSettingsImpl;
import org.powermock.mockpolicies.impl.MockPolicyInterceptionSettingsImpl;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.MockPolicyInitializer;

/**
 * The default implementation of the {@link MockPolicyInitializer} interface for
 * mock policies.
 */
public class MockPolicyInitializerImpl implements MockPolicyInitializer {

	private final PowerMockPolicy[] mockPolicies;
	private final Class<? extends PowerMockPolicy>[] mockPolicyTypes;

	public MockPolicyInitializerImpl(Class<? extends PowerMockPolicy>[] mockPolicies) {
		this(mockPolicies, false);
	}

	public MockPolicyInitializerImpl(Class<?> testClass) {
		this(getMockPolicies(testClass), false);
	}

	private MockPolicyInitializerImpl(Class<? extends PowerMockPolicy>[] mockPolicies, boolean internal) {
		if (internal) {
			mockPolicyTypes = null;
		} else {
			mockPolicyTypes = mockPolicies;
		}
		if (mockPolicies == null) {
			this.mockPolicies = new PowerMockPolicy[0];
		} else {
			this.mockPolicies = new PowerMockPolicy[mockPolicies.length];
			for (int i = 0; i < mockPolicies.length; i++) {
				this.mockPolicies[i] = Whitebox.newInstance(mockPolicies[i]);
			}
		}
	}

	public boolean isPrepared(String fullyQualifiedClassName) {
		MockPolicyClassLoadingSettings settings = getClassLoadingSettings();
		final boolean foundInSuppressStaticInitializer = Arrays.binarySearch(
				settings.getStaticInitializersToSuppress(), fullyQualifiedClassName) < 0;
		final boolean foundClassesLoadedByMockClassloader = Arrays.binarySearch(settings
				.getFullyQualifiedNamesOfClassesToLoadByMockClassloader(), fullyQualifiedClassName) < 0;
		return foundInSuppressStaticInitializer || foundClassesLoadedByMockClassloader;
	}

	public boolean needsInitialization() {
		MockPolicyClassLoadingSettings settings = getClassLoadingSettings();
		return settings.getStaticInitializersToSuppress().length > 0
				|| settings.getFullyQualifiedNamesOfClassesToLoadByMockClassloader().length > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(ClassLoader classLoader) {
		if (classLoader instanceof MockClassLoader) {
			initialize((MockClassLoader) classLoader);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	private void initialize(MockClassLoader classLoader) {
		if (mockPolicies.length > 0) {
			MockPolicyClassLoadingSettings classLoadingSettings = getClassLoadingSettings();
			String[] fullyQualifiedNamesOfClassesToLoadByMockClassloader = classLoadingSettings
					.getFullyQualifiedNamesOfClassesToLoadByMockClassloader();
			classLoader.addClassesToModify(fullyQualifiedNamesOfClassesToLoadByMockClassloader);

			for (String string : classLoadingSettings.getStaticInitializersToSuppress()) {
				classLoader.addClassesToModify(string);
				MockRepository.addSuppressStaticInitializer(string);
			}

			invokeInitializeInterceptionSettingsFromClassLoader(classLoader);
		}
	}

	private void invokeInitializeInterceptionSettingsFromClassLoader(MockClassLoader classLoader) {
		try {
			final int sizeOfPolicies = mockPolicyTypes.length;
			Object mockPolicies = Array.newInstance(Class.class, sizeOfPolicies);
			for (int i = 0; i < sizeOfPolicies; i++) {
				final Class<?> policyLoadedByClassLoader = Class.forName(mockPolicyTypes[i].getName(), false,
						classLoader);
				Array.set(mockPolicies, i, policyLoadedByClassLoader);
			}
			final Class<?> thisTypeLoadedByMockClassLoader = Class.forName(this.getClass().getName(), false,
					classLoader);
			Object mockPolicyHandler = Whitebox.invokeConstructor(thisTypeLoadedByMockClassLoader, mockPolicies, true);
			Whitebox.invokeMethod(mockPolicyHandler, "initializeInterceptionSettings");
		} catch (InvocationTargetException e) {
			final Throwable targetException = e.getTargetException();
			if (targetException instanceof RuntimeException) {
				throw (RuntimeException) targetException;
			} else if (targetException instanceof Error) {
				throw (Error) targetException;
			} else {
				throw new RuntimeException(e);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("PowerMock internal error: Failed to load class.", e);
		}
	}

	/*
	 * This method IS used, but it's invoked using reflection from the
	 * invokeInitializeInterceptionSettingsFromClassLoader method.
	 */
	@SuppressWarnings("unused")
	private void initializeInterceptionSettings() {
		MockPolicyInterceptionSettings interceptionSettings = getInterceptionSettings();

		for (Method method : interceptionSettings.getMethodsToSuppress()) {
			MockRepository.addMethodToSuppress(method);
		}

		for (Entry<Method, Object> entry : interceptionSettings.getSubtituteReturnValues().entrySet()) {
			final Method method = entry.getKey();
			final Object className = entry.getValue();
			MockRepository.putSubstituteReturnValue(method, className);
		}

		for (Field field : interceptionSettings.getFieldsToSuppress()) {
			MockRepository.addFieldToSuppress(field);
		}

		for (String type : interceptionSettings.getFieldTypesToSuppress()) {
			MockRepository.addFieldTypeToSuppress(type);
		}
	}

	private MockPolicyInterceptionSettings getInterceptionSettings() {
		MockPolicyInterceptionSettings settings = new MockPolicyInterceptionSettingsImpl();
		for (PowerMockPolicy mockPolicy : mockPolicies) {
			mockPolicy.applyInterceptionPolicy(settings);
		}
		return settings;
	}

	private MockPolicyClassLoadingSettings getClassLoadingSettings() {
		MockPolicyClassLoadingSettings settings = new MockPolicyClassLoadingSettingsImpl();
		for (PowerMockPolicy mockPolicy : mockPolicies) {
			mockPolicy.applyClassLoadingPolicy(settings);
		}
		return settings;
	}

	/**
	 * Get the mock policies from a test-class.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends PowerMockPolicy>[] getMockPolicies(Class<?> testClass) {
		Class<? extends PowerMockPolicy>[] powerMockPolicies = new Class[0];
		if (testClass.isAnnotationPresent(MockPolicy.class)) {
			MockPolicy annotation = testClass.getAnnotation(MockPolicy.class);
			powerMockPolicies = annotation.value();
		}
		return powerMockPolicies;
	}
}
