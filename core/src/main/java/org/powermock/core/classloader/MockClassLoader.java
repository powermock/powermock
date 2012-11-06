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

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.powermock.core.ClassReplicaCreator;
import org.powermock.core.WildcardMatcher;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.core.transformers.MockTransformer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mock all classes except system classes.
 *
 * Notice that there are two different types of classes that are not mocked:
 * <ol>
 * <li>system classes are deferred to another classloader</li>
 * <li>testing framework classes are loaded, but not modified</li>
 * </ol>
 *
 * @author Johan Haleby
 * @author Jan Kronquist
 */
public final class MockClassLoader extends DeferSupportingClassLoader {

    /**
     * Pass this string to the constructor to indicate that all classes should
     * be modified.
     */
    public static final String MODIFY_ALL_CLASSES = "*";

    private List<MockTransformer> mockTransformerChain;

    private Set<String> modify = Collections.synchronizedSet(new HashSet<String>());

    /*
     * Classes not deferred but loaded by the mock class loader but they're not
     * modified.
     */
    private final String[] packagesToLoadButNotModify = new String[] { "org.junit.", "junit.", "org.easymock.", "net.sf.cglib.", "javassist.",
            "org.powermock.modules.junit4.internal.", "org.powermock.modules.junit4.legacy.internal.", "org.powermock.modules.junit3.internal.",
            "org.powermock" };

    private final String[] specificClassesToLoadButNotModify = new String[] { InvocationSubstitute.class.getName(), PowerMockPolicy.class.getName(),
            ClassReplicaCreator.class.getName() };

    /*
     * Classes that should always be deferred regardless of what the user
     * specifies in annotations etc.
     */
    private static final String[] packagesToBeDeferred = new String[] { "org.hamcrest.*", "java.*", "javax.accessibility.*", "sun.*", "org.junit.*",
            "junit.*", "org.pitest.*", "org.powermock.modules.junit4.common.internal.*", "org.powermock.modules.junit3.internal.PowerMockJUnit3RunnerDelegate*",
            "org.powermock.core*" };

    // TODO Why is this needed!? We need to find a better solution.
    final private String ignoredClass = "net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$";
    final private String ignoredClass2 = "net.sf.cglib.core.MethodWrapper$MethodWrapperKey$$KeyFactoryByCGLIB";

    private ClassPool classPool = new ClassPool();

    /**
     * Creates a new instance of the {@link MockClassLoader} based on the
     * following parameters:
     *
     * @param classesToMock
     *            The classes that must be modified to prepare for testability.
     * @param packagesToDefer
     *            Classes in these packages will be defered to the system
     *            class-loader.
     */
    public MockClassLoader(String[] classesToMock, String[] packagesToDefer) {
        super(MockClassLoader.class.getClassLoader(), getPackagesToDefer(packagesToDefer));

        addClassesToModify(classesToMock);
        classPool.appendClassPath(new ClassClassPath(this.getClass()));
    }

    private static String[] getPackagesToDefer(final String[] additionalDeferPackages) {
        final int additionalIgnorePackagesLength = additionalDeferPackages == null ? 0 : additionalDeferPackages.length;
        final int defaultDeferPackagesLength = packagesToBeDeferred.length;
        final int allIgnoreLength = defaultDeferPackagesLength + additionalIgnorePackagesLength;
        final String[] allPackagesToBeIgnored = new String[allIgnoreLength];
        if (allIgnoreLength > defaultDeferPackagesLength) {
            System.arraycopy(packagesToBeDeferred, 0, allPackagesToBeIgnored, 0, defaultDeferPackagesLength);
            System.arraycopy(additionalDeferPackages, 0, allPackagesToBeIgnored, defaultDeferPackagesLength, additionalIgnorePackagesLength);
            return allPackagesToBeIgnored;
        }
        return packagesToBeDeferred;
    }

    /**
     * Creates a new instance of the {@link MockClassLoader} based on the
     * following parameters:
     *
     * @param classesToMock
     *            The classes that must be modified to prepare for testability.
     */
    public MockClassLoader(String[] classesToMock) {
        this(classesToMock, new String[0]);
    }

    /**
     * Add classes that will be loaded by the mock classloader, i.e. these
     * classes will be byte-code manipulated to allow for testing. Any classes
     * contained in the {@link #packagesToBeDeferred} will be ignored. How ever
     * classes added here have precedence over additionally deferred (ignored)
     * packages (those ignored by the user using @PrepareForTest).
     *
     * @param classes
     *            The fully qualified name of the classes that will be appended
     *            to the list of classes that will be byte-code modified to
     *            enable testability.
     */
    public void addClassesToModify(String... classes) {
        for (String clazz : classes) {
            if (!shouldDefer(packagesToBeDeferred, clazz)) {
                modify.add(clazz);
            }
        }
    }

    protected Class<?> loadModifiedClass(String s) throws ClassFormatError, ClassNotFoundException {
        Class<?> loadedClass = null;
        // findSystemClass(s);
        deferTo.loadClass(s);
        if (shouldModify(s) && !shouldLoadModified(s)) {
            loadedClass = loadMockClass(s);
        } else {
            loadedClass = loadUnmockedClass(s);
        }
        return loadedClass;
    }

    private boolean shouldModify(String className) {
        final boolean shouldIgnoreClass = shouldIgnore(deferPackages, className);
        final boolean shouldModifyAll = shouldModifyAll();
        if(shouldModifyAll) {
            return !shouldIgnoreClass;
        } else {
            /* Never mind if we should ignore the class here since
             * classes added by prepared for test should (i.e. those added in "modify")
             * have precedence over ignored packages.
             */
            return WildcardMatcher.matchesAny(modify, className);
        }
    }

    public boolean shouldModifyAll() {
        return (modify.size() == 1 && modify.iterator().next().equals(MODIFY_ALL_CLASSES));
    }

    private Class<?> loadUnmockedClass(String name) throws ClassFormatError, ClassNotFoundException {
        byte bytes[] = null;
        try {
            /*
             * TODO This if-statement is a VERY ugly hack to avoid the
             * java.lang.ExceptionInInitializerError caused by
             * "javassist.NotFoundException:
             * net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$7fb24d72
             * ". This happens after the
             * 
             * se.jayway.examples.tests.privatefield.
             * SimplePrivateFieldServiceClassTest#testUseService(..) tests has
             * been run and all other tests will fail if this class is tried to
             * be loaded. Atm I have found no solution other than this ugly hack
             * to make it work. We really need to investigate the real cause of
             * this behavior.
             */
            if (name.startsWith(ignoredClass) || name.startsWith(ignoredClass2)) {
                // ignore
            } else {
                final CtClass ctClass = classPool.get(name);
                if (ctClass.isFrozen()) {
                    ctClass.defrost();
                }
                bytes = ctClass.toBytecode();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes == null ? null : defineClass(name, bytes, 0, bytes.length);
    }

    /**
     * Load a mocked version of the class.
     */
    private Class<?> loadMockClass(String name) {
        CtClass type = null;
        byte[] clazz = null;

        ClassPool.doPruning = false;
        try {
            type = classPool.get(name);
            for (MockTransformer transformer : mockTransformerChain) {
                type = transformer.transform(type);
            }
            clazz = type.toBytecode();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to transform class with name " + name + ". Reason: " + e.getMessage(), e);
        }

        return defineClass(name, clazz, 0, clazz.length);
    }

    public void setMockTransformerChain(List<MockTransformer> mockTransformerChain) {
        this.mockTransformerChain = mockTransformerChain;
    }

    @Override
    protected boolean shouldModifyClass(String s) {
        return modify.contains(s);
    }

    @Override
    protected boolean shouldLoadUnmodifiedClass(String className) {
        for (String classNameToLoadButNotModify : specificClassesToLoadButNotModify) {
            if (className.equals(classNameToLoadButNotModify)) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldLoadModified(String className) {
        for (String packageToLoadButNotModify : packagesToLoadButNotModify) {
            if (className.startsWith(packageToLoadButNotModify)) {
                return true;
            }
        }
        return false;
    }
}
