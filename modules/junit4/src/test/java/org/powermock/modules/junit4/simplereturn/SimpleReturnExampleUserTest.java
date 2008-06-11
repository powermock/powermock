package org.powermock.modules.junit4.simplereturn;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.simplereturn.SimpleReturnExample;
import samples.simplereturn.SimpleReturnExampleUser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleReturnExample.class)
public class SimpleReturnExampleUserTest {

	@Test
	public void testMyMethod() throws Exception {
		SimpleReturnExample mock = createMock(SimpleReturnExample.class);
		expect(mock.mySimpleMethod()).andReturn(2);

		replay(mock);

		assertEquals(2, new SimpleReturnExampleUser(mock).myMethod());

		verify(mock);
	}

}
