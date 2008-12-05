package org.powermock.tests.utils.impl;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
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
}
