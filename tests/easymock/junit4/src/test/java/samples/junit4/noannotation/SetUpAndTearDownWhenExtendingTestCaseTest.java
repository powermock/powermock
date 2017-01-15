package samples.junit4.noannotation;

import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SetUpAndTearDownWhenExtendingTestCaseTest extends TestCase {
	private static final String INITIAL_MESSAGE = "";
	private static final String SET_UP_MESSAGE = "setUp";
	private static final String TEST_MESSAGE = "test";

	private static String CURRENT_MESSAGE = INITIAL_MESSAGE;

	@Override
	protected void setUp() throws Exception {
		assertEquals(INITIAL_MESSAGE, CURRENT_MESSAGE);
		CURRENT_MESSAGE = SET_UP_MESSAGE;
	}

	@Override
	protected void tearDown() throws Exception {
		assertEquals(TEST_MESSAGE, CURRENT_MESSAGE);
	}

	public void testSomething() throws Exception {
		assertEquals(SET_UP_MESSAGE, CURRENT_MESSAGE);
		CURRENT_MESSAGE = TEST_MESSAGE;
	}
}
