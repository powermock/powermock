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

/**
 * Specifies the return value when stubbing a method.
 */
public interface MethodStubStrategy<T> {

	/**
	 * Stubs the method to return the specified returnValue.
	 * 
	 * @param returnValue
	 *            The value that will be returned.
	 * @deprecated Since version 1.4.1, use {@link #toReturn(Object)} instead.
	 */
	void andReturn(T returnValue);

	/**
	 * Stubs the method to return the specified returnValue.
     *
	 * @param returnValue
	 *            The value that will be returned.
	 */
	void toReturn(T returnValue);

	/**
	 * Stubs the method to throw the specified throwable.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	void toThrow(Throwable throwable);
}
