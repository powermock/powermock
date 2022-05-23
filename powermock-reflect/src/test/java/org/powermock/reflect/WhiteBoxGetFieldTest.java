/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.reflect;

import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test case to demonstrate an issue
 * {@link org.powermock.reflect.internal.WhiteboxImpl#getField(Class, String)}.
 * As the class hierarchy for the given Class is ascended, if the field is not
 * found in the parent class, the interfaces are checked before the grandparent
 * class. The problem is that the getSuperclass() method will always return null
 * for the Class representing the interface. The current implementation of
 * WhiteboxImpl blindly adds this null to the {@code examine} LinkedList.
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

	/**
	 * Verifies that <a
	 * href="http://code.google.com/p/powermock/issues/detail?id=149">issue
	 * 149</a> has been resolved.
	 */
	@Test
	public void testGetField() {
		Field fieldA = WhiteboxImpl.getField(ClassUnderTest.class, "fieldA");
		assertThat(fieldA).isNotNull();
	}
}
