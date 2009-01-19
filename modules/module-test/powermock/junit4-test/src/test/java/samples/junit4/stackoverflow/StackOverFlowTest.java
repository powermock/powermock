package samples.junit4.stackoverflow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EvilHashCode.class)
public class StackOverFlowTest {

	@Test
	public void testStackOverFlowShouldNotOccur() throws Exception {
		new EvilHashCode();
	}
}
