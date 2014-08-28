package samples.junit4.expectnew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.exceptions.TooManyConstructorsFoundException;
import samples.expectnew.PrimitiveAndWrapperDemo;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.powermock.api.easymock.PowerMock.createMockAndExpectNew;

/**
 * Unit test for the {@link PrimitiveAndWrapperDemo} class.
 */
@RunWith(PowerMockRunner.class)
public class PrimitiveAndWrapperDemoTest {

	@Test
	public void testWhenConstructorCannotBeDetermined() throws Exception {
		try {
			createMockAndExpectNew(PrimitiveAndWrapperDemo.class, 2);
			fail("Should throw TooManyConstructorsFoundException");
		} catch (TooManyConstructorsFoundException e) {
			assertThat(e.getMessage(), containsString("Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're referring to."
                    + "\nMatching constructors in class samples.expectnew.PrimitiveAndWrapperDemo were:\n"));
            assertThat(e.getMessage(), containsString("samples.expectnew.PrimitiveAndWrapperDemo( java.lang.Integer.class )\n"));
            assertThat(e.getMessage(), containsString("samples.expectnew.PrimitiveAndWrapperDemo( int.class )\n"));
		}
	}

	@Test
	public void testWrapperConstructor() throws Exception {
		createMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[] { Integer.class }, 2);
	}

	@Test
	public void testPrimitiveConstructor() throws Exception {
		createMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[] { int.class }, 2);
	}
}
