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
	public void testMockA() {
		ChildA a = PowerMock.createMock(ChildA.class);
		EasyMock.expect(a.getValue()).andReturn(5);
		
		PowerMock.replay(a);
		
		assertEquals(5, a.getValue());
		
		PowerMock.verify(a);
	}
}
