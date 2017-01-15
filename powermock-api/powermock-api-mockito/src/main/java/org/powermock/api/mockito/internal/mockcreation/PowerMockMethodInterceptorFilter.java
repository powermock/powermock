package org.powermock.api.mockito.internal.mockcreation;

import org.mockito.cglib.proxy.MethodProxy;
import org.mockito.internal.InternalMockHandler;
import org.mockito.mock.MockCreationSettings;
import org.powermock.api.mockito.repackaged.MethodInterceptorFilter;

import java.lang.reflect.Method;

class PowerMockMethodInterceptorFilter extends MethodInterceptorFilter {

    public PowerMockMethodInterceptorFilter(InternalMockHandler handler,
            MockCreationSettings mockSettings) {
        super(handler, mockSettings);
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args,
            MethodProxy methodProxy) throws Throwable {
        Object intercept = super.intercept(proxy, method, args, methodProxy);
        if ("finalize".equals(method.getName())) {
            MockitoStateCleaner cleaner = new MockitoStateCleaner();
            cleaner.clearConfiguration();
            cleaner.clearMockProgress();
        }
        return intercept;
    }

}
