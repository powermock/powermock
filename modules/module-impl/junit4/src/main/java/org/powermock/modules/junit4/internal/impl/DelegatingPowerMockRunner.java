/*
 * Copyright 2013 the original author or authors.
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

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.PowerMockRunnerDelegate.SinceJUnit_4_5;
import org.powermock.modules.junit4.common.internal.PowerMockJUnitRunnerDelegate;
import org.powermock.modules.junit4.common.internal.impl.JUnitVersion;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import org.powermock.tests.utils.PowerMockTestNotifier;
import org.powermock.tests.utils.impl.PowerMockTestNotifierImpl;

public class DelegatingPowerMockRunner extends Runner
implements PowerMockJUnitRunnerDelegate, Filterable {

    private final String testClassName;
    private final Runner delegate;
    private final ClassLoader testClassLoader;
    private final Method[] testMethods;
    private final PowerMockTestNotifier powerMockTestNotifier;

    public DelegatingPowerMockRunner(Class<?> klass) throws Throwable {
        this(klass, null);
    }

    public DelegatingPowerMockRunner(Class<?> klass, String[] methodsToRun)
    throws Throwable {
        this(klass, methodsToRun, null);
    }

    public DelegatingPowerMockRunner(
            Class<?> klass, String[] methodsToRun, PowerMockTestListener[] listeners)
    throws Exception {
        testClassName = klass.getName();
        delegate = createDelegate(klass);
        testClassLoader = klass.getClassLoader();
        testMethods = determineTestMethods(klass, methodsToRun);
        powerMockTestNotifier = new PowerMockTestNotifierImpl(listeners == null ? new PowerMockTestListener[0] : listeners);
    }

    private static Method[] determineTestMethods(
            Class<?> testClass, String[] testMethodNames) {
        List<Method> testMethods = new ArrayList<Method>();
        for (Method m : testClass.getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                testMethods.add(m);
            }
        }
        if (testMethods.isEmpty()) {
            for (String testMethodName : testMethodNames) {
                try {
                    testMethods.add(testClass.getMethod(testMethodName));
                } catch (NoSuchMethodException ignore) {
                    System.err.println(ignore.getMessage());
                }
            }
        }
        return testMethods.toArray(new Method[testMethods.size()]);
    }

    private static Runner createDelegate(final Class<?> testClass)
    throws Exception {
        /*
         * Because of the mockito integration it seems like it is necessary to
         * set context classloader during delegate creation ...
         */
        return withContextClassLoader(testClass.getClassLoader(),
                new Callable<Runner>() {
            @Override
            public Runner call() throws Exception {
                try {
                    return Whitebox.invokeConstructor(
                            testClass.isAnnotationPresent(PowerMockRunnerDelegate.class)
                            ? testClass.getAnnotation(PowerMockRunnerDelegate.class).value()
                            : PowerMockRunnerDelegate.DefaultJUnitRunner.class,
                            new Class[] {Class.class},
                            new Object[] {testClass});
                } catch (ConstructorNotFoundException rootProblem) {
                    if (testClass.isAnnotationPresent(PowerMockRunnerDelegate.class)
                            && JUnitVersion.isGreaterThanOrEqualTo("4.5")) {
                        try {
                            return Whitebox.invokeConstructor(testClass.getAnnotation(PowerMockRunnerDelegate.class).value(),
                                    SinceJUnit_4_5.runnerAlternativeConstructorParams(),
                                    new Object[] {
                                        testClass,
                                        SinceJUnit_4_5.newRunnerBuilder()
                                    });
                        } catch (ConstructorNotFoundException ignoredWorkAroundFailure) {
                        }
                    }
                    throw rootProblem;
                }
            }
        });
    }

    private static <T> T withContextClassLoader(
            ClassLoader loader, Callable<T> callable) throws Exception {
        final ClassLoader originalClassLoaderBackup =
                Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            return callable.call();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoaderBackup);
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        try {
            withContextClassLoader(testClassLoader, new Callable<Void>() {
                @Override
                public Void call() {
                    PowerMockRunNotifier powerNotifier = new PowerMockRunNotifier(
                            notifier, powerMockTestNotifier, testMethods);
                    try {
                        GlobalNotificationBuildSupport.prepareTestSuite(
                                testClassName, powerNotifier);
                        delegate.run(powerNotifier);
                    } finally {
                        GlobalNotificationBuildSupport
                                .closePendingTestSuites(powerNotifier);                        
                    }
                    return null;
                }
            });
        } catch (Exception cannotHappen) {
            throw new Error(cannotHappen);
        }
    }

    @Override
    public Description getDescription() {
        return delegate.getDescription();
    }

    @Override
    public int getTestCount() {
        return delegate.testCount();
    }

    @Override
    public Class<?> getTestClass() {
        return getDescription().getTestClass();
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        if (this.delegate instanceof Filterable) {
            ((Filterable) this.delegate).filter(filter);
        }
    }
}
