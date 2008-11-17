package org.powermock.modules.junit4.expectnew;

import static org.powermock.PowerMock.createMockAndExpectNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import samples.expectnew.PrimitiveAndWrapperDemo;

/**
 * Unit test for the {@link PrimitiveAndWrapperDemo} class.
 */
@RunWith(PowerMockRunner.class)
public class PrimitiveAndWrapperDemoTest {

	@Test
	public void testWhenConstructorCannotBeDetermined() throws Exception {
		try {
			createMockAndExpectNew(PrimitiveAndWrapperDemo.class, 2);
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're refering to."
							+ "\nMatching constructors in class samples.expectnew.PrimitiveAndWrapperDemo were:\n"
							+ "samples.expectnew.PrimitiveAndWrapperDemo( int.class )\n"
							+ "samples.expectnew.PrimitiveAndWrapperDemo( java.lang.Integer.class )\n", e.getMessage());
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
