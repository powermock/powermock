package org.powermock.modules.junit4.powermockignore;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.PowerMock.createMock;
import static org.powermock.PowerMock.expectNew;
import static org.powermock.PowerMock.replay;
import static org.powermock.PowerMock.verify;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewDemo;
import samples.newmocking.MyClass;

/**
 * Verifies the that the {@link PrepareForTest} annotation has precedence over
 * the {@link PowerMockIgnore} annotation.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewDemo.class)
@PowerMockIgnore("samples.expectnew")
public class PowerMockIgnoreAnnotationPrecedenceWithPrepreForTestAnnotationTest {

	@Test
	public void testNewWithCheckedException() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();

		final String expectedFailMessage = "testing checked exception";
		expectNew(MyClass.class).andThrow(new IOException(expectedFailMessage));

		replay(MyClass.class);

		try {
			tested.throwExceptionAndWrapInRunTimeWhenInvoction();
			fail("Should throw a checked Exception!");
		} catch (RuntimeException e) {
			assertTrue(e.getCause() instanceof IOException);
			assertEquals(expectedFailMessage, e.getMessage());
		}

		verify(MyClass.class);
	}

	@Test
	public void testGetMessage() throws Exception {
		ExpectNewDemo tested = new ExpectNewDemo();
		MyClass myClassMock = createMock(MyClass.class);

		expectNew(MyClass.class).andReturn(myClassMock);

		String expected = "Hello altered World";
		expect(myClassMock.getMessage()).andReturn("Hello altered World");

		replay(myClassMock, MyClass.class);

		String actual = tested.getMessage();

		verify(myClassMock, MyClass.class);
		assertEquals("Expected and actual did not match", expected, actual);
	}

}
