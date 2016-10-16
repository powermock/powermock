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
package org.powermock.mockpolicies.impl;

import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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

	@Override
	public String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		if (fullyQualifiedNamesOfClassesToLoadByMockClassloader == null) {
			return new String[0];
		}
		return fullyQualifiedNamesOfClassesToLoadByMockClassloader.toArray(new String[fullyQualifiedNamesOfClassesToLoadByMockClassloader.size()]);
	}

	@Override
	public String[] getStaticInitializersToSuppress() {
		if (staticInitializersToSuppress == null) {
			return new String[0];
		}
		return staticInitializersToSuppress.toArray(new String[staticInitializersToSuppress.size()]);
	}

	@Override
	public void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String firstClass, String... additionalClasses) {
		fullyQualifiedNamesOfClassesToLoadByMockClassloader.add(firstClass);
		addFullyQualifiedNamesOfClassesToLoadByMockClassloader(additionalClasses);
	}

	@Override
	public void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes) {
		Collections.addAll(fullyQualifiedNamesOfClassesToLoadByMockClassloader, classes);
	}

	@Override
	public void addStaticInitializersToSuppress(String firstStaticInitializerToSuppress, String... additionalStaticInitializersToSuppress) {
		staticInitializersToSuppress.add(firstStaticInitializerToSuppress);
		addStaticInitializersToSuppress(additionalStaticInitializersToSuppress);
	}

	@Override
	public void addStaticInitializersToSuppress(String[] staticInitializersToSuppress) {
		Collections.addAll(this.staticInitializersToSuppress, staticInitializersToSuppress);
	}

	@Override
	public void setFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes) {
		fullyQualifiedNamesOfClassesToLoadByMockClassloader.clear();
		addFullyQualifiedNamesOfClassesToLoadByMockClassloader(classes);
	}

	@Override
	public void setStaticInitializersToSuppress(String[] staticInitializersToSuppress) {
		this.staticInitializersToSuppress.clear();
		addStaticInitializersToSuppress(staticInitializersToSuppress);
	}

}
