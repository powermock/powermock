package org.powermock.tests.utils.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

/**
 * Unit tests for the {@link PrepareForTestExtractorImpl} class.
 * 
 */
public class PrepareForTestExtractorImplTest {

	/**
	 * Makes sure that issue <a
	 * href="http://code.google.com/p/powermock/issues/detail?id=81">81</a> is
	 * solved.
	 */
	@Test
	public void assertThatInterfacesWorks() throws Exception {
		final PrepareForTestExtractorImpl tested = new PrepareForTestExtractorImpl();
		final Set<String> hashSet = new HashSet<String>();
		Whitebox.invokeMethod(tested, "addClassHierarchy", hashSet, Serializable.class);
		assertEquals(1, hashSet.size());
		assertEquals(Serializable.class.getName(), hashSet.iterator().next());
	}

	/**
	 * Makes sure that issue <a
	 * href="http://code.google.com/p/powermock/issues/detail?id=111">111</a> is
	 * solved.
	 */
	@Test
	public void shouldFindClassesToPrepareForTestInTheWholeClassHierarchy() throws Exception {
		final PrepareForTestExtractorImpl tested = new PrepareForTestExtractorImpl();
		final String[] classesToPrepare = tested.getTestClasses(DemoClassForPrepareForTest.class);
		assertEquals(5, classesToPrepare.length);
		final String classPrefix = "Class";
		final List<String> classesToPrepareList = Arrays.asList(classesToPrepare);
		for (int i = 0; i < classesToPrepare.length; i++) {
			assertTrue(classesToPrepareList.contains(classPrefix + (i + 1)));
		}
	}
}

@PrepareForTest(fullyQualifiedNames = { "Class1", "Class2" })
class DemoClassForPrepareForTest extends DemoClassForPrepareForTestParent {

}

@PrepareForTest(fullyQualifiedNames = { "Class3" })
class DemoClassForPrepareForTestParent extends DemoClassForPrepareForTestGrandParent {

}

@PrepareForTest(fullyQualifiedNames = { "Class4", "Class5" })
class DemoClassForPrepareForTestGrandParent {

}