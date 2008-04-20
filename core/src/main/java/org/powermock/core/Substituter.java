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
package org.powermock.core;

/**
 * 
 * Pairs a {@link SubstituteInvocation} with the number of times it should be
 * executed.
 * 
 * @author Johan Haleby
 */
public interface Substituter<T> {

	/**
	 * The {@link SubstituteInvocation} that should be invoked
	 * {@link #getExpectedInvocationCount()} times. The default is 1.
	 * 
	 * @return The {@link SubstituteInvocation}.
	 */
	SubstituteInvocation<T> getSubstituteInvocation();

	/**
	 * Get the number of times the substitute invocation should be invoked.
	 */
	int getExpectedInvocationCount();

	/**
	 * Get the number of times the substitute invocation actually was be
	 * invoked.
	 */
	int getActualInvocationCount();

	/**
	 * Set the number of times the substitute invocation actually has been
	 * invoked.
	 * 
	 * @param count
	 *            The number of times the substitute invocation actually has
	 *            been invoked
	 */
	void setActualInvocationCount(int count);

	/**
	 * Set the number of times the substitute invocation should be invoked.
	 * 
	 * @param count
	 *            The number of times the substitute invocation should be
	 *            invoked.
	 */
	void setExpectedInvocationCount(int count);

}
