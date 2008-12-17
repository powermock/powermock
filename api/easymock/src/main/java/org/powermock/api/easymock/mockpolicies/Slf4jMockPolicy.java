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
 * An slf4j mock policy. This mock policy deals with solving slf4j related
 * mocking issues. It takes care of loading all concerned slf4j and log4j
 * classes through the correct class-loader and automatically prepares and
 * injects logger instances. This policy does the following:
 * <ol>
 * <li>Prepares <tt>org.apache.log4j.Appender</tt>,
 * <tt>org.slf4j.LoggerFactory</tt> and
 * <tt>org.apache.log4j.xml.DOMConfigurator</tt> for testing.</li>
 * <li>All calls to the <tt>org.slf4j.LoggerFactory#getLogger(..)</tt> methods
 * are intercepted and returns a nice mock of type <tt>org.slf4j.Logger</tt>.</li>
 * </ol>
 */
public class Slf4jMockPolicy extends AbstractEasyMockLogPolicyBase {

	protected String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		return new String[] { "org.apache.log4j.Appender", "org.slf4j.LoggerFactory", "org.apache.log4j.xml.DOMConfigurator" };
	}

	@Override
	protected String getLogFrameworkName() {
		return "slf4j";
	}

	@Override
	protected String getLoggerClassToMock() {
		return "org.slf4j.Logger";
	}

	@Override
	protected String getLoggerFactoryClassName() {
		return "org.slf4j.LoggerFactory";
	}

	@Override
	protected String getLoggerMethodName() {
		return "getLogger";
	}
}