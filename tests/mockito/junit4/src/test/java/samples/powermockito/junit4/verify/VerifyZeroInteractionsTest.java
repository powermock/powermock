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

package samples.powermockito.junit4.verify;


import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;
import samples.singleton.StaticExample;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;


@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticExample.class)
public class VerifyZeroInteractionsTest {
    
    @Test
    public void should_throw_verification_exception_in_case_if_static_method_is_called() {
        mockStatic(StaticExample.class);
        
        StaticExample.staticMethodReturningString();
        
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyZeroInteractions(StaticExample.class);
            }
        }).as("Verify Exception is thrown.")
          .isInstanceOf(NoInteractionsWanted.class)
          .hasMessageContaining("No interactions");
    }
    
    @Test
    public void should_throw_verification_exception_in_case_if_instance_method_called() {
        final Service mock = mock(Service.class);
        
        mock.getServiceMessage();
        
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyZeroInteractions(mock);
            }
        }).as("Verify Exception is thrown.")
          .isInstanceOf(NoInteractionsWanted.class)
          .hasMessageContaining("No interactions");
    }
    
    @Test
    public void should_not_throw_verification_exception_in_case_if_no_methods_are_called_for_static_mock() {
        mockStatic(StaticExample.class);
        
        final Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyZeroInteractions(StaticExample.class);
            }
        });
        
        assertThat(throwable)
            .as("Verify Exception is not thrown.")
            .isNull();
    }
    
    @Test
    public void should_not_throw_verification_exception_in_case_if_no_methods_are_called_for_instance_mock() {
        final Service mock = mock(Service.class);
    
        final Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                verifyZeroInteractions(mock);
            }
        });
    
        assertThat(throwable)
            .as("Verify Exception is not thrown.")
            .isNull();
    }
    
}
