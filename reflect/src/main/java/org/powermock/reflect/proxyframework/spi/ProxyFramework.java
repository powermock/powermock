package org.powermock.reflect.proxyframework.spi;

/**
 * The purpose of a the proxy framework implementation is to return the
 * unproxied types of classes.
 */
public interface ProxyFramework {

	/**
	 * Check if the class is a proxy and if it is return the unproxied type.
	 * 
	 * @param type
	 *            The class to check.
	 * @return The unproxied class type.
	 */
	Class<?> getUnproxiedType(Class<?> type);

	/**
	 * @return <code>true</code> if <tt>type</tt> is a proxy, <code>false</code>
	 *         otherwise.
	 */
	boolean isProxy(Class<?> type);
}
