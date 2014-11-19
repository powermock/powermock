/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.junit4.internal.impl;

import java.lang.reflect.Method;
import java.util.LinkedList;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport;
import org.powermock.tests.utils.PowerMockTestNotifier;

import static org.powermock.reflect.Whitebox.invokeMethod;

/**
 * Wraps JUnit's RunNotifier to make sure that the PowerMock-specific
 * instances of {@link PowerMockTestListener} will also be informed.
 * It is stateful and (hopefully) thread-safe.
 *
 * @see PowerMockTestListener
 */
class PowerMockRunNotifier extends RunNotifier
implements GlobalNotificationBuildSupport.Callback {

    private Class<?> suiteClass = null;
    private final Thread motherThread = Thread.currentThread();
    private final RunNotifier junitRunNotifier;
    private final PowerMockTestNotifier powerMockTestNotifier;
    private final Method[] testMethods;
    private final LinkedList<Object> pendingTestInstancesOnMotherThread =
            new LinkedList<Object>();
    private final ThreadLocal<NotificationBuilder> notificationBuilder =
            new ThreadLocal<NotificationBuilder>() {
        @Override
        protected NotificationBuilder initialValue() {
            return new NotificationBuilder(
                    (Method[]) testMethods.clone(),
                    powerMockTestNotifier,
                    pendingTestInstancesOnMotherThread);
        }
    };

    PowerMockRunNotifier(
            RunNotifier junitRunNotifier,
            PowerMockTestNotifier powerMockTestNotifier,
            Method[] testMethods) {
        this.junitRunNotifier = junitRunNotifier;
        this.powerMockTestNotifier = powerMockTestNotifier;
        this.testMethods = testMethods;
    }

    Class<?> getSuiteClass() {
        return this.suiteClass;
    }

    public void suiteClassInitiated(Class<?> testClass) {
        this.suiteClass = testClass;
        notificationBuilder.get().testSuiteStarted(testClass);
    }

    public void testInstanceCreated(Object testInstance) {
        if (Thread.currentThread() == motherThread) {
            pendingTestInstancesOnMotherThread.add(testInstance);
        }
        notificationBuilder.get().testInstanceCreated(testInstance);
    }

    @Override
    public void addListener(RunListener listener) {
        invoke("addListener", listener);
    }

    @Override
    public void removeListener(RunListener listener) {
        invoke("removeListener", listener);
    }

    @Override
    public void fireTestRunStarted(Description description) {
        invoke("fireTestRunStarted", description);
    }

    @Override
    public void fireTestRunFinished(Result result) {
        invoke("fireTestRunFinished", result);
    }

    @Override
    public void fireTestStarted(Description description)
    throws StoppedByUserException {
        invoke("fireTestStarted", description);
        notificationBuilder.get().testStartHasBeenFired(description);
    }

    @Override
    public void fireTestFailure(Failure failure) {
        notificationBuilder.get().failure(failure);
        invoke("fireTestFailure", failure);
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure) {
        notificationBuilder.get().assumptionFailed(failure.getDescription());
        invoke("fireTestAssumptionFailed", failure);
    }

    @Override
    public void fireTestIgnored(Description description) {
        notificationBuilder.get().testIgnored(description);
        invoke("fireTestIgnored", description);
    }

    @Override
    public void fireTestFinished(Description description) {
        try {
            notificationBuilder.get().testFinished(description);
        } catch (Throwable failure) {
            fireTestFailure(new Failure(description, failure));
            return;
        }
        invoke("fireTestFinished", description);
    }

    @Override
    public void pleaseStop() {
        invoke("pleaseStop");
    }

    @Override
    public void addFirstListener(RunListener listener) {
        invoke("addFirstListener", listener);
    }

    private void invoke(String methodName, Object... args) {
        try {
            invokeMethod(junitRunNotifier, methodName, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
