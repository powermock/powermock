package org.powermock.tests.utils;

/**
 * Holds various keys that may be put into the MockRepository to store state.
 */
public class Keys {

	/**
	 * Key that can be used to set or get the current test instance in the mock
	 * repository.
	 */
	public static final String CURRENT_TEST_INSTANCE = "powermock.test.instance";

	/**
	 * Key that can be used to set or get the current test method in the mock
	 * repository.
	 */
	public static final String CURRENT_TEST_METHOD = "powermock.test.method";

}
