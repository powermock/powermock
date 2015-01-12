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
package org.powermock.modules.junit4;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.AllTests;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.powermock.modules.junit4.common.internal.impl.JUnitVersion;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PowerMockRunnerDelegate {

    Class<? extends Runner> value() default DefaultJUnitRunner.class;

    public final class DefaultJUnitRunner extends Runner {

        private final Runner wrappedDefaultRunner;

        public DefaultJUnitRunner(Class<?> testClass) throws Throwable {
            wrappedDefaultRunner = createDefaultRunner(testClass);
        }

        private static Runner createDefaultRunner(Class<?> testClass)
        throws Throwable {
            try {
                Method suiteMethod = testClass.getMethod("suite");
                if (junit.framework.Test.class.isAssignableFrom(suiteMethod.getReturnType())) {
                    return new AllTests(testClass);
                } else {
                    /* Continue below ... */
                }
            } catch (NoSuchMethodException thereIsNoSuiteMethod) {
                /* Continue below ... */
            }
            if (junit.framework.TestCase.class.isAssignableFrom(testClass)) {
                return new JUnit38ClassRunner(testClass);
            } else if (JUnitVersion.isGreaterThanOrEqualTo("4.5")) {
                return SinceJUnit_4_5.createRunnerDelegate(testClass);
            } else {
                return new JUnit4ClassRunner(testClass);
            }
        }

        @Override
        public Description getDescription() {
            return wrappedDefaultRunner.getDescription();
        }

        @Override
        public void run(RunNotifier notifier) {
            wrappedDefaultRunner.run(notifier);
        }
    }

    /**
     * Stuff that needs to be handled in a separate class, because it
     * deals with API that did not exist before JUnit-4.5. Having this inside
     * {@link DefaultJUnitRunner} would cause runtime error when JUnit-4.4
     * or earlier is used.
     */
    public class SinceJUnit_4_5 {
        static Runner createRunnerDelegate(Class<?> testClass) throws InitializationError {
            return new JUnit4(testClass);
        }
        public static Class[] runnerAlternativeConstructorParams() {
            return new Class[] {Class.class, RunnerBuilder.class};
        }
        public static Object newRunnerBuilder() {
            return new AllDefaultPossibilitiesBuilder(false);
        }
    }
}
