package org.powermock.api.mockito.internal.mockcreation;

import org.mockito.internal.configuration.GlobalConfiguration;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.powermock.api.support.ClassLoaderUtil;
import org.powermock.reflect.Whitebox;

class MockitoStateCleaner {
    
    void clearMockProgress() {
        clearThreadLocalIn(ThreadSafeMockingProgress.class);
    }

    void clearConfiguration() {
        clearThreadLocalIn(GlobalConfiguration.class);
    }

    private void clearThreadLocalIn(Class<?> cls) {
        Whitebox.getInternalState(cls, ThreadLocal.class).set(null);
        final Class<?> clazz;
        if(ClassLoaderUtil.hasClass(cls, ClassLoader.getSystemClassLoader())) {
            clazz = ClassLoaderUtil.loadClass(cls, ClassLoader.getSystemClassLoader());
        } else {
            clazz = ClassLoaderUtil.loadClass(cls, cls.getClassLoader());
        }
        Whitebox.getInternalState(clazz, ThreadLocal.class).set(null);
    }
    
    
}
