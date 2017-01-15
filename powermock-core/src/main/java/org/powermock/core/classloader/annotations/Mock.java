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
 * This annotation can be placed on those fields in your test class that should
 * be mocked. This eliminates the need to setup and tear-down mocks manually
 * which minimizes repetitive test code and makes the test more readable. In
 * order for PowerMock to control the life-cycle of the mocks you must supply
 * the {@link PowerMockListener} annotation to the class-level of the test case.
 * For example:
 * 
 * <pre>
 * ...
 * &#064;PowerMockListener(AnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;Mock
 * 	private PersonDao personDaoMock;
 * 
 * 	private PersonService classUnderTest;
 * 
 * 	&#064;Before
 * 	public void setUp() {
 * 		classUnderTest = new PersonService(personDaoMock);
 * 	}
 *  ...
 * }
 * </pre>
 * <p>
 * 
 * Note that you can also create partial mocks by using the annotation. Let's
 * say that the PersonService has a method called "getPerson" and another method
 * called "savePerson" and these are the only two methods that you'd like to
 * mock. Rewriting the previous example to accommodate this will give us the
 * following test:
 * 
 * <pre>
 * ...
 * &#064;PowerMockListener(EasyMockAnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;Mock({&quot;getPerson&quot;, &quot;savePerson&quot;})
 * 	private PersonDao personDaoMock;
 * 
 * 	private PersonService classUnderTest;
 * 
 * 	&#064;Before
 * 	public void setUp() {
 * 		classUnderTest = new PersonService(personDaoMock);
 * 	}
 *  ...
 * }
 * </pre>
 * <p>
 * 
 * @deprecated Use Mock annotation in respective extension API instead. This
 *             annotation will be removed in an upcoming release.
 */

@Deprecated
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {
	String[] value() default "";
}
