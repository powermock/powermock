package samples.junit4.nice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.nice.NiceDemo;

import static org.powermock.api.easymock.PowerMock.*;

/**
 * This is a simple test case for the {@link NiceDemo} class that demonstrates
 * that strict method mocking works.
 * 
 */
@RunWith(PowerMockRunner.class)
public class NiceDemoTest {

	@Test
	public void testCallAThenB_noExpectations() throws Exception {
		NiceDemo tested = createNicePartialMock(NiceDemo.class, "A", "B");

		replay(tested);

		tested.callAThenB();

		verify(tested);
	}

}
