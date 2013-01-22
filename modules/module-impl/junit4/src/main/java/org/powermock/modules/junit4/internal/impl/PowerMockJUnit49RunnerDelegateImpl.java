package org.powermock.modules.junit4.internal.impl;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestMethod;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Statement;
import org.powermock.core.spi.PowerMockTestListener;


/**
 * Extends the functionality of {@link PowerMockJUnit47RunnerDelegateImpl} to enable the usage of
 * {@link TestRule}.
 */
@SuppressWarnings( "deprecation" )
public class PowerMockJUnit49RunnerDelegateImpl extends PowerMockJUnit47RunnerDelegateImpl {

    public PowerMockJUnit49RunnerDelegateImpl( Class<?> klass, String[] methodsToRun, PowerMockTestListener[] listeners ) throws InitializationError {
        super( klass, methodsToRun, listeners );
    }

    public PowerMockJUnit49RunnerDelegateImpl( Class<?> klass, String[] methodsToRun ) throws InitializationError {
        super( klass, methodsToRun );
    }

    public PowerMockJUnit49RunnerDelegateImpl( Class<?> klass ) throws InitializationError {
        super( klass );
    }

    @Override
    protected PowerMockJUnit47MethodRunner createPowerMockRunner( final Object testInstance,
                                                                  final TestMethod testMethod,
                                                                  RunNotifier notifier,
                                                                  Description description,
                                                                  final boolean extendsFromTestCase ) {
        return new PowerMockJUnit49MethodRunner( testInstance, testMethod, notifier, description, extendsFromTestCase );
    }

    protected class PowerMockJUnit49MethodRunner extends PowerMockJUnit47MethodRunner {

        private Description description;

        protected PowerMockJUnit49MethodRunner( Object testInstance,
                                                TestMethod method,
                                                RunNotifier notifier,
                                                Description description,
                                                boolean extendsFromTestCase ) {
            super( testInstance, method, notifier, description, extendsFromTestCase );
            this.description = description;
        }

        @Override
        protected Statement applyRuleToLastStatement(final Method method, final Object testInstance, Field field,
                final LastRuleTestExecutorStatement lastStatement) throws IllegalAccessException {
            final Object fieldValue = field.get(testInstance);
            final Statement statement;
            if (fieldValue instanceof MethodRule) {
                // the MethodRule is known by junit 4.9 -> delegate to super-class
                statement = super.applyRuleToLastStatement(method, testInstance, field, lastStatement);
            } else if (fieldValue instanceof TestRule){
                TestRule rule = (TestRule) fieldValue;
                statement = rule.apply(lastStatement, description);
            } else {
                throw new IllegalStateException("Can only handle MethodRule and TestRule");
            }
            return statement;
        }
    }
}
