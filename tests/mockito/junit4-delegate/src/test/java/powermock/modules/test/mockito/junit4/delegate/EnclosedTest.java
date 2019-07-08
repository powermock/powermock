/*
 * Copyright 2014 the original author or authors.
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
package powermock.modules.test.mockito.junit4.delegate;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.staticandinstance.StaticAndInstanceDemo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Demonstrates how PowerMockRunner with annotation PowerMockRunnerDelegate can
 * provide PowerMock features to yet-another JUnit runner.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Enclosed.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class EnclosedTest {

    private static final String stubbedReturnValue
            = "Stubbed return-value from " + EnclosedTest.class;

    public static class NoStubbing {

        @Test
        public void noStubbing() {
            assertThat("Original return-value of #getStaticMessage()",
                    StaticAndInstanceDemo.getStaticMessage(),
                    not(equalTo(stubbedReturnValue)));
        }
    }

    public static class StubbedStaticReturnValue {

        @Test
        public void stubbedStaticReturnValue() {
            mockStatic(StaticAndInstanceDemo.class);
            when(StaticAndInstanceDemo.getStaticMessage())
                    .thenReturn(stubbedReturnValue);
            assertThat("Stubbed return-value of #getStaticMessage()",
                    StaticAndInstanceDemo.getStaticMessage(),
                    equalTo(stubbedReturnValue));
            verifyStatic(StaticAndInstanceDemo.class);
            StaticAndInstanceDemo.getStaticMessage();
            verifyNoMoreInteractions(StaticAndInstanceDemo.class);
        }
    }

    public static class WhenStubbingIsOver {

        @Test
        public void whenStubbingIsOver() {
            assertThat("Back to original return-value of #getStaticMessage()",
                    StaticAndInstanceDemo.getStaticMessage(),
                    not(equalTo(stubbedReturnValue)));
        }
    }

    public static class SubClass extends StubbedStaticReturnValue {}

    public static class SubClassWithExtraNonPublicConstructors
    extends StubbedStaticReturnValue {
        public SubClassWithExtraNonPublicConstructors() {}
        private SubClassWithExtraNonPublicConstructors(boolean arg) {}
        protected SubClassWithExtraNonPublicConstructors(String arg) {}
    }
}
