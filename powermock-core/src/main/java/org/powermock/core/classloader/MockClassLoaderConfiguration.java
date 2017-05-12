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

import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.WildcardMatcher;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.utils.ArrayUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.powermock.core.classloader.MockClassLoader.MODIFY_ALL_CLASSES;

/**
 * The instance of the class provides information about classes which have to be mocked, loaded without modification
 * or defer to system class loader.
 */
public class MockClassLoaderConfiguration {
    
    /*
     * Classes that should always be deferred regardless of what the user
     * specifies in annotations etc.
     */
    static final String[] PACKAGES_TO_BE_DEFERRED = new String[]{
        "org.hamcrest.*",
        "java.*",
        "javax.accessibility.*",
        "sun.*",
        "org.junit.*",
        "org.testng.*",
        "junit.*",
        "org.pitest.*",
        "org.powermock.modules.junit4.common.internal.*",
        "org.powermock.modules.junit3.internal.PowerMockJUnit3RunnerDelegate*",
        "org.powermock.core*",
        "org.jacoco.agent.rt.*"
    };
    
    /*
     * Classes not deferred but loaded by the mock class loader but they're not
     * modified.
     */
    private static final String[] PACKAGES_TO_LOAD_BUT_NOT_MODIFY = new String[]{
        "org.junit.",
        "junit.",
        "org.testng.",
        "org.easymock.",
        "net.sf.cglib.",
        "javassist.",
        "org.powermock.modules.junit4.internal.",
        "org.powermock.modules.junit4.legacy.internal.",
        "org.powermock.modules.junit3.internal.",
        "org.powermock"};
    
    private final String[] specificClassesToLoadButNotModify = new String[]{
        InvocationSubstitute.class.getName(),
        PowerMockPolicy.class.getName(),
        ClassReplicaCreator.class.getName()
    };
    
    private final Set<String> modify = Collections.synchronizedSet(new HashSet<String>());
    private String[] deferPackages;
    
    /**
     * Create an instance of configuration without any classes to mock or ignore.
     */
    public MockClassLoaderConfiguration() {
        this(new String[0], new String[0]);
    }
    
    /**
     * Create an instance of configuration
     * @param classesToMock classes that should be modified by {@link MockClassLoader}.
     * @param packagesToDefer classes/packages that should be deferred to system class loader.
     */
    public MockClassLoaderConfiguration(String[] classesToMock, String[] packagesToDefer) {
        deferPackages = getPackagesToDefer(packagesToDefer);
        addClassesToModify(classesToMock);
    }
    
    /**
     * Add packages or classes to ignore. Loading of all classes that locate in the added packages will be delegate to a system classloader.
     * <p>
     * Package should be specified with using mask. Example:
     * </p>
     * <pre>
     *     configuration.addIgnorePackage("org.powermock.example.*");
     * </pre>
     *
     * @param packagesToIgnore fully qualified names of classes or names of packages that end by <code>.*</code>
     */
    public void addIgnorePackage(String... packagesToIgnore) {
        if (packagesToIgnore != null && packagesToIgnore.length > 0) {
            final int previousLength = deferPackages.length;
            String[] newDeferPackages = new String[previousLength + packagesToIgnore.length];
            System.arraycopy(deferPackages, 0, newDeferPackages, 0, previousLength);
            System.arraycopy(packagesToIgnore, 0, newDeferPackages, previousLength, packagesToIgnore.length);
            deferPackages = newDeferPackages;
        }
    }
    
    /**
     * Add classes that will be loaded by the mock classloader, i.e. these
     * classes will be byte-code manipulated to allow for testing. Any classes
     * contained in the {@link #PACKAGES_TO_BE_DEFERRED} will be ignored. How ever
     * classes added here have precedence over additionally deferred (ignored)
     * packages (those ignored by the user using @PrepareForTest).
     *
     * @param classes The fully qualified name of the classes that will be appended
     *                to the list of classes that will be byte-code modified to
     *                enable testability.
     */
    public final void addClassesToModify(String... classes) {
        if (classes != null) {
            for (String clazz : classes) {
                if (!shouldDefer(PACKAGES_TO_BE_DEFERRED, clazz)) {
                    modify.add(clazz);
                }
            }
        }
    }
    
    boolean shouldDefer(String className) {
        return shouldDefer(deferPackages, className);
    }
    
    boolean shouldMockClass(String className) {
        return shouldModify(className) && !shouldLoadWithMockClassloaderWithoutModifications(className);
    }
    
    String[] getDeferPackages() {
        return ArrayUtil.clone(deferPackages);
    }
    
    private boolean shouldDefer(String[] packages, String name) {
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
    
    private boolean shouldIgnore(String[] packages, String name) {
        for (String ignore : packages) {
            if (WildcardMatcher.matches(name, ignore)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldLoadUnmodifiedClass(String className) {
        for (String classNameToLoadButNotModify : specificClassesToLoadButNotModify) {
            if (className.equals(classNameToLoadButNotModify)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldLoadWithMockClassloaderWithoutModifications(String className) {
        if (className.startsWith("org.powermock.example")) {
            return false;
        }
        for (String packageToLoadButNotModify : PACKAGES_TO_LOAD_BUT_NOT_MODIFY) {
            if (className.startsWith(packageToLoadButNotModify)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldModifyClass(String className) {
        return modify.contains(className);
    }
    
    private boolean shouldIgnore(String className) {
        return shouldIgnore(deferPackages, className);
    }
    
    boolean shouldModify(String className) {
        final boolean shouldIgnoreClass = shouldIgnore(className);
        final boolean shouldModifyAll = shouldModifyAll();
        if (shouldModifyAll) {
            return !shouldIgnoreClass;
        } else {
            /*
             * Never mind if we should ignore the class here since
             * classes added by prepared for test should (i.e. those added in "modify")
             * have precedence over ignored packages.
             */
            return WildcardMatcher.matchesAny(modify, className);
        }
    }
    
    private boolean shouldModifyAll() {
        return (modify.size() == 1 && modify.iterator().next().equals(MODIFY_ALL_CLASSES));
    }
    
    private static String[] getPackagesToDefer(final String[] additionalDeferPackages) {
        final int additionalIgnorePackagesLength = additionalDeferPackages == null ? 0 : additionalDeferPackages.length;
        final int defaultDeferPackagesLength = PACKAGES_TO_BE_DEFERRED.length;
        final int allIgnoreLength = defaultDeferPackagesLength + additionalIgnorePackagesLength;
        final String[] allPackagesToBeIgnored = new String[allIgnoreLength];
        if (allIgnoreLength > defaultDeferPackagesLength) {
            System.arraycopy(PACKAGES_TO_BE_DEFERRED, 0, allPackagesToBeIgnored, 0, defaultDeferPackagesLength);
            System.arraycopy(additionalDeferPackages != null ? additionalDeferPackages : new String[0], 0, allPackagesToBeIgnored, defaultDeferPackagesLength,
                             additionalIgnorePackagesLength);
            return allPackagesToBeIgnored;
        }
        return PACKAGES_TO_BE_DEFERRED;
    }
}
