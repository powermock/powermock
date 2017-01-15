package samples.junit4.abstractmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.abstractmocking.AbstractMethodMocking;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractMethodMocking.class)
public class AbstractMethodMockingTest {

	@Test
	public void testMockingOfAbstractMethod() throws Exception {
		final String value = "a string";
		AbstractMethodMocking tested = createPartialMock(
				AbstractMethodMocking.class, "getIt");

		expectPrivate(tested, "getIt").andReturn(value);

		replay(tested);

		assertEquals(value, tested.getValue());

		verify(tested);
	}
}
