/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.spi.support;

/**
 * A class that can be used as a substitution instead of mocking a particular
 * class. For example when mocking a new instance call you can fake the
 * constructor invocation by creating a mock object (A) for this class and
 * invoke the {@link #performSubstitutionLogic(Object...)} method instead with
 * the constructor arguments. The interception process must take care of doing
 * this. Also remember that behaviors such as replay and/or verify must be
 * performed on (A).
 * 
 */
public interface InvocationSubstitute<T> {

	public T performSubstitutionLogic(Object... arguments) throws Exception;
}
