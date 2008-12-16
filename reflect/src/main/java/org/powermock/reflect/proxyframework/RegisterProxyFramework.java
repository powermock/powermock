package org.powermock.reflect.proxyframework;

import org.powermock.reflect.internal.WhiteboxImpl;
import org.powermock.reflect.spi.ProxyFramework;

/**
 * All API's must register a proxy framework using this class.
 */
public class RegisterProxyFramework {

	/**
	 * Register a proxy framework.
	 * 
	 * @param proxyFramework
	 *            The proxy framework to register.
	 */
	public static void registerProxyFramework(ProxyFramework proxyFramework) {
		WhiteboxImpl.setInternalState(WhiteboxImpl.class, proxyFramework);
	}
}
