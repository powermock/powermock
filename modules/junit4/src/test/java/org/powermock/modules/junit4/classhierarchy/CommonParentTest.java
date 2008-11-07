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
package org.powermock.modules.junit4.classhierarchy;

import static org.junit.Assert.*;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.classhierarchy.ChildA;
import samples.classhierarchy.ChildB;
import samples.classhierarchy.Parent;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildA.class, ChildB.class, Parent.class})
public class CommonParentTest {

	@Test
	public void testPossibleToMockTwoClassesWithSameParent() throws Exception {
		ChildA a = PowerMock.createMock(ChildA.class);
		EasyMock.expect(a.getValue()).andReturn(5);
		
		PowerMock.replay(a);
		
		assertEquals(5, a.getValue());
		
		PowerMock.verify(a);
	}
}
