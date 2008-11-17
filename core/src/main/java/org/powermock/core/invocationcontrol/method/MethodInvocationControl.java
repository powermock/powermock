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
package org.powermock.core.invocationcontrol.method;

import java.lang.reflect.Method;
import java.util.Set;

import org.easymock.internal.MockInvocationHandler;

/**
 * The purpose of an invocation control is to determine whether a certain method
 * is mocked or not. This is determined by pairing up an InvocationHandler (that
 * is associated with an entire object) and the Methods for this object that
 * should be mocked.
 * 
 * @author Johan Haleby
 */
public interface MethodInvocationControl {

	/**
	 * Get the methods that are mocked for this invocation control. If the
	 * returned Set is empty, <i>all</i> methods are considered to be mocked.
	 * 
	 * @return A Set of methods that are mocked. The set can never be
	 *         <code>null</code>.
	 */
	public Set<Method> getMockedMethods();

	/**
	 * Get the invocation handler.
	 * 
	 * @return The invocation handler.
	 */
	public MockInvocationHandler getInvocationHandler();

	/**
	 * Determine whether a certain method is mocked by this Invocation Control.
	 * 
	 * @param method
	 *            The method that should be checked.
	 * @return <code>true</code> if the method is mocked, <code>false</code>
	 *         otherwise.
	 */
	public boolean isMocked(Method method);
}
