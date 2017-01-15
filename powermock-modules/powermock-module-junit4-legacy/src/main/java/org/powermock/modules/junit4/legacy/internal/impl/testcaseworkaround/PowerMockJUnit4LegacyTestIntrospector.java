package org.powermock.modules.junit4.legacy.internal.impl.testcaseworkaround;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.TestIntrospector;

import java.lang.reflect.Method;

/**
 * A custom {@link TestIntrospector} that supports methods not annotated by the
 * Test annotation but should still be executed in the test case. This is
 * actually a workaround for the JUnit 4 test runner when the test case extends
 * from the {@code TestCase} class.
 */
public class PowerMockJUnit4LegacyTestIntrospector extends TestIntrospector {

	private static final long NO_TIMEOUT = 0L;

	public PowerMockJUnit4LegacyTestIntrospector(Class<?> testClass) {
		super(testClass);
	}

	@SuppressWarnings("all")
	public long getTimeout(Method method) {
		Test annotation = method.getAnnotation(Test.class);
		long timeout = annotation == null ? NO_TIMEOUT : annotation.timeout();
		return timeout;
	}

	@SuppressWarnings("all")
	public Class<? extends Throwable> expectedException(Method method) {
		Test annotation = method.getAnnotation(Test.class);
		if (annotation == null || annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}
}
