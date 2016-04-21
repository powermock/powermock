/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.modules.junit4.rule;

import org.powermock.modules.junit4.common.internal.impl.JUnit4TestMethodChecker;
import org.powermock.tests.utils.impl.AbstractCommonTestSuiteChunkerImpl;

import java.lang.reflect.Method;

/**
 *
 */
public class PowerMockRuleTestSuiteChunker extends AbstractCommonTestSuiteChunkerImpl {

    public PowerMockRuleTestSuiteChunker(Class testClass) throws Exception {
        super(testClass);
    }

    @Override
    public boolean shouldExecuteTestForMethod(Class<?> testClass, Method potentialTestMethod) {
        return new JUnit4TestMethodChecker(testClass, potentialTestMethod).isTestMethod();
    }

}
