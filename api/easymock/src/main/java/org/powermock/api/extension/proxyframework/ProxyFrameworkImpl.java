package org.powermock.api.extension.proxyframework;

import net.sf.cglib.proxy.Enhancer;
import org.powermock.reflect.spi.ProxyFramework;

/**
 * CGLib proxy framework setup.
 */
public class ProxyFrameworkImpl implements ProxyFramework {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getUnproxiedType(Class<?> type) {
        Class<?> currentType = type;
        while (isProxy(currentType)) {
            currentType = currentType.getSuperclass();
        }
        return currentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProxy(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.getName().contains("$$EnhancerByCGLIB$$") || Enhancer.isEnhanced(type);
    }
}
