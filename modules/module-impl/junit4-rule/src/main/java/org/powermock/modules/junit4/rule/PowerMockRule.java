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
import org.powermock.classloading.ClassloaderExecutor;

public class PowerMockRule implements MethodRule {
    private static ClassloaderExecutor classloaderExecutor;
    private static Class<?> previousTargetClass;
    
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		if (classloaderExecutor == null || previousTargetClass != target.getClass()) {
			classloaderExecutor = PowerMockClassloaderExecutor.forObject(target);
			previousTargetClass = target.getClass();
		}
		return new PowerMockStatement(base, classloaderExecutor);
	}

}

class PowerMockStatement extends Statement {
    private final Statement fNext;
	private final ClassloaderExecutor classloaderExecutor;

    public PowerMockStatement(Statement base, ClassloaderExecutor classloaderExecutor) {
        fNext = base;
		this.classloaderExecutor = classloaderExecutor;
    }

    @Override
    public void evaluate() throws Throwable {
        classloaderExecutor.execute(new Runnable() {
            public void run() {
                try {
                		fNext.evaluate();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
