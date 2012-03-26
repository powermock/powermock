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

import org.powermock.core.WildcardMatcher;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Defers classloading of system classes to a delegate.
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
public abstract class DeferSupportingClassLoader extends ClassLoader {
    private Map<String, Class<?>> classes;

    String deferPackages[];

    ClassLoader deferTo;

    public void addIgnorePackage(String... packagesToIgnore) {
        if (packagesToIgnore != null && packagesToIgnore.length > 0) {
            final int previousLength = deferPackages.length;
            String[] newDeferPackages = new String[previousLength + packagesToIgnore.length];
            System.arraycopy(deferPackages, 0, newDeferPackages, 0, previousLength);
            System.arraycopy(packagesToIgnore, 0, newDeferPackages, previousLength, packagesToIgnore.length);
            deferPackages = newDeferPackages;
        }
    }

    public DeferSupportingClassLoader(ClassLoader classloader, String deferPackages[]) {
        if (classloader == null) {
            deferTo = ClassLoader.getSystemClassLoader();
        } else {
            deferTo = classloader;
        }
        classes = new HashMap<String, Class<?>>();
        this.deferPackages = deferPackages;
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        if ((clazz = (Class<?>) classes.get(name)) == null) {
            final boolean shouldDefer = shouldDefer(deferPackages, name);
            if (shouldDefer) {
                clazz = deferTo.loadClass(name);
            } else {
                clazz = loadModifiedClass(name);
            }
            if (resolve) {
                resolveClass(clazz);
            }
        }

        classes.put(name, clazz);
        return clazz;
    }

    protected boolean shouldDefer(String[] packages, String name) {
        for (String packageToCheck : packages) {
            if (deferConditionMatches(name, packageToCheck)) {
                return true;
            }
        }
        return false;
    }

    private boolean deferConditionMatches(String name, String packageName) {
        final boolean wildcardMatch = WildcardMatcher.matches(name, packageName);
        return wildcardMatch && !(shouldLoadUnmodifiedClass(name) || wildcardMatch && shouldModifyClass(name));
    }

    protected boolean shouldIgnore(Iterable<String> packages, String name) {
        synchronized (packages) {
            for (String ignore : packages) {
                if (WildcardMatcher.matches(ignore, name)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean shouldIgnore(String[] packages, String name) {
        synchronized (packages) {
            for (String ignore : packages) {
                if (WildcardMatcher.matches(name, ignore)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the resource with the specified name on the search path.
     * 
     * @param name
     *            the name of the resource
     * @return a <code>URL</code> for the resource, or <code>null</code> if the
     *         resource could not be found.
     */
    @Override
    protected URL findResource(String name) {
        try {
            return Whitebox.<URL> invokeMethod(deferTo, "findResource", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
	protected Enumeration<URL> findResources(String name) throws IOException {
        try {
            return Whitebox.<Enumeration<URL>> invokeMethod(deferTo, "findResources", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

    protected boolean shouldModify(Iterable<String> packages, String name) {
        return !shouldIgnore(packages, name);
    }

    protected abstract Class<?> loadModifiedClass(String s) throws ClassFormatError, ClassNotFoundException;

    protected abstract boolean shouldModifyClass(String s);

    protected abstract boolean shouldLoadUnmodifiedClass(String className);
}
