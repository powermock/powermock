package org.powermock.modules.junit4.legacy.internal.impl.testcaseworkaround;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.internal.runners.BeforeAndAfterRunner;
import org.junit.internal.runners.TestIntrospector;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.reflect.Whitebox;

/**
 * This class is needed because the test method runner creates a new instance of
 * a {@link TestIntrospector} in its constructor. The TestIntrospector needs to
 * be changed in order to support methods not annotated with Test to avoid
 * NPE's.
 * <p>
 * This class also executes the setUp and tearDown methods if the test case
 * extends TestCase.
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

			// Execute the the setUp method if needed
			executeMethodInTestInstance("setUp");
			if (timeout > 0) {
				// The runWithTimeout method is private in the super class,
				// invoke it using reflection.
				Whitebox.invokeMethod(this, TestMethodRunner.class, "runWithTimeout", timeout);
			} else {
				Whitebox.invokeMethod(this, TestMethodRunner.class, "runMethod");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				executeMethodInTestInstance("tearDown");
			} finally {
				notifier.fireTestFinished(description);
			}
		}
	}

	/**
	 * This method takes care of executing a method in the test object <i>if</i>
	 * this object extends from {@link TestCase}. It can be used to execute the
	 * setUp and tearDown methods for example.
	 */
	private void executeMethodInTestInstance(String methodName) {
		if (TestCase.class.isAssignableFrom(Whitebox.getInternalState(this, "fTest").getClass())) {
			Object object = Whitebox.getInternalState(this, "fTest");
			try {
				if (object != null) {
					Whitebox.invokeMethod(object, methodName);
				}
			} catch (Throwable e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new RuntimeException(e);
			}
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
