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
package org.powermock.core.classloader.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.powermock.core.spi.PowerMockPolicy;

/**
 * A Mock Policy can be used to make it easier to unit test some code with
 * PowerMock in isolation from a certain framework. A mock policy implementation
 * can for example suppress some methods, suppress static initializers or
 * intercept method calls and change their return value (for example to return a
 * mock object) for certain framework or set of classes or interfaces.
 * <p>
 * A mock policy can for example be implemented to avoid writing repetitive
 * setup code for your tests. Say that you're using a framework X that in order
 * for you to test it requires that certain methods should always return a mock
 * implementation. Perhaps some static initializers must be suppressed as well.
 * Instead of copying this code between tests it would be a good idea to write a
 * reusable mock policy.
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MockPolicy {
	/**
	 * @return A list of mock policies that should be used in the test class.
	 */
	Class<? extends PowerMockPolicy>[] value();
}
