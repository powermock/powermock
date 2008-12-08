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
package org.powermock.tests.utils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * A Mock Policy handler takes care of initializing and returning methods,
 * instances and mocks that should be
 */
public interface MockPolicyHandler {
	/**
	 * @return The fully-qualified names to all types that should be loaded by
	 *         the mock classloader. Can be executed by any classloader.
	 */
	String[] getClassesToBeLoadedByMockClassloader();

	/**
	 * Initializes all classes that should have their static constructors
	 * suppressed. The fully-qualified names of these classes are also returned.
	 * Can be executed by any classloader.
	 * 
	 * @return The fully-qualified names of the classes that should have their
	 *         static initializers suppressed.
	 */
	String[] initStaticSuppression();

	/**
	 * Initializes all substitute return values and also returns an unmodifiable
	 * map of all method-object pairs the were initialized. <b><u>MUST</u> be
	 * loaded by the same classloader that executes the test!</b>
	 */
	Map<Method, Object> initSubstituteReturnValues();

	/**
	 * @return <code>true</code> if the class with the fully-qualified name of
	 *         <code>fullyQualifiedClassName</code> was prepared for testing by
	 *         this policy handler.
	 */
	boolean isPrepared(String fullyQualifiedClassName);
}
