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

package org.powermock.api.mockito.invocation;

import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationContainer;
import org.mockito.invocation.MockHandler;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.internal.invocation.InvocationControlAssertionError;
import org.powermock.core.MockRepository;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The class provides a access to method and data of  {@link org.mockito.invocation.MockHandler} from the given mock instance.
 */
public class MockHandlerAdaptor<T> {
    private final T mock;
    private final InvocationFactory invocationFactory;
    private final MockingDetails mockingDetails;
    
    MockHandlerAdaptor(final T mock) {
        this.mock = mock;
        this.invocationFactory = new InvocationFactory();
        this.mockingDetails = Mockito.mockingDetails(mock);
    }
    
    public void setAnswersForStubbing(final List<Answer<?>> answers) {
        InvocationContainer invocationContainer = getMockHandler().getInvocationContainer();
        try {
            Whitebox.invokeMethod(invocationContainer, "setAnswersForStubbing", answers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getMock() {
        return mock;
    }
    
    private MockHandler<T> getMockHandler() {
        return mockingDetails.getMockHandler();
    }
    
    InvocationContainer getInvocationContainer() {
        return getMockHandler().getInvocationContainer();
    }
    
    Object performIntercept(final Object mock, final Method method, Object[] arguments) throws Throwable {
        
        Invocation invocation = createInvocation(mock, method, arguments);
        
        try {
            return getMockHandler().handle(invocation);
        } catch (NotAMockException e) {
            if (invocation.getMock()
                          .getClass()
                          .getName()
                          .startsWith("java.") && MockRepository.getInstanceMethodInvocationControl(invocation.getMock()) != null) {
                return invocation.callRealMethod();
            } else {
                throw e;
            }
        } catch (MockitoAssertionError e) {
            InvocationControlAssertionError.updateErrorMessageForMethodInvocation(e);
            throw e;
        }
    }
    
    private Invocation createInvocation(final Object mock, final Method method, final Object[] arguments) {
        return invocationFactory.createInvocation(mock, method, getMockHandler().getMockSettings(), arguments);
    }
}
