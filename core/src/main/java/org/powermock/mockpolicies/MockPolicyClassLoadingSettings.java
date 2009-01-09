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
package org.powermock.mockpolicies;

/**
 * Contains class-loading related settings. PowerMock uses the information
 * stored in this object to configure it's mock classloader to allow for
 * testability.
 * <p>
 * Since mock policies can be chained subsequent policies can override behavior
 * of a previous policy. To avoid accidental overrides it's recommended
 * <i>add</i> behavior instead of <i>setting</i> behavior since the latter
 * overrides all previous configurations.
 */
public interface MockPolicyClassLoadingSettings {

	/**
	 * Set which static initializers to suppress. Note that this overrides all
	 * previous configurations.
	 */
	void setStaticInitializersToSuppress(String[] staticInitializersToSuppress);

	/**
	 * Add static initializers to suppress.
	 */
	void addStaticInitializersToSuppress(String firstStaticInitializerToSuppress, String... additionalStaticInitializersToSuppress);

	/**
	 * Add static initializers to suppress.
	 */
	void addStaticInitializersToSuppress(String[] staticInitializersToSuppress);

	/**
	 * Set which types that should be loaded (and possibly modified) by the mock
	 * classloader. Note that this overrides all previous configurations.
	 */
	void setFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes);

	/**
	 * Add types that should be loaded (and possibly modified) by the mock
	 * classloader.
	 */
	void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String firstClass, String... additionalClasses);

	/**
	 * Add types that should be loaded (and possibly modified) by the mock
	 * classloader.
	 */
	void addFullyQualifiedNamesOfClassesToLoadByMockClassloader(String[] classes);

	/**
	 * @return The fully-qualified names to the classes whose static
	 *         initializers that should be suppressed.
	 */
	String[] getStaticInitializersToSuppress();

	/**
	 * @return The fully-qualified names to all types that should be loaded by
	 *         the mock classloader.
	 */
	String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader();
}
