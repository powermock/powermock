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

import org.powermock.api.easymock.internal.mockpolicies.AbstractEasyMockLogPolicyBase;


/**
 * A Jakarta Commons-Logging (JCL) mock policy. This mock policy deals with
 * solving JCL related mocking issues. It takes care of loading all concerned
 * JCL classes through the correct class-loader and automatically prepares and
 * injects logger instances. This policy does the following:
 * <ol>
 * <li>Prepares all classes in the <tt>org.apache.commons.logging</tt> for test
 * as well as <tt>org.apache.log4j.Appender</tt> and
 * <tt>org.apache.log4j.xml.DOMConfigurator</tt>.</li>
 * <li>All calls to the
 * <tt>org.apache.commons.logging.LogFactory#getLog(..)</tt> methods are
 * intercepted and returns a nice mock of type
 * <tt>org.apache.commons.logging.Log</tt>.</li>
 * </ol>
 */
public class JclMockPolicy extends AbstractEasyMockLogPolicyBase {

	/**
	 * Loads all log4j classes with the mock classloader.
	 */
	public String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		return new String[] { "org.apache.commons.logging.", "org.apache.log4j.Appender", "org.apache.log4j.xml.DOMConfigurator" };
	}

	@Override
	protected String getLogFrameworkName() {
		return "commons-logging";
	}

	@Override
	protected String getLoggerClassToMock() {
		return "org.apache.commons.logging.Log";
	}

	@Override
	protected String getLoggerFactoryClassName() {
		return "org.apache.commons.logging.LogFactory";
	}

	@Override
	protected String getLoggerMethodName() {
		return "getLog";
	}
}