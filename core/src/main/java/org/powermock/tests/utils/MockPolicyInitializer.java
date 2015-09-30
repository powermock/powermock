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
package org.powermock.tests.utils;

import org.powermock.core.classloader.MockClassLoader;

/**
 * A Mock Policy initializer takes care of initializing the behavior defined by
 * the mock policies.
 */
public interface MockPolicyInitializer {

	/**
	 * Initializes the mock policies for a given class loader. Note that this
	 * method must <b><i>not</i></b> be called from the class loader (
	 * <code>classLoader</code>) that you pass in to this method.
	 * <p>
	 * Note that if the class-loader is not an instance of
	 * {@link MockClassLoader} this method will return silently.
	 */
	void initialize(ClassLoader classLoader);

	/**
	 * @return <code>true</code> if a client needs to perform initialization for
	 *         this {@link MockPolicyInitializer}, <code>false</code> otherwise.
	 */
	boolean needsInitialization();

	/**
	 * @return <code>true</code> if the class with the fully-qualified name of
	 *         <code>fullyQualifiedClassName</code> was prepared for testing by
	 *         this mock policy initializer.
	 */
	boolean isPrepared(String fullyQualifiedClassName);

	/**
	 * Re executes the {@link MockPolicy#} of all the policies for a given class
	 * loader. This method must be called after a call to
	 * {@link MockPolicyInitializer#initialize(ClassLoader)} on the same class
	 * loader.
	 * <p>
	 * Note that if the class-loader is not an instance of
	 * {@link MockClassLoader} this method will return silently.
	 */
	void refreshPolicies(ClassLoader classLoader);
}
