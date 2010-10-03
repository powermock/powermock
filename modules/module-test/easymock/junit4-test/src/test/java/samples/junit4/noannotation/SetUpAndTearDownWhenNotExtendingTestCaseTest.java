package samples.junit4.noannotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SetUpAndTearDownWhenNotExtendingTestCaseTest {
	private static final String INITIAL_MESSAGE = "";
	private static String CURRENT_MESSAGE = INITIAL_MESSAGE;

	public void setUp() throws Exception {
		fail("Should not call setUp");
	}

	public void tearDown() throws Exception {
		fail("Should not call tearDown");
	}

	@Test
	public void testSomething() throws Exception {
		assertEquals(INITIAL_MESSAGE, CURRENT_MESSAGE);
	}
}
