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
package org.powermock.core.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * The purpose of a method invocation control is to invoke a proxy to simulate a
 * method call. It also has functionality to replay and verify mocks (which may
 * not be needed for certain invocation controls) and to check whether a certain
 * method is mocked or not.
 * 
 */
public interface MethodInvocationControl extends InvocationHandler, DefaultBehavior {

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