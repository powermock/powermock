/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    @Override
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
    @Override
    public boolean isProxy(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.getName().contains("$$EnhancerByMockitoWithCGLIB$$") || Enhancer.isEnhanced(type);
    }
}
