package demo.org.powermock.examples;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createPartialMockAndInvokeDefaultConstructor;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(Log4jUser.class)
public class Log4jUserTest {

	@Test
	public void testMergeMessageWith() throws Exception {
		final Log4jUser tested = createPartialMockAndInvokeDefaultConstructor(Log4jUser.class, "getMessage");
		final String otherMessage = "other message";
		final String firstMessage = "first message and ";

		expect(tested.getMessage()).andReturn(firstMessage);

		replayAll();

		final String actual = tested.mergeMessageWith(otherMessage);

		verifyAll();

		assertEquals(firstMessage + otherMessage, actual);
	}
}
