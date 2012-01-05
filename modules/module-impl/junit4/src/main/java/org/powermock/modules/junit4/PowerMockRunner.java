/*
 * Copyright 2008 the original author or authors.
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

import junit.runner.Version;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.modules.junit4.common.internal.impl.AbstractCommonPowerMockRunner;
import org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl;
import org.powermock.modules.junit4.internal.impl.PowerMockJUnit47RunnerDelegateImpl;
import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;

public class PowerMockRunner extends AbstractCommonPowerMockRunner {

    public PowerMockRunner(Class<?> klass) throws Exception {
        super(klass, getJUnitVersion() >= 4.7f ? PowerMockJUnit47RunnerDelegateImpl.class
                : PowerMockJUnit44RunnerDelegateImpl.class);
    }

    /**
     * Clean up some state to avoid OOM issues
     */
    @Override
    public void run(RunNotifier notifier) {
        Description description = getDescription();
        try {
            super.run(notifier);
        } finally {
            Whitebox.setInternalState(description, "fAnnotations", new Annotation[]{});
        }
    }

    private static float getJUnitVersion() {
        String version = Version.id();
        int dot = version.indexOf('.');
        if (dot > 0) {
            // Make sure that only one dot exists
            dot = version.indexOf('.', dot + 1);
            if (dot > 0) {
                /*
                 * If minor version such as 4.8.1 then remove the last digit,
                 * e.g. "4.8.1" becomes "4.8".
                 */
                version = version.substring(0, dot);
            }
        }
        try {
            return Float.parseFloat(version);
        } catch (NumberFormatException e) {
            // If this happens we revert to JUnit 4.4 runner
            return 4.4f;
        }
    }
}
