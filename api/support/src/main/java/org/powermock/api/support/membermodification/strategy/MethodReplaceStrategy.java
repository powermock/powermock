/*
 * Copyright 2009 the original author or authors.
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
package org.powermock.api.support.membermodification.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Specifies the replace strategy for a method.
 */
public interface MethodReplaceStrategy {

	/**
	 * Replaces the method invocation with this method.
	 * <p>
	 * Note that both methods needs to be static.
	 * 
	 * @param method
	 *            The method call will be replaced by this method instead. Needs
	 *            to be static.
	 */
	void with(Method method);

	/**
	 * Replaces the method invocation with an invocation handler
	 * 
	 * @param invocationHandler
	 *            The invocation handler to replace the method call with.
	 */
	void with(InvocationHandler invocationHandler);
}
