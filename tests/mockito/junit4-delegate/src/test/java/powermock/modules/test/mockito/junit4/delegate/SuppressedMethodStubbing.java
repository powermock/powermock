/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powermock.modules.test.mockito.junit4.delegate;

import java.util.concurrent.Callable;
import org.powermock.api.support.membermodification.strategy.MethodStubStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public enum SuppressedMethodStubbing {

    Hello("Hello"),
    float_4(4.0F),
    exception(new Exception("message")) {
                @Override
                public <T> void enforceOn(MethodStubStrategy<T> stub) {
                    stub.toThrow((Exception) value);
                }

                @Override
                public void verify(final Callable<?> invocation) throws Exception {
                    super.verify(new Callable<Exception>() {
                        @Override
                        public Exception call() {
                            try {
                                invocation.call();
                                fail("Expected exception: " + value);
                                return null;
                            } catch (Exception actualException) {
                                return actualException;
                            }
                        }
                    });
                }

                @Override
                public String toString() {
                    return "throws Exception";
                }
            };

    final Object value;

    private SuppressedMethodStubbing(Object value) {
        this.value = value;
    }

    public <T> void enforceOn(MethodStubStrategy<T> stub) {
        stub.toReturn((T) value);
    }

    public void verify(Callable<?> invocation) throws Exception {
        assertEquals(value, invocation.call());
    }

    @Override
    public String toString() {
        return "returns " + value;
    }
}
