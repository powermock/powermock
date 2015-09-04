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

import javassist.Loader;
import org.powermock.core.WildcardMatcher;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
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
public abstract class DeferSupportingClassLoader extends Loader {
    private Map<String, SoftReference<?>> classes;

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
        classes = new HashMap<String, SoftReference<?>>();
        this.deferPackages = deferPackages;
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        SoftReference<?> reference = classes.get(name);
        if (reference == null || reference.get() == null) {
            final Class<?> clazz;
            if (shouldDefer(deferPackages, name)) {
                clazz = deferTo.loadClass(name);
            } else {
                clazz = loadModifiedClass(name);
            }
            if (resolve) {
                resolveClass(clazz);
            }
            classes.put(name, (reference = new SoftReference<Object>(clazz)));
        }
        return (Class<?>) reference.get();
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
        return wildcardMatch && !(shouldLoadUnmodifiedClass(name) || shouldModifyClass(name));
    }

    protected boolean shouldIgnore(Iterable<String> packages, String name) {
        for (String ignore : packages) {
            if (WildcardMatcher.matches(ignore, name)) {
                return true;
            }
        }
        return false;
    }

    protected boolean shouldIgnore(String[] packages, String name) {
        for (String ignore : packages) {
            if (WildcardMatcher.matches(name, ignore)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the resource with the specified name on the search path.
     *
     * @param name the name of the resource
     * @return a <code>URL</code> for the resource, or <code>null</code> if the
     * resource could not be found.
     */
    protected URL findResource(String name) {
        try {
            return Whitebox.invokeMethod(deferTo, "findResource", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Enumeration<URL> findResources(String name) throws IOException {
        try {
            return Whitebox.invokeMethod(deferTo, "findResources", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public URL getResource(String s) {
        return deferTo.getResource(s);
    }

    public InputStream getResourceAsStream(String s) {
        return deferTo.getResourceAsStream(s);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        // If deferTo is already the parent, then we'd end up returning two copies of each resource...
        if (deferTo.equals(getParent()))
            return deferTo.getResources(name);
        else
            return super.getResources(name);
    }

    protected boolean shouldModify(Iterable<String> packages, String name) {
        return !shouldIgnore(packages, name);
    }

    protected abstract Class<?> loadModifiedClass(String s) throws ClassFormatError, ClassNotFoundException;

    protected abstract boolean shouldModifyClass(String s);

    protected abstract boolean shouldLoadUnmodifiedClass(String className);
}
