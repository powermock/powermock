package org.powermock.reflect.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Unit tests specific to the WhiteboxImpl.
 */
public class WhiteboxImplTest {

	/**
	 * Asserts that a previous bug was fixed.
	 */
	@Test
	public void assertThatClassAndNotStringIsNotSameWhenInvokingCheckIfTypesAreSame() throws Exception {
		Method method = WhiteboxImpl.getMethod(WhiteboxImpl.class, "checkIfTypesAreSame", Class[].class, Class[].class);
		boolean invokeMethod = (Boolean) method.invoke(WhiteboxImpl.class, new Class<?>[] { Class.class }, new Class<?>[] { String.class });
		assertFalse(invokeMethod);
	}

	@Test
	public void assertThatClassAndClassIsSameWhenInvokingCheckIfTypesAreSame() throws Exception {
		Method method = WhiteboxImpl.getMethod(WhiteboxImpl.class, "checkIfTypesAreSame", Class[].class, Class[].class);
		boolean invokeMethod = (Boolean) method.invoke(WhiteboxImpl.class, new Class<?>[] { Class.class }, new Class<?>[] { Class.class });
		assertTrue(invokeMethod);
	}
}
