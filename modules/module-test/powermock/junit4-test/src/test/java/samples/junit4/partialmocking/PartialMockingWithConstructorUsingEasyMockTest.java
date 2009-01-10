package samples.junit4.partialmocking;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.lang.reflect.Method;

import org.easymock.classextension.ConstructorArgs;
import org.junit.Ignore;
import org.junit.Test;

import samples.partialmocking.PartialMockingWithConstructor;

public class PartialMockingWithConstructorUsingEasyMockTest {

	@Ignore("The initialize method is never invoked but is caught by the proxy. This is a possibly a bug in EasyMock class extensions?")
	@Test
	public void testPartialMock() throws Exception {

		/*
		 * In the original test case PartialMockingWithConstructor had
		 * constructor arguments which I removed to slim down the test case,
		 * originally I was using the following method to create a partial mock.
		 * Regardless the same problem still occurs.
		 */
		ConstructorArgs args = new ConstructorArgs(PartialMockingWithConstructor.class.getConstructor());
		Method touchMethod = PartialMockingWithConstructor.class.getMethod("touch");

		PartialMockingWithConstructor nationPartialMock = createMock(PartialMockingWithConstructor.class, args, touchMethod);

		/*
		 * The following method also causes the same problem.
		 */

		// Nation nationPartialMock =
		// createPartialMockAndInvokeDefaultConstructor(Nation.class,"touch");
		replay(nationPartialMock);

		// Uncommenting the following line has no effect on the test result.
		// nationPartialMock.initialize();

		verify(nationPartialMock);
	}
}
