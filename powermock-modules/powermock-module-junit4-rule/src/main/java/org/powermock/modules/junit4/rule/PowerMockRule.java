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
import org.powermock.api.support.SafeExceptionRethrower;
import org.powermock.classloading.ClassloaderExecutor;
import org.powermock.classloading.SingleClassloaderExecutor;
import org.powermock.classloading.spi.DoNotClone;
import org.powermock.core.MockRepository;
import org.powermock.tests.utils.MockPolicyInitializer;
import org.powermock.tests.utils.TestChunk;
import org.powermock.tests.utils.TestSuiteChunker;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;

public class PowerMockRule implements MethodRule {
	private static Class<?> previousTargetClass;
	private static MockPolicyInitializer mockPolicyInitializer;

    @DoNotClone
	private static TestSuiteChunker testSuiteChunker;


	@Override
	public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        if (isNotRuleInitialized(target)) {
            init(target);
		}
		return new PowerMockStatement(base, testSuiteChunker.getTestChunk(method.getMethod()), mockPolicyInitializer);
	}

    protected boolean isNotRuleInitialized(Object target) {return testSuiteChunker  == null || previousTargetClass != target.getClass();}

    protected void init(Object target) {
        final Class<?> testClass = target.getClass();

        try {
            mockPolicyInitializer = new MockPolicyInitializerImpl(testClass);
            testSuiteChunker = new PowerMockRuleTestSuiteChunker(testClass);
            previousTargetClass = target.getClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class PowerMockStatement extends Statement {
	private final Statement fNext;
    private final ClassloaderExecutor classloaderExecutor;
	private final MockPolicyInitializer mockPolicyInitializer;

    public PowerMockStatement(Statement fNext, TestChunk testChunk, MockPolicyInitializer mockPolicyInitializer) {
        this.fNext = fNext;
        this.mockPolicyInitializer = mockPolicyInitializer;
        this.classloaderExecutor = new SingleClassloaderExecutor(testChunk.getClassLoader());
    }

    @Override
	public void evaluate() throws Throwable {
		classloaderExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// Re-executes the policy method that might initialize mocks that 
					// were cleared after the previous statement.
					// This fixes https://github.com/jayway/powermock/issues/581
					mockPolicyInitializer.refreshPolicies(getClass().getClassLoader());
					fNext.evaluate();
				} catch (Throwable e) {
					SafeExceptionRethrower.safeRethrow(e);
				} finally {
					// Clear the mock repository after each test
					MockRepository.clear();
				}
			}
		});
	}
}
