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

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.mockito.internal.expectation.PowerMockitoStubberImpl;
import org.powermock.api.mockito.internal.invocation.MockitoNewInvocationControl;
import org.powermock.api.mockito.internal.mockcreation.DefaultMockCreator;
import org.powermock.api.mockito.internal.stubbing.PowerMockCallRealMethod;
import org.powermock.api.mockito.internal.verification.DefaultConstructorArgumentsVerification;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.ClassloaderWrapper;
import org.powermock.core.spi.NewInvocationControl;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static org.powermock.utils.Asserts.assertNotNull;

public class PowerMockitoCore {
    
    private static final PowerMockCallRealMethod POWER_MOCK_CALL_REAL_METHOD = new PowerMockCallRealMethod();
    
    private static final String NO_OBJECT_CREATION_ERROR_MESSAGE_TEMPLATE = "No instantiation of class %s was recorded during the test. Note that only expected object creations (e.g. those using whenNew(..)) can be verified.";
    
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
    
    public <T> DefaultConstructorArgumentsVerification<T> verifyNew(final Class<T> mock, final VerificationMode mode) {
        assertNotNull(mock, "Class to verify cannot be null");
        assertNotNull(mode, "Verify mode cannot be null");

        @SuppressWarnings("unchecked") MockitoNewInvocationControl<T> invocationControl = (MockitoNewInvocationControl<T>) MockRepository.getNewInstanceControl(mock);
    
        assertNotNull(invocationControl, String.format(NO_OBJECT_CREATION_ERROR_MESSAGE_TEMPLATE, Whitebox.getType(mock).getName()));
    
        invocationControl.verify(mode);
        //noinspection unchecked
        return new DefaultConstructorArgumentsVerification<T>((NewInvocationControl<T>) invocationControl, mock);
    }
    
    public <T> T spy(final T object) {
        MockSettings mockSettings = Mockito.withSettings().defaultAnswer(POWER_MOCK_CALL_REAL_METHOD);
        //noinspection unchecked
        return DefaultMockCreator.mock((Class<T>) Whitebox.getType(object), false, true, object, mockSettings, (Method[]) null);
    }
    
    private PowerMockitoStubber doAnswer(final Callable<Stubber> callable) {
        final Stubber stubber = ClassloaderWrapper.runWithClass(callable);
        return new PowerMockitoStubberImpl(stubber);
    }
}
