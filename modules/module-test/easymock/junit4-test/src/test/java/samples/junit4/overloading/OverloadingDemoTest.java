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
import samples.overloading.OverloadedMethodsExample;
import samples.overloading.OverloadingDemo;

import static org.powermock.api.easymock.PowerMock.*;

/**
 * Demonstrates that PowerMock correctly invoke overloaded methods from the
 * MockGateway.
 * href="http://code.google.com/p/powermock/issues/detail?id=229">229</a> is
 * solved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ OverloadedMethodsExample.class, OverloadingDemo.class })
public class OverloadingDemoTest {

	@Test
	public void mockGatewayFindsBestOverloadedMethodCandidateWhenOnlyOneArgument() throws Exception {
		final Parent object = createMock(ChildA.class);

		mockStatic(OverloadedMethodsExample.class);

		OverloadedMethodsExample.overloadedMethodWithOneArgument(object);
		expectLastCall().once();

		expectNew(ChildA.class).andReturn((ChildA) object);

		replayAll();

		final OverloadingDemo demo = new OverloadingDemo();
		demo.performSingleOverloadedArgumentTest();

		verifyAll();
	}

	@Test
	public void mockGatewayFindsBestOverloadedMethodCandidateWhenBothArgumentSame() throws Exception {
		final Parent child = createMock(ChildA.class);

		mockStatic(OverloadedMethodsExample.class);

		OverloadedMethodsExample.overloadedMethodWithTwoArguments(child, child);
		expectLastCall().once();

		expectNew(ChildA.class).andReturn((ChildA) child).times(2);

		replayAll();

		final OverloadingDemo demo = new OverloadingDemo();
		demo.performMethodOverloadTestWhenBothArgumentSame();

		verifyAll();
	}

	@Test
	public void mockGatewayFindsBestOverloadedMethodCandidateWhenOneArgumentSameAndOneDifferent() throws Exception {
		final Parent child = createMock(ChildA.class);
		final Parent parent = createMock(Parent.class);

		mockStatic(OverloadedMethodsExample.class);

		OverloadedMethodsExample.overloadedMethodWithTwoArguments(parent, child);
		expectLastCall().once();

		expectNew(ChildA.class).andReturn((ChildA) child);
		expectNew(Parent.class).andReturn(parent);

		replayAll();

		final OverloadingDemo demo = new OverloadingDemo();
		demo.performMethodOverloadTestWithOneArgumentSameAndOneDiffernt();

		verifyAll();
	}
}
