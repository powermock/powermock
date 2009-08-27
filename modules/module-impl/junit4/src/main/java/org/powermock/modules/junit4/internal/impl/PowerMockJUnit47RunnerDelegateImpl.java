package org.powermock.modules.junit4.internal.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Rule;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestMethod;
import org.junit.rules.MethodRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.reflect.Whitebox;

/**
 * Extends the functionality of {@link PowerMockJUnit44RunnerDelegateImpl} to
 * enable the usage of rules.
 */
@SuppressWarnings("deprecation")
public class PowerMockJUnit47RunnerDelegateImpl extends PowerMockJUnit44RunnerDelegateImpl {
	public PowerMockJUnit47RunnerDelegateImpl(Class<?> klass, String[] methodsToRun, PowerMockTestListener[] listeners) throws InitializationError {
		super(klass, methodsToRun, listeners);
	}

	public PowerMockJUnit47RunnerDelegateImpl(Class<?> klass, String[] methodsToRun) throws InitializationError {
		super(klass, methodsToRun);
	}

	public PowerMockJUnit47RunnerDelegateImpl(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected PowerMockJUnit44MethodRunner createPowerMockRunner(final Object testInstance, final TestMethod testMethod, RunNotifier notifier,
			Description description, final boolean extendsFromTestCase) {
		return new PowerMockJUnit47MethodRunner(testInstance, testMethod, notifier, description, extendsFromTestCase);
	}

	protected class PowerMockJUnit47MethodRunner extends PowerMockJUnit44MethodRunner {

		protected PowerMockJUnit47MethodRunner(Object testInstance, TestMethod method, RunNotifier notifier, Description description,
				boolean extendsFromTestCase) {
			super(testInstance, method, notifier, description, extendsFromTestCase);
		}

		@Override
		public void executeTest(final Method method, final Object testInstance, final Runnable test) {
			Set<Field> rules = Whitebox.getFieldsAnnotatedWith(testInstance, Rule.class);
			if (rules.isEmpty()) {
				executeTestInSuper(method, testInstance, test);
			} else {
				for (Field field : rules) {
					try {
						MethodRule rule = (MethodRule) field.get(testInstance);
						rule.apply(new Statement() {

							@Override
							public void evaluate() throws Throwable {
								executeTestInSuper(method, testInstance, test);
							}
						}, new FrameworkMethod(method), testInstance);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		private void executeTestInSuper(final Method method, final Object testInstance, final Runnable test) {
			super.executeTest(method, testInstance, test);
		}
	}
}
