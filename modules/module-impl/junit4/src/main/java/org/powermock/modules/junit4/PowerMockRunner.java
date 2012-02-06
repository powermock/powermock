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

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.powermock.modules.junit4.common.internal.impl.AbstractCommonPowerMockRunner;
import org.powermock.modules.junit4.common.internal.impl.VersionCompatibility;
import org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl;
import org.powermock.modules.junit4.internal.impl.PowerMockJUnit47RunnerDelegateImpl;
import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;

public class PowerMockRunner extends AbstractCommonPowerMockRunner {

    public PowerMockRunner(Class<?> klass) throws Exception {
        super(klass, VersionCompatibility.getJUnitVersion().isGreaterOrEquals( 4, 7 ) ? PowerMockJUnit47RunnerDelegateImpl.class
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
}
