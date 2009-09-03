package samples.powermockito.junit4.privatemocking;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.privatemocking.PrivateMethodDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { PrivateMethodDemo.class })
public class PrivateMockingTest {

	@Test
	public void expectationsWorkWhenSpyingOnPrivateMethods() throws Exception {
		PrivateMethodDemo tested = spy(new PrivateMethodDemo());
		assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

		when(tested, "doSayYear", 12, "test").thenReturn("another");

		assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));
		assertEquals("another", tested.sayYear("test", 12));
	}
}
