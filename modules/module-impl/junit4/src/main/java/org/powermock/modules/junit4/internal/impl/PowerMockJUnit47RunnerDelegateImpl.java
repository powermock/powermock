/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.junit4.internal.impl;

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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Extends the functionality of {@link PowerMockJUnit44RunnerDelegateImpl} to
 * enable the usage of rules.
 */
@SuppressWarnings("deprecation")
public class PowerMockJUnit47RunnerDelegateImpl extends PowerMockJUnit44RunnerDelegateImpl {
    public boolean hasRules;

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

        private Throwable potentialTestFailure;

        protected PowerMockJUnit47MethodRunner(Object testInstance, TestMethod method, RunNotifier notifier, Description description,
                                               boolean extendsFromTestCase) {
            super(testInstance, method, notifier, description, extendsFromTestCase);
        }

        @Override
        public void executeTest(final Method method, final Object testInstance, final Runnable test) {
            // We change the context classloader to the current CL in order for the Mockito
            // framework to load it's plugins (such as MockMaker) correctly.
            final ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            final Set<Field> rules;
            try {
                rules = Whitebox.getFieldsAnnotatedWith( testInstance, Rule.class );
            } finally {
                Thread.currentThread().setContextClassLoader(originalCL);
            }
            hasRules = !rules.isEmpty();
            if (!hasRules) {
                executeTestInSuper(method, testInstance, test);
            } else {
                int processedFields = 0;
                for (Field field : rules) {
                    processedFields++;
                    try {
                        LastRuleTestExecutorStatement lastStatement = new LastRuleTestExecutorStatement(processedFields, rules.size(), test, testInstance, method);
                        Statement statement = applyRuleToLastStatement(method,
                                testInstance, field, lastStatement);
                        statement.evaluate();
                    } catch (Throwable e) {
                        /*
                         * No rule could handle the exception thus we need to
                         * add it as a failure.
                         */
                        super.handleException(testMethod, potentialTestFailure == null ? e : potentialTestFailure);
                    }
                }
            }
        }

        protected Statement applyRuleToLastStatement(final Method method, final Object testInstance, Field field,
                                                     final LastRuleTestExecutorStatement lastStatement) throws IllegalAccessException {
            MethodRule rule = (MethodRule) field.get(testInstance);
            Statement statement = rule.apply(lastStatement, new FrameworkMethod(method), testInstance);
            return statement;
        }

        /**
         * Since a JUnit 4.7 rule may potentially deal with "unexpected"
         * exceptions we cannot handle the exception before the rule has been
         * completely evaluated. Thus we just store the exception here and
         * rethrow it after the test method has finished executing. In that way
         * the rule may get a chance to handle the exception appropriately.
         */
        @Override
        protected void handleException(TestMethod testMethod, Throwable actualFailure) {
            if (hasRules) {
                potentialTestFailure = actualFailure;
            } else {
                super.handleException(testMethod, actualFailure);
            }
        }

        private void executeTestInSuper(final Method method, final Object testInstance, final Runnable test) {
            super.executeTest(method, testInstance, test);
        }

        protected final class LastRuleTestExecutorStatement extends Statement {
            private final Runnable test;
            private final Object testInstance;
            private final Method method;
            private final int noOfRules;
            private final int currentRule;

            private LastRuleTestExecutorStatement(int currentRuleNumber, int noOfRules, Runnable test, Object testInstance, Method method) {
                this.currentRule = currentRuleNumber;
                this.noOfRules = noOfRules;
                this.test = test;
                this.testInstance = testInstance;
                this.method = method;
            }

            @Override
            public void evaluate() throws Throwable {
                if (currentRule == noOfRules) {
                    executeTestInSuper(method, testInstance, test);
                    if (potentialTestFailure != null) {
                        // Rethrow the potential failure caught in the test.
                        throw potentialTestFailure;
                    }
                }
            }
        }
    }
}
