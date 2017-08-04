/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.classloader;

import org.powermock.core.InvocationException;

import java.util.concurrent.Callable;

// We change the context classloader to the current CL in order for the Mockito
// framework to load it's plugins (such as MockMaker) correctly.
public class ClassloaderWrapper {
    
    public static void runWithClass(final Runnable runnable) {
        runWithClassClassLoader(ClassloaderWrapper.class.getClassLoader(), runnable);
    }
    
    public static void runWithClassClassLoader(final ClassLoader classLoader, final Runnable runnable) {
        final ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    public static <V> V runWithClass(final Callable<V> callable) {
        return runWithClassClassLoader(ClassloaderWrapper.class.getClassLoader(), callable);
    }
    
    public static <V> V runWithClassClassLoader(final ClassLoader classLoader, final Callable<V> callable) {
        final ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new InvocationException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
}
