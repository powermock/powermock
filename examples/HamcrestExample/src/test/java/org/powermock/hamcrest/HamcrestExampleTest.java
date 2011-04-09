package org.powermock.hamcrest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * A simple test case that asserts the PowerMock works together with Hamcrest
 * matchers.
 */
@RunWith(PowerMockRunner.class)
public class HamcrestExampleTest {

	@Test
	public void testGetString() throws Exception {
		assertThat("File extension", HamcrestExample.getString(), is("hamcrest"));
	}
}
