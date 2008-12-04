package samples.junit4.resetmock;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.reset;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

/**
 * Tests to verify that the reset functionality works.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewDemo.class)
public class ResetMockTest {

	@Test
	public void assertManualResetWorks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		reset(myClassMock);
		reset(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

	@Test
	public void assertManualResetWorksWhenMixingInstanceAndClassMocks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		reset(myClassMock, MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

	@Test
	public void assertResetAllWorks() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		MyClass myClassMock = createMock(MyClass.class);
		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		String message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);

		resetAll();

		expectNew(MyClass.class).andReturn(myClassMock);
		expect(myClassMock.getMessage()).andReturn("message");

		replayAll();

		message = tested.getMessage();

		verifyAll();
		assertEquals("message", message);
	}

}
