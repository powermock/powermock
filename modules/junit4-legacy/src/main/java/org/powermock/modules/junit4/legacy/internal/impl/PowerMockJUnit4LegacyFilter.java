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
package org.powermock.modules.junit4.legacy.internal.impl;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

/**
 * A JUnit filter that will filter the methods specified in String[] passed to
 * the constructor (i.e. these are the only tests that will be run for a
 * particular test class).
 * 
 * @author Johan Haleby
 */
public class PowerMockJUnit4LegacyFilter extends Filter {

	private final String[] methodNamesToRun;

	public PowerMockJUnit4LegacyFilter(String[] methodNamesToRun) {
		this.methodNamesToRun = methodNamesToRun;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String describe() {
		return methodNamesToRun.length + " tests.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRun(Description description) {
		boolean shouldRun = false;
		for (String testMethodName : methodNamesToRun) {
			if (testMethodName.equals(extractMethodName(description))) {
				shouldRun = true;
				break;
			}
		}
		return shouldRun;
	}

	private String extractMethodName(Description description) {
		final String displayName = description.getDisplayName();
		/*
		 * The test method name is the string to the left of the first
		 * parenthesis.
		 * testSayPrivateStatic(org.powermock.modules.junit4.legacy.singleton.StupidSingletonTest)
		 * is an example of a display name.
		 */
		final int indexOfParenthesis = displayName.indexOf('(');
		if (indexOfParenthesis == -1) {
			throw new RuntimeException(
					"Internal error: Failed to find the test method name.");
		}
		return displayName.substring(0, indexOfParenthesis);
	}
}
