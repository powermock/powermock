/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers.javassist;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.MockTransformerChain;
import powermock.test.support.ClassWithLargeMethods;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MethodSizeMockTransformerTest extends AbstractBaseMockTransformerTest {
    
    @Test
    public void shouldLoadClassWithMethodLowerThanJvmLimit() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodLowerThanLimit.class.getName());
        assertNotNull("Class has been loaded", clazz);
        // There should be no exception since method was not overridden
        clazz.getMethod("init").invoke(clazz);
    }
    
    @Test
    public void shouldLoadClassAndOverrideMethodGreaterThanJvmLimit() throws Exception {
        final Class<?> clazz = loadWithMockClassLoader(ClassWithLargeMethods.MethodGreaterThanLimit.class.getName());
        
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                clazz.getMethod("init").invoke(clazz);
            }
        });
        
        assertThat(throwable)
            .as("Overridden method should throw exception")
            .isNotNull();
        
        assertThat(throwable.getCause())
            .as("Clause of exception should be IllegalAccessException")
            .isInstanceOf(IllegalAccessException.class)
            .hasMessageContaining("Method was too large and after instrumentation exceeded JVM limit");
    }
    
    @Override
    protected MockTransformerChain createMockTransformerChain() {
        return new JavassistMockTransformerChainFactory().createDefaultChain(Collections.<MockTransformer>emptyList());
    }
}