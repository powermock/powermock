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
package org.powermock.api.easymock.mockpolicies;


/**
 * A log4j mock policy. This mock policy deals with solving log4j related
 * mocking issues. It takes care of loading all concerned log4j classes through
 * the correct class-loader and automatically prepares and injects logger
 * instances. This policy does the following:
 * <ol>
 * <li>Prepares all log4j classes and interfaces for testing.</li>
 * <li>All calls to the <tt>org.apache.log4j.Logger#getLogger(..)</tt> methods
 * are intercepted and returns a nice mock of type
 * <tt>org.apache.log4j.Logger</tt>.</li>
 * </ol>
 */
public class Log4jMockPolicy extends AbstractEasyMockLogPolicyBase {

	private static final String LOGGER_CLASS = "org.apache.log4j.Logger";

	/**
	 * Loads all log4j classes with the mock classloader.
	 */
	@Override
	protected String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		return new String[] { "org.apache.log4j.*" };
	}

	@Override
	protected String getLogFrameworkName() {
		return "log4j";
	}

	@Override
	protected String getLoggerClassToMock() {
		return LOGGER_CLASS;
	}

	@Override
	protected String getLoggerFactoryClassName() {
		return LOGGER_CLASS;
	}

	@Override
	protected String getLoggerMethodName() {
		return "getLogger";
	}
}