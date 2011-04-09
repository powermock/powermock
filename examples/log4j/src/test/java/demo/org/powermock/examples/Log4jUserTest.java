package demo.org.powermock.examples;

import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(Log4jUser.class)
@MockPolicy(Log4jMockPolicy.class)
public class Log4jUserTest {

	@Test
	public void assertThatLog4jMockPolicyWorks() throws Exception {
		final Log4jUser tested = createPartialMockAndInvokeDefaultConstructor(Log4jUser.class, "getMessage");
		final String otherMessage = "other message";
		final String firstMessage = "first message and ";

		expect(tested.getMessage()).andReturn(firstMessage);

		replayAll();

		final String actual = tested.mergeMessageWith(otherMessage);
		Class<? extends Logger> class1 = Whitebox.getInternalState(Log4jUserParent.class, Logger.class).getClass();
		assertTrue(Enhancer.isEnhanced(class1));

		verifyAll();

		assertEquals(firstMessage + otherMessage, actual);
	}
}
