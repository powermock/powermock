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
package org.powermock.core.testlisteners;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core static utility to help modules, such as PowerMockRunner, that needs to
 * communicate with some 3rd-party framework in order to properly fire
 * events to PowerMockTestListener instances.
 */
public class GlobalNotificationBuildSupport {

    public interface Callback {
        void suiteClassInitiated(Class<?> testClass);

        void testInstanceCreated(Object testInstance);
    }

    private static final Map<String, Callback> testSuiteCallbacks =
            new ConcurrentHashMap<String, Callback>();

    private static final Map<Class<?>, Callback> initiatedTestSuiteClasses =
            new ConcurrentHashMap<Class<?>, Callback>();

    private static final ThreadLocal<Class<?>> pendingInitiatedTestClass =
            new ThreadLocal<Class<?>>();

    public static void prepareTestSuite(
            String testClassName, Callback callback) {
        if (testSuiteCallbacks.containsKey(testClassName)) {
            throw new IllegalStateException(
                    "Needs to wait for concurrent test-suite execution to start!");
        } else {
            testSuiteCallbacks.put(testClassName, callback);
            Class<?> initiatedTestClass = pendingInitiatedTestClass.get();
            if (null != initiatedTestClass
                    && initiatedTestClass.getName().equals(testClassName)) {
                System.err.println("Detected late test-suite preparation of "
                        + "already initiated test-" + initiatedTestClass);
                testClassInitiated(initiatedTestClass);
            }
        }
    }

    public static void testClassInitiated(Class<?> testClass) {
        if (false == initiatedTestSuiteClasses.containsKey(testClass)) {
            Callback callback = testSuiteCallbacks.get(testClass.getName());
            if (null == callback) {
                pendingInitiatedTestClass.set(testClass);
            } else {
                initiatedTestSuiteClasses.put(testClass, callback);
                callback.suiteClassInitiated(testClass);
                pendingInitiatedTestClass.set(null);
            }
        }
    }

    private static int countInitializersInTrace(final String className) {
        int initializerCount = 0;
        for (StackTraceElement ste : new Throwable().getStackTrace()) {
            if ("<init>".equals(ste.getMethodName())
                    && className.equals(ste.getClassName())
                    && 2 <= ++initializerCount) {
                return 2;
            }
        }
        return initializerCount;
    }

    public static void testInstanceCreated(Object testInstance) {
        for (Class<?> c = testInstance.getClass(); Object.class != c; c = c.getSuperclass()) {
            Callback callback = initiatedTestSuiteClasses.get(c);
            if (null != callback) {
                if (1 == countInitializersInTrace(c.getName())) {
                    callback.testInstanceCreated(testInstance);
                }
                return;
            }
        }
    }

    public static void closeTestSuite(Class<?> testClass) {
        Callback callback = initiatedTestSuiteClasses.remove(testClass);
        if (null != callback
                && !initiatedTestSuiteClasses.values().contains(callback)) {
            testSuiteCallbacks.values().remove(callback);
        }
    }

    public static void closePendingTestSuites(Callback callback) {
        testSuiteCallbacks.values().remove(callback);
        initiatedTestSuiteClasses.values()
                .removeAll(java.util.Collections.singleton(callback));
    }
}
