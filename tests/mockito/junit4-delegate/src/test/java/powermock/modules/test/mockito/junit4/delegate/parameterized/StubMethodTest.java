/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powermock.modules.test.mockito.junit4.delegate.parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import powermock.modules.test.mockito.junit4.delegate.SuppressedMethod;
import powermock.modules.test.mockito.junit4.delegate.SuppressedMethodStubbing;
import samples.suppressmethod.SuppressMethod;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static powermock.modules.test.mockito.junit4.delegate.SuppressedMethod.*;
import static powermock.modules.test.mockito.junit4.delegate.SuppressedMethodStubbing.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(SuppressMethod.class)
public class StubMethodTest {

    @Parameterized.Parameter(0)
    public SuppressedMethod method;

    @Parameterized.Parameter(1)
    public SuppressedMethodStubbing stubbing;

    @Test
    public void test() throws Exception {
        stubbing.enforceOn(stub(method(SuppressMethod.class, method.name())));

        final SuppressMethod tested = new SuppressMethod();
        Callable<?> methodInvocation = new Callable<Object>() {
            @Override
            public Object call() {
                return method.invokeOn(tested);
            }
        };

        stubbing.verify(methodInvocation);
        stubbing.verify(methodInvocation);
        stubbing.verify(methodInvocation);
    }

    @Parameterized.Parameters(name = " {0} {1}")
    public static List<?> paramValues() {
        return Arrays.asList(new Object[][]{
            {getObject, Hello},
            {getObject, float_4},
            {getObject, exception},
            {getObjectStatic, Hello},
            {getObjectStatic, float_4},
            {getObjectStatic, exception},
            //			{getFloat, Hello},  //Incompatible return-type
            {getFloat, float_4},
            {getFloat, exception},});
    }
}
