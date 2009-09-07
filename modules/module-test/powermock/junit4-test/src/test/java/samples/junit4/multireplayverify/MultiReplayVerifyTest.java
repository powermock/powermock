package samples.junit4.multireplayverify;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

/**
 * Asserts that it's ok the manually replay a mock and then use replayAll(). The
 * same regards verify and verifyAll.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewDemo.class)
public class MultiReplayVerifyTest {

	@Test
	public void replyFollowedByReplayAllIsAllowed() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage()).andReturn("Hello altered World");

		replay(MyClass.class);
		replayAll();

		String actual = tested.getMessage();

		verifyAll();
		assertEquals("Expected and actual did not match", expected, actual);
	}

	@Test
	public void verifyFollowedByVerifyAllIsAllowed() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage()).andReturn("Hello altered World");

		replayAll();

		String actual = tested.getMessage();

		verify(MyClass.class);
		verifyAll();
		assertEquals("Expected and actual did not match", expected, actual);
	}

}
