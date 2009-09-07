package org.powermock.reflect;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

/**
 * A test case to demonstrate an issue
 * {@link org.powermock.reflect.internal.WhiteboxImpl#getField(Class, String)}.
 * As the class hierarchy for the given Class is ascended, if the field is not
 * found in the parent class, the interfaces are checked before the grandparent
 * class. The problem is that the getSuperclass() method will always return null
 * for the Class representing the interface. The current implementation of
 * WhiteboxImpl blindly adds this null to the <code>examine</code> LinkedList.
 * 
 * @author Ben Chatelain
 */
public class WhiteBoxGetFieldTest {

	interface SomeInterface {
	}

	class GrandParentClass {
		@SuppressWarnings("unused")
		private String fieldA;
	}

	class ParentClass extends GrandParentClass {
	}

	class ClassUnderTest extends ParentClass implements SomeInterface {
		@SuppressWarnings("unused")
		private String fieldB;
	}

	@Test
	public void testGetField() {
		Field fieldA = WhiteboxImpl.getField(ClassUnderTest.class, "fieldA");
		Assert.assertNotNull(fieldA);
	}
}
