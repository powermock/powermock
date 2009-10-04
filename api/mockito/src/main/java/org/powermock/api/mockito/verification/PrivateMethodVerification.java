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
package org.powermock.api.mockito.verification;

import java.lang.reflect.Method;

import org.mockito.Mockito;

public interface PrivateMethodVerification {

	/**
	 * Verify calls to private methods without having to specify the method
	 * name. The method will be looked up using the parameter types (if
	 * possible).
	 * 
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public void invoke(Object... arguments) throws Exception;

	/**
	 * Verify calls to the specific method.
	 * 
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public WithOrWithoutVerifiedArguments invoke(Method method) throws Exception;

	/**
	 * Verify a private method call by specifying the method name of the method
	 * to verify.
	 * 
	 * @see {@link Mockito#invoke(Object)}
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public void invoke(String methodToVerify, Object... arguments) throws Exception;
}
