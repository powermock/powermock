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

import org.powermock.core.IndicateReloadClass;

import java.lang.annotation.*;

/**
 * This annotation tells PowerMock to prepare certain classes for testing.
 * Classes needed to be defined using this annotation are typically those that
 * needs to be byte-code manipulated. This includes final classes, classes with
 * final, private, static or native methods that should be mocked and also
 * classes that should be return a mock object upon instantiation.
 * <p>
 * This annotation can be placed at both test classes and individual test
 * methods. If placed on a class all test methods in this test class will be
 * handled by PowerMock (to allow for testability). To override this behavior
 * for a single method just place a <code>&#064;PrepareForTest</code> annotation
 * on the specific test method. This is useful in situations where for example
 * you'd like to modify class X in test method A but in test method B you want X
 * to be left intact. In situations like this you place a
 * <code>&#064;PrepareForTest</code> on method B and exclude class X from the
 * {@link #value()} list.
 * <p>
 * Sometimes you need to prepare inner classes for testing, this can be done by
 * suppling the fully-qualified name of the inner-class that should be mocked to
 * the {@link #fullyQualifiedNames()} list.
 * 
 * <p>
 * The annotation should always be combined with the
 * <code>&#064;RunWith(PowerMockRunner.class)</code> if using junit 4.x or
 * 
 * <pre>
 * public static TestSuite suite() throws Exception {
 * 	return new PowerMockSuite(MyTestCase.class);
 * }
 * </pre>
 * 
 * if using junit3.
 * <p>
 * The difference between this annotation and the {@link PrepareForTest}
 * annotation is that this annotation only modifies the specified classes
 * whereas the {@link PrepareForTest} annotation manipulates the full class
 * hierarchy. This annotation is recommend if you want full control over which
 * classes that are byte-code manipulated.
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PrepareOnlyThisForTest {
	Class<?>[] value() default IndicateReloadClass.class;

	String[] fullyQualifiedNames() default "";
}
