package demo.org.powermock.examples.tutorial.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom marker annotation to demonstrate how use PowerMock to set field
 * dependencies. In a real application these dependencies are set by an external
 * dependency injection framework using reflection.
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {

}
