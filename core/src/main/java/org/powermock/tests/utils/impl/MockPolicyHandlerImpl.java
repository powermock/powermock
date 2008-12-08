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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.powermock.core.MockRepository;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.MockPolicyHandler;

/**
 * The default implementation of the {@link MockPolicyHandler} interface for
 * mock policies.
 */
public class MockPolicyHandlerImpl implements MockPolicyHandler {

	private final PowerMockPolicy[] mockPolicies;

	public MockPolicyHandlerImpl(Class<? extends PowerMockPolicy>[] mockPolicies) {
		if (mockPolicies == null) {
			this.mockPolicies = new PowerMockPolicy[0];
		} else {
			this.mockPolicies = new PowerMockPolicy[mockPolicies.length];
			for (int i = 0; i < mockPolicies.length; i++) {
				this.mockPolicies[i] = Whitebox.newInstance(mockPolicies[i]);
			}
		}
	}

	public String[] getClassesToBeLoadedByMockClassloader() {
		List<String> list = new LinkedList<String>();
		for (PowerMockPolicy mockPolicy : mockPolicies) {
			final String[] fullyQualifiedNamesToLoadByMockClassloader = mockPolicy.getFullyQualifiedNamesOfClassesToLoadByMockClassloader();
			if (fullyQualifiedNamesToLoadByMockClassloader != null) {
				list.addAll(Arrays.asList(fullyQualifiedNamesToLoadByMockClassloader));
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public String[] initStaticSuppression() {
		final String[] staticInitializersToSuppress = getClassesToSuppress();
		for (String classToSuppress : staticInitializersToSuppress) {
			MockRepository.addSuppressStaticInitializer(classToSuppress);
		}
		return staticInitializersToSuppress;
	}

	public boolean isPrepared(String fullyQualifiedClassName) {
		final boolean foundInSuppressStaticInitializer = Arrays.binarySearch(getClassesToSuppress(), fullyQualifiedClassName) < 0;
		final boolean foundClassesLoadedByMockClassloader = Arrays.binarySearch(getClassesToBeLoadedByMockClassloader(), fullyQualifiedClassName) < 0;
		return foundInSuppressStaticInitializer || foundClassesLoadedByMockClassloader;
	}

	private String[] getClassesToSuppress() {
		List<String> list = new LinkedList<String>();
		for (PowerMockPolicy mockPolicy : mockPolicies) {
			final String[] staticInitializersToSuppress = mockPolicy.getStaticInitializersToSuppress();
			if (staticInitializersToSuppress != null) {
				list.addAll(Arrays.asList(staticInitializersToSuppress));
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public Map<Method, Object> initSubstituteReturnValues() {
		final Map<Method, Object> all = new HashMap<Method, Object>();
		for (PowerMockPolicy mockPolicy : mockPolicies) {
			final Map<Method, Object> subtituteReturnValues = mockPolicy.getSubtituteReturnValues();
			if (subtituteReturnValues != null) {
				all.putAll(subtituteReturnValues);
			}
		}
		for (Entry<Method, Object> entry : all.entrySet()) {
			MockRepository.putSubstituteReturnValue(entry.getKey(), entry.getValue());
		}
		return Collections.unmodifiableMap(all);
	}
}
