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
package org.powermock.core.classloader.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation tells PowerMock to prepare all classes (except certain system
 * and test related classes) for test. Note that static initializers are
 * <i>not</i> removed.
 * <p>
 * The annotation should always be combined with the
 * {@code &#064;RunWith(PowerMockRunner.class)} if using junit 4.x or
 * 
 * <pre>
 * public static TestSuite suite() throws Exception {
 * 	return new PowerMockSuite(MyTestCase.class);
 * }
 * </pre>
 * 
 * if using junit3.
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PrepareEverythingForTest {
}
