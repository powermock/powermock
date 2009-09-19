package org.powermock.api.extension.proxyframework;

import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.Factory;
import org.powermock.reflect.spi.ProxyFramework;

/**
 * CGLib proxy framework setup.
 */
public class ProxyFrameworkImpl implements ProxyFramework {

    /**
     * {@inheritDoc}
     */
    public Class<?> getUnproxiedType(Class<?> type) {
        Class<?> currentType = type;
        while (isProxy(currentType)) {
            for (Class<?> i : currentType.getInterfaces()) {
                if (!i.getName().equals(Factory.class.getName())) {
                    return i;
                }
            }
            currentType = currentType.getSuperclass();
        }
        return currentType;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProxy(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.getName().contains("$$EnhancerByMockitoWithCGLIB$$") || Enhancer.isEnhanced(type);
    }
}
