package demo.org.powermock.examples.tutorial.hellopower.withoutpowermock;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

public class HelloWorldTest {

	@Test
	public void testGreeting() {
		ConfigWrapper config = EasyMock.createMock(ConfigWrapper.class);
		expect(config.getGreeting()).andReturn("Hello");
		expect(config.getTarget()).andReturn("world");
		
		replay(config);
		
		assertEquals("Hello world", new HelloWorld(config).greet());

		verify(config);
	}
}
