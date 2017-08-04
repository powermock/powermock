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
package org.powermock.api.mockito.internal;

import org.mockito.Mockito;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.verification.MockAwareVerificationMode;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.internal.expectation.PowerMockitoStubberImpl;
import org.powermock.api.mockito.internal.verification.StaticMockAwareVerificationMode;
import org.powermock.core.classloader.ClassloaderWrapper;

import java.util.concurrent.Callable;

public class PowerMockitoCore {
    
    public PowerMockitoStubber doAnswer(final Answer answer) {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doAnswer(answer);
            }
        });
    }
    
    public PowerMockitoStubber doThrow(final Throwable toBeThrown) {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doThrow(toBeThrown);
            }
        });
    }
    
    public PowerMockitoStubber doCallRealMethod() {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doCallRealMethod();
            }
        });
    }
    
    public PowerMockitoStubber doNothing() {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doNothing();
            }
        });
    }
    
    public PowerMockitoStubber doReturn(final Object toBeReturned) {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doReturn(toBeReturned);
            }
        });
    }
    
    public PowerMockitoStubber doAnswer(final Object toBeReturned, final Object... othersToBeReturned) {
        return doAnswer(new Callable<Stubber>() {
            @Override
            public Stubber call() throws Exception {
                return Mockito.doReturn(toBeReturned, othersToBeReturned);
            }
        });
    }
    
    private PowerMockitoStubber doAnswer(final Callable<Stubber> callable) {
        final Stubber stubber = ClassloaderWrapper.runWithClass(callable);
        return new PowerMockitoStubberImpl(stubber);
    }
    
    private MockingProgress getMockingProgress() {
        return ThreadSafeMockingProgress.mockingProgress();
    }
    
    public MockAwareVerificationMode wrapInMockitoSpecificVerificationMode(Object mock, VerificationMode mode) {
        return new MockAwareVerificationMode(mock, mode, getMockingProgress().verificationListeners());
    }
public <T> VerificationMode wrapInStaticVerificationMode(final Class<T> mockedClass, final VerificationMode verificationMode) {
        return new StaticMockAwareVerificationMode(mockedClass, verificationMode, getMockingProgress().verificationListeners());
    }
    public MockAwareVerificationMode wrapInStaticVerificationMode(VerificationMode verificationMode) {
        return new StaticMockAwareVerificationMode(verificationMode, getMockingProgress().verificationListeners());
    }
}
