package org.powermock.modules.junit4.legacy.internal.impl.testcaseworkaround;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.runners.BeforeAndAfterRunner;
import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.Whitebox;

/**
 * This class is needed because the test method runner creates a new instance of
 * a {@link TestIntrospector} in its constructor. The TestIntrospector needs to
 * be changed in order to support methods not annotated with Test to avoid
 * NPE's. This is really a bug in JUnit when using custom runners.
 */
public class PowerMockJUnit4LegacyTestMethodRunner extends TestMethodRunner {

	private final PowerMockJUnit4LegacyTestIntrospector testIntrospector;

	private final Method method;
	private final Description description;
	private final RunNotifier notifier;

	public PowerMockJUnit4LegacyTestMethodRunner(Object test, Method method, RunNotifier notifier, Description description) {
		super(test, method, notifier, description);
		this.method = method;
		this.description = description;
		this.notifier = notifier;
		testIntrospector = new PowerMockJUnit4LegacyTestIntrospector(test.getClass());
		Whitebox.setInternalState(this, "fTestIntrospector", testIntrospector, TestMethodRunner.class);
		Whitebox.setInternalState(this, "fTestIntrospector", testIntrospector, BeforeAndAfterRunner.class);
	}

	@Override
	public void run() {
		if (testIntrospector.isIgnored(method)) {
			notifier.fireTestIgnored(description);
			return;
		}
		notifier.fireTestStarted(description);
		try {
			long timeout = testIntrospector.getTimeout(method);
			if (timeout > 0)
				// The runWithTimeout method is private in the super class,
				// invoke it using reflection.
				Whitebox.invokeMethod(this, TestMethodRunner.class, "runWithTimeout", timeout);
			else
				Whitebox.invokeMethod(this, TestMethodRunner.class, "runMethod");
		} finally {
			notifier.fireTestFinished(description);
		}
	}

	@Override
	protected void runUnprotected() {
		try {
			executeMethodBody();
			if (expectsException())
				addFailure(new AssertionError("Expected exception: " + expectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual = e.getTargetException();
			if (!expectsException())
				addFailure(actual);
			else if (isUnexpected(actual)) {
				String message = "Unexpected exception, expected<" + expectedException().getName() + "> but was<" + actual.getClass().getName() + ">";
				addFailure(new Exception(message, actual));
			}
		} catch (Throwable e) {
			addFailure(e);
		}
	}

	private boolean isUnexpected(Throwable exception) {
		return !expectedException().isAssignableFrom(exception.getClass());
	}

	private boolean expectsException() {
		return expectedException() != null;
	}

	private Class<? extends Throwable> expectedException() {
		return testIntrospector.expectedException(method);
	}

}
