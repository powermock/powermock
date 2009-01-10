package samples.junit4.partialmocking;

import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.createPartialMockAndInvokeDefaultConstructor;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Ignore;
import org.junit.Test;

import samples.partialmocking.PartialMockingWithConstructor;

public class PartialMockingWithConstructorTest {

	@Ignore("The initialize method is never invoked but is caught by the proxy. This is a possibly a bug in EasyMock class extensions?")
	@Test
	public void testPartialMock() throws Exception {

		/*
		 * In the original test case Nation had constructor arguments which I
		 * removed to slim down the test case, originally I was using the
		 * following method to create a partial mock. Regardless the same
		 * problem still ocurrs.
		 */
		PartialMockingWithConstructor nationPartialMock = createPartialMockAndInvokeDefaultConstructor(PartialMockingWithConstructor.class, "touch");

		/*
		 * The following method also causes the same problem.
		 */

		// Nation nationPartialMock =
		// createPartialMockAndInvokeDefaultConstructor(Nation.class,"touch");
		replay(nationPartialMock);

		// Uncommenting the following line has no effect on the test result.
		// nationPartialMock.initialise();

		verify(nationPartialMock);
	}
}
