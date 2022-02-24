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
import org.junit.runner.RunWith;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.powermock.api.mockito.MockitoVersion;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticClass.class)
public class PowerMockStaticMockingTest {
    
    @Test
    public void should_mock_static_method_when_mockito_inline_mock_creator_for_mockito_tests() {

        assumeTrue("Test makes sense only for Mockito 2 & 3 & 4",
                MockitoVersion.isMockito2() || MockitoVersion.isMockito3() || MockitoVersion.isMockito4());
    
        PowerMockito.mockStatic(StaticClass.class);
        
        String value = "Why me?";
        
        when(StaticClass.ask()).thenReturn(value);
        
        assertThat(StaticClass.ask())
            .as("Mock for static method works")
            .isEqualTo(value);
    }
    
    @Test
    public void should_verify_static_method_when_mockito_inline_mock_creator_for_mockito_tests() throws Exception {

        assumeTrue("Test makes sense only for Mockito 2 & 3 & 4",
                MockitoVersion.isMockito2() || MockitoVersion.isMockito3() || MockitoVersion.isMockito4());
    
        PowerMockito.mockStatic(StaticClass.class);
    
        final String value = "Why me?";
    
        PowerMockito.doNothing().when(StaticClass.class,"say", value);
        
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                StaticClass.say(value);
                PowerMockito.verifyNoMoreInteractions(StaticClass.class);
            }
        }).as("Verify exception is thrown")
          .isInstanceOf(NoInteractionsWanted.class);
    }
    
}
