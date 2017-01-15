/*
 * Copyright 2008 the original author or authors.
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
package samples.junit4.classwithinnermembers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.classwithinnermembers.ClassWithInnerMembers;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Test PowerMock's basic support for inner (member) and local classes.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassWithInnerMembers.class)
public class ClassWithInnerMembersTest {

	@Test
	public void assertStaticMemberClassMockingWorksWithNoConstructorArguments() throws Exception {
		Class<Object> innerClassType = Whitebox.getInnerClassType(ClassWithInnerMembers.class, "MyInnerClass");
		Object innerClassMock = createMock(innerClassType);
		expectNew(innerClassType).andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getValue());

		verifyAll();
	}

	@Test
	public void assertStaticMemberClassMockingWorksWithConstructorArguments() throws Exception {
		Class<Object> innerClassType = Whitebox.getInnerClassType(ClassWithInnerMembers.class, "StaticInnerClassWithConstructorArgument");
		Object innerClassMock = createMock(innerClassType);
		expectNew(innerClassType, "value").andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getValueForStaticInnerClassWithConstructorArgument());

		verifyAll();
	}

	@Test
	public void assertLocalClassMockingWorks() throws Exception {

		final Class<Object> type = Whitebox.getLocalClassType(ClassWithInnerMembers.class, 1, "MyLocalClass");
		Object innerClassMock = createMock(type);
		expectNew(type).andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getLocalClassValue());

		verifyAll();
	}

	@Test
	public void assertLocalClassMockingWorksWithArguments() throws Exception {
		final Class<Object> type = Whitebox.getLocalClassType(ClassWithInnerMembers.class, 2, "MyLocalClass");
		Object innerClassMock = createMock(type);
		expectNew(type, "my value").andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getLocalClassValueWithArgument());

		verifyAll();
	}

	@Test
	public void assertNonStaticMemberClassMockingWorksWithArguments() throws Exception {
		Class<Object> innerClassType = Whitebox.getInnerClassType(ClassWithInnerMembers.class, "MyInnerClassWithConstructorArgument");
		Object innerClassMock = createMock(innerClassType);
		expectNew(innerClassType, "value").andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getValueForInnerClassWithConstructorArgument());

		verifyAll();
	}

	@Test
	public void assertThatAnonymousInnerClassesWorks() throws Exception {
		final Class<Object> type = Whitebox.getAnonymousInnerClassType(ClassWithInnerMembers.class, 1);
		Object innerClassMock = createMock(type);
		expectNew(type).andReturn(innerClassMock);
		expectPrivate(innerClassMock, "doStuff").andReturn("something else");

		replayAll();

		assertEquals("something else", new ClassWithInnerMembers().getValueForAnonymousInnerClass());

		verifyAll();
	}
}
