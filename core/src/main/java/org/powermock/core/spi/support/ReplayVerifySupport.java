package org.powermock.core.spi.support;

/**
 * Interface that provides the replay and verify behavior.
 */
public interface ReplayVerifySupport {

	/**
	 * Replay the given object. May throw exception if replay is not needed.
	 * 
	 * @param mock
	 *            The object(s) to replay. May be <code>null</code>.
	 * 
	 * @return the result of the replay (may be <code>null</code>).
	 */
	Object replay(Object... mocks);

	/**
	 * Verify the given object. May throw exception if verify is not needed.
	 * 
	 * @param mock
	 *            The object(s) to verify. May be <code>null</code>.
	 * 
	 * @return the result of the verification (may be <code>null</code>).
	 */
	Object verify(Object... mocks);

}
