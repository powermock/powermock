package org.powermock.api.extension.proxyframework;

import org.powermock.reflect.spi.ProxyFramework;

/**
 *
 */
public class ProxyFrameworkImpl implements ProxyFramework {
    @Override
    public Class<?> getUnproxiedType(Class<?> type) {
        return type;
    }

    @Override
    public boolean isProxy(Class<?> type) {
        return false;
    }
}
