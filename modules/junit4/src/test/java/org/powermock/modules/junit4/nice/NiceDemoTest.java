package org.powermock.modules.junit4.nice;

import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.createPartialMockNice;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.nice.NiceDemo;

/**
 * This is a simple test case for the {@link NiceDemo} class that demonstrates
 * that strict method mocking works.
 * 
 */
@RunWith(PowerMockRunner.class)
public class NiceDemoTest {

	@Test
	public void testCallAThenB_noExpectations() throws Exception {
		NiceDemo tested = createPartialMockNice(NiceDemo.class, "A", "B");

		replay(tested);

		tested.callAThenB();

		verify(tested);
	}

}
