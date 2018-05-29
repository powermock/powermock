/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.classloader;

import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Defers classloading of system classes to a delegate.
 *
 * @author Johan Haleby
 * @author Jan Kronquist
 * @author Arthur Zagretdinov
 */
abstract class DeferSupportingClassLoader extends ClassLoader {
    
    private final ConcurrentMap<String, SoftReference<Class<?>>> classes;
    private final ConcurrentMap<String, Object> parallelLockMap;
    
    private final MockClassLoaderConfiguration configuration;
    
    ClassLoader deferTo;
    
    DeferSupportingClassLoader(ClassLoader classloader, MockClassLoaderConfiguration configuration) {
        
        this.configuration = configuration;
        this.classes = new ConcurrentHashMap<String, SoftReference<Class<?>>>();
        
        if (classloader == null) {
            deferTo = ClassLoader.getSystemClassLoader();
        } else {
            deferTo = classloader;
        }
        parallelLockMap = new ConcurrentHashMap<String,Object>();
    }
    
    @Override
    public URL getResource(String s) {
        return deferTo.getResource(s);
    }
    
    @Override
    public InputStream getResourceAsStream(String s) {
        return deferTo.getResourceAsStream(s);
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        // If deferTo is already the parent, then we'd end up returning two copies of each resource...
        if (deferTo.equals(getParent())) {
            return deferTo.getResources(name);
        }
        else {
            return super.getResources(name);
        }
    }
    
    public MockClassLoaderConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Register a class to the cache of this classloader
     */
    public void cache(Class<?> cls) {
        if (cls != null) {
            classes.put(cls.getName(), new SoftReference<Class<?>>(cls));
        }
    }
    
    protected abstract Class<?> loadClassByThisClassLoader(String s) throws ClassFormatError, ClassNotFoundException;
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass1(name);
            if (clazz == null) {
                clazz = loadClass1(name, resolve);
            }
            return clazz;
        }
    }
    
    protected Object getClassLoadingLock(String className) {
        Object lock = this;
        if (parallelLockMap != null) {
            Object newLock = new Object();
            lock = parallelLockMap.putIfAbsent(className, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }
        return lock;
    }
    
    /**
     * Finds the resource with the specified name on the search path.
     *
     * @param name the name of the resource
     * @return a {@code URL} for the resource, or {@code null} if the
     * resource could not be found.
     */
    @Override
    protected URL findResource(String name) {
        try {
            return Whitebox.invokeMethod(deferTo, "findResource", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        try {
            return Whitebox.invokeMethod(deferTo, "findResources", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Class<?> loadClass1(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        if (shouldDefer(name)) {
            clazz = loadByDeferClassLoader(name);
        } else {
            clazz = loadClassByThisClassLoader(name);
        }
        if (resolve) {
            resolveClass(clazz);
        }
        classes.put(name, new SoftReference<Class<?>>(clazz));
        return clazz;
    }
    
    private Class<?> loadByDeferClassLoader(final String name) throws ClassNotFoundException {
        final Class<?> clazz;
        clazz = deferTo.loadClass(name);
        return clazz;
    }
    
    private boolean shouldDefer(String name) {
        return configuration.shouldDefer(name);
    }
    
    private Class<?> findLoadedClass1(String name) {SoftReference<Class<?>> reference = classes.get(name);
        Class<?> clazz = null;
        if (reference != null) {
            clazz = reference.get();
        }
        if (clazz == null) {
            clazz = findLoadedClass(name);
        }
        return clazz;
    }
}
