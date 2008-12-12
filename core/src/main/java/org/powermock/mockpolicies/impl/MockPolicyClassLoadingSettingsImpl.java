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
package org.powermock.mockpolicies.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;

/**
 * The default implementation of the {@link MockPolicyClassLoadingSettings}
 * interface.
 */
public class MockPolicyClassLoadingSettingsImpl implements MockPolicyClassLoadingSettings {
	private Set<String> fullyQualifiedNamesOfClassesToLoadByMockClassloader;
	private Set<String> staticInitializersToSuppress;

	public MockPolicyClassLoadingSettingsImpl() {
		fullyQualifiedNamesOfClassesToLoadByMockClassloader = new LinkedHashSet<String>();
		staticInitializersToSuppress = new LinkedHashSet<String>();
	}

	public String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		if (fullyQualifiedNamesOfClassesToLoadByMockClassloader == null) {
			return new String[0];
		}
		return fullyQualifiedNamesOfClassesToLoadByMockClassloader.toArray(new String[fullyQualifiedNamesOfClassesToLoadByMockClassloader.size()]);
	}

	public String[] getStaticInitializersToSuppress() {
		if (staticInitializersToSuppress == null) {
			return new String[0];
		}
		return staticInitializersToSuppress.toArray(new String[staticInitializersToSuppress.size()]);
	}

	public void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String firstClass, String... additionalClasses) {
		fullyQualifiedNamesOfClassesToLoadByMockClassloader.add(firstClass);
		addFullyQualifiedNamesOfClassesToLoadByMockClassloader(additionalClasses);
	}

	public void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes) {
		for (String clazz : classes) {
			fullyQualifiedNamesOfClassesToLoadByMockClassloader.add(clazz);
		}
	}

	public void addStaticInitializersToSuppress(String firstStaticInitializerToSuppress, String... additionalStaticInitializersToSuppress) {
		staticInitializersToSuppress.add(firstStaticInitializerToSuppress);
		addStaticInitializersToSuppress(additionalStaticInitializersToSuppress);
	}

	public void addStaticInitializersToSuppress(String[] staticInitializersToSuppress) {
		for (String staticInitializerToSuppress : staticInitializersToSuppress) {
			this.staticInitializersToSuppress.add(staticInitializerToSuppress);
		}
	}

	public void setFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes) {
		fullyQualifiedNamesOfClassesToLoadByMockClassloader.clear();
		addFullyQualifiedNamesOfClassesToLoadByMockClassloader(classes);
	}

	public void setStaticInitializersToSuppress(String[] staticInitializersToSuppress) {
		this.staticInitializersToSuppress.clear();
		addStaticInitializersToSuppress(staticInitializersToSuppress);
	}

}
