package org.powermock.core.classloader;

import org.powermock.core.classloader.annotations.UseClassPathAdjuster;
import org.powermock.core.classloader.bytebuddy.ByteBuddyMockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;

public enum ByteCodeFramework {
    Javassist {
        @Override
        MockClassLoader createClassloader(final MockClassLoaderConfiguration configuration,
                                          final UseClassPathAdjuster useClassPathAdjuster) {
            return new JavassistMockClassLoader(configuration, useClassPathAdjuster);
        }
    },
    ByteBuddy {
        @Override
        MockClassLoader createClassloader(final MockClassLoaderConfiguration configuration,
                                          final UseClassPathAdjuster useClassPathAdjuster) {
            return new ByteBuddyMockClassLoader(configuration);
        }
    };
    
    abstract MockClassLoader createClassloader(MockClassLoaderConfiguration configuration, final UseClassPathAdjuster useClassPathAdjuster);
}
