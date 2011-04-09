/*
 * Copyright 2010 the original author or authors.
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
package samples.junit4.overloading;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.classhierarchy.ChildA;
import samples.classhierarchy.Parent;
import samples.overloading.StaticAndInstanceMethodWithSameName;
import samples.overloading.StaticAndInstanceMethodWithSameNameUser;

import static org.powermock.api.easymock.PowerMock.*;

/**
 * Demonstrates that PowerMock correctly methods that seam to be overloaded but
 * differ because one is static and one is instance.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticAndInstanceMethodWithSameNameUser.class, StaticAndInstanceMethodWithSameName.class })
public class MethodWithSameNameButDifferentDefinitionTypeTest {

    @Test
    public void mockGatewayCanInvokeInstanceMethodWhenClassContainsStaticAndInstanceMethodWithSameName() throws Exception {
        final ChildA object = createMock(ChildA.class);
        StaticAndInstanceMethodWithSameName mock = createMock(StaticAndInstanceMethodWithSameName.class);

        expectNew(ChildA.class).andReturn(object);
        mock.overloaded((Parent) object);
        expectLastCall().once();

        replayAll();

        new StaticAndInstanceMethodWithSameNameUser().performInstaceInvocation(mock);

        verifyAll();
    }

    @Test
    public void mockGatewayCanInvokeStaticMethodWhenClassContainsStaticAndInstanceMethodWithSameName() throws Exception {
        final Parent object = createMock(ChildA.class);

        mockStatic(StaticAndInstanceMethodWithSameName.class);
        expectNew(ChildA.class).andReturn((ChildA) object);
        StaticAndInstanceMethodWithSameName.overloaded((ChildA) object);
        expectLastCall().once();

        replayAll();

        new StaticAndInstanceMethodWithSameNameUser().performStaticInvocation();

        verifyAll();
    }
}
