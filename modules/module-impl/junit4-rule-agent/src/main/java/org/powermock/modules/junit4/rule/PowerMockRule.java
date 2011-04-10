/*
 * Copyright 2010 the original author or authors.
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
package org.powermock.modules.junit4.rule;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.core.MockRepository;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.agent.support.PowerMockAgentTestInitializer;

public class PowerMockRule implements MethodRule {

    static {
        if(PowerMockRule.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
            throw new IllegalStateException("PowerMockRule can only be used with the system classloader but was loaded by "+PowerMockRule.class.getClassLoader());
        }
        PowerMockAgent.initializeIfPossible();
    }


    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        PowerMockAgentTestInitializer.initialize(target.getClass());
        return new PowerMockStatement(base);
    }
}

class PowerMockStatement extends Statement {
    private final Statement fNext;

    public PowerMockStatement(Statement base) {
        fNext = base;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            fNext.evaluate();
        } finally {
            // Clear the mock repository after each test
            MockRepository.clear();
        }
    }
}
