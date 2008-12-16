package org.powermock.core.spi;

/**
 * Interface that provides the replay, verify and reset behavior for mock
 * objects and classes.
 */
public interface DefaultBehavior {

	/**
	 * Replay the given objects or classes. May throw exception if replay is not
	 * needed or not supported.
	 * 
	 * @param mock
	 *            The object(s) to replay. May be <code>null</code>.
	 * 
	 * @return the result of the replay (may be <code>null</code>).
	 */
	Object replay(Object... mocks);

	/**
	 * Verify the given objects or classes. May throw exception if verify is not
	 * needed or not supported.
	 * 
	 * @param mock
	 *            The object(s) to verify. May be <code>null</code>.
	 * 
	 * @return the result of the verification (may be <code>null</code>).
	 */
	Object verify(Object... mocks);

	/**
	 * Reset the given objects or classes. May throw exception if reset is not
	 * needed or not supported.
	 * 
	 * @param mock
	 *            The object(s) to replay. May be <code>null</code>.
	 * 
	 * @return the result of the replay (may be <code>null</code>).
	 */
	Object reset(Object... mocks);

}
