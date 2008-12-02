package org.powermock.core.classloader.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be placed on those fields in your test class should be
 * mocked. This eliminates the need to setup and tear-down mocks manually which
 * minimizes repetitive test code and makes the test case more readable. In
 * order for PowerMock to control the life-cycle of the mocks you must supply
 * the {@link PowerMockListener} annotation to the class-level of the test case.
 * For example when using the EasyMock API:
 * 
 * <pre>
 * &#064;PowerMockListener(EasyMockAnnotationEnabler.class)
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
 */

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Mock {
	String[] value() default "";
}
