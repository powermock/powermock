package demo.org.powermock.examples.tutorial.hellopower;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(SimpleConfig.class)
@RunWith(PowerMockRunner.class)
public class HelloWorldTest {

	@Test
	public void testGreeting() throws Exception {
		mockStatic(SimpleConfig.class);
		expect(SimpleConfig.getGreeting()).andReturn("Hello");
		expect(SimpleConfig.getTarget()).andReturn("world");

		replayAll();

		assertEquals("Hello world", new HelloWorld().greet());

		verifyAll();
	}
}
