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

/**
 * Holds various keys that may be put into the MockRepository to store state.
 */
public class Keys {

	/**
	 * Key that can be used to set or get the current test instance in the mock
	 * repository.
	 */
	public static final String CURRENT_TEST_INSTANCE = "powermock.test.instance";

	/**
	 * Key that can be used to set or get the current test method in the mock
	 * repository.
	 */
	public static final String CURRENT_TEST_METHOD = "powermock.test.method";

	/**
	 * Key that can be used to set or get the current test method arguments in
	 * the mock repository.
	 */
	public static final String CURRENT_TEST_METHOD_ARGUMENTS = "powermock.test.arguments";

}
