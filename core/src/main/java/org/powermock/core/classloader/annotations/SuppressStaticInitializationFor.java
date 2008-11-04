package org.powermock.core.classloader.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to suppress static initializers (constructors) for one or
 * more classes.
 * <p>
 * The reason why an annotation is needed for this is because we need to know at
 * <strong>load-time</strong> if the static constructor execution for this
 * class should be skipped or not. Unfortunately we cannot pass the class as the
 * value parameter to the annotation (and thus get type-safe values) because
 * then the class would be loaded before PowerMock could have suppressed its
 * constructor.
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SuppressStaticInitializationFor {
	String[] value() default "";
}
