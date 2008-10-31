package demo.org.powermock.examples.tutorial.hellopower;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.PowerMock.mockStatic;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//@PrepareForTest(SimpleConfig.class)
//@RunWith(PowerMockRunner.class)
public class HelloWorldTest_Tutorial {

	@Test
	public void testGreeting() {
		// TODO: mock the static methods of SimpleConfig
		mockStatic(SimpleConfig.class);
		
		expect(SimpleConfig.getGreeting()).andReturn("Hello");
		expect(SimpleConfig.getTarget()).andReturn("world");
		
		replay(SimpleConfig.class);
		
		// TODO: make this assertion work
		assertEquals("Hello world", new HelloWorld().greet());

		verify(SimpleConfig.class);
	}
}
