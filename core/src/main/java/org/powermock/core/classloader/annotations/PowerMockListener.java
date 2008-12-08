package org.powermock.core.classloader.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.powermock.core.spi.PowerMockTestListener;

/**
 * The PowerMock listener annotation can be used to tell PowerMock which
 * listeners should be instantiated and invoked during a test. A listener is
 * invoked according to the events specified in the
 * {@link PowerMockTestListener} interface.
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PowerMockListener {
	Class<? extends PowerMockTestListener>[] value();
}
