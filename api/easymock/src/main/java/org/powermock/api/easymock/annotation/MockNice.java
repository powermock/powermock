package org.powermock.api.easymock.annotation;

import org.powermock.core.classloader.annotations.PowerMockListener;

import java.lang.annotation.*;

/**
 * This annotation can be placed on those fields in your test class that should
 * be mocked in a nice manner (i.e. by default allows all method calls and
 * returns appropriate empty values (0, {@code null} or {@code false}
 * )). This eliminates the need to setup and tear-down mocks manually which
 * minimizes repetitive test code and makes the test more readable. In order for
 * PowerMock to control the life-cycle of the mocks you must supply the
 * {@link PowerMockListener} annotation to the class-level of the test case. For
 * example when using the EasyMock API:
 * 
 * <pre>
 * &#064;PowerMockListener(EasyMockAnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;MockNice
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
 * &#064;PowerMockListener(EasyMockAnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;MockNice({&quot;getPerson&quot;, &quot;savePerson&quot;})
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
 */

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MockNice {
	String[] value() default "";
}
