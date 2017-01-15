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

import java.lang.annotation.*;

/**
 * This annotation tells PowerMock to defer the loading of classes with the
 * names supplied to {@link #value()} to the system classloader.
 * <p>
 * For example suppose you'd like to defer the loading of all classes in the
 * <tt>org.myproject</tt> package and all its sub-packages but you still like to
 * prepare "MyClass" for test. Then you do like this:
 * 
 * <pre>
 * &#064;PowerMockIgnore(&quot;org.myproject.*&quot;)
 * &#064;PrepareForTest(MyClass.class)
 * &#064;RunWith(PowerMockRunner.class)
 * public class MyTest {
 * ...
 * }
 * 
 * </pre>
 * 
 * This is useful in situations when you have e.g. a test/assertion utility
 * framework (such as something similar to Hamcrest) whose classes must be
 * loaded by the same classloader as EasyMock, JUnit and PowerMock etc.
 * <p>
 * Note that the {@link PrepareForTest} and {@link PrepareOnlyThisForTest} will
 * have precedence over this annotation. This annotation will have precedence
 * over the {@link PrepareEverythingForTest} annotation.
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PowerMockIgnore {
    String[] value() default "";
}
