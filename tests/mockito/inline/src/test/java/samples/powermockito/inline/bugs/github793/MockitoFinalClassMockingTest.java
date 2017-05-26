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

package samples.powermockito.inline.bugs.github793;


import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.MockitoVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class MockitoFinalClassMockingTest {
    
    @Test
    public void should_mock_final_class_with_using_mockito_inline_mock_creator() {
        
        assumeTrue("Test make seances only for Mockito 2", MockitoVersion.isMockito2());
        
        FinalClass mock = Mockito.mock(FinalClass.class);
        
        String value = "Why me?";
        
        when(mock.ask()).thenReturn(value);
        
        assertThat(mock.ask())
            .as("Mock for final class works")
            .isEqualTo(value);
    }
    
    @Test
    public void should_mock_final_method_with_using_mockito_inline_mock_creator() {
        
        assumeTrue("Test make seances only for Mockito 2", MockitoVersion.isMockito2());
        
        final FinalClass mock = Mockito.mock(FinalClass.class);
        
        final String value = "Why me?";
        
        doNothing().when(mock).say(value);
        
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                mock.say(value);
            }
        });
        
        assertThat(throwable)
            .as("Mock for final method works")
            .isNull();
    }
}
