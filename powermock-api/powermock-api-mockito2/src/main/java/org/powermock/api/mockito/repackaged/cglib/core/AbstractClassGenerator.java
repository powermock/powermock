/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.ClassReader;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Abstract class for all code-generating CGLIB utilities.
 * In addition to caching generated classes for performance, it provides hooks for
 * customizing the {@code ClassLoader}, name of the generated class, and transformations
 * applied before generation.
 */
abstract public class AbstractClassGenerator
implements ClassGenerator
{
    private static final Object NAME_KEY = new Object();
    private static final ThreadLocal CURRENT = new ThreadLocal();

    private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
    private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;
    private Source source;
    private ClassLoader classLoader;
    private String namePrefix;
    private Object key;
    private boolean useCache = true;
    private String className;
    private boolean attemptLoad;

    protected AbstractClassGenerator(Source source) {
        this.source = source;
    }

    /**
     * Used internally by CGLIB. Returns the {@code AbstractClassGenerator}
     * that is being used to generate a class in the current thread.
     */
    public static AbstractClassGenerator getCurrent() {
        return (AbstractClassGenerator)CURRENT.get();
    }

    protected void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    final protected String getClassName() {
        if (className == null)
            className = getClassName(getClassLoader());
        return className;
    }

    private String getClassName(final ClassLoader loader) {
        final Set nameCache = getClassNameCache(loader);
        return namingPolicy.getClassName(namePrefix, source.name, key, new Predicate() {
            public boolean evaluate(Object arg) {
                return nameCache.contains(arg);
            }
        });
    }

    private Set getClassNameCache(ClassLoader loader) {
        return (Set)((Map)source.cache.get(loader)).get(NAME_KEY);
    }

    /**
     * @see #setNamingPolicy
     */
    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    /**
     * Override the default naming policy.
     * @see DefaultNamingPolicy
     * @param namingPolicy the custom policy, or null to use the default
     */
    public void setNamingPolicy(NamingPolicy namingPolicy) {
        if (namingPolicy == null)
            namingPolicy = DefaultNamingPolicy.INSTANCE;
        this.namingPolicy = namingPolicy;
    }

    /**
     * @see #setUseCache
     */
    public boolean getUseCache() {
        return useCache;
    }

    /**
     * Whether use and update the static cache of generated classes
     * for a class with the same properties. Default is {@code true}.
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean getAttemptLoad() {
        return attemptLoad;
    }

    /**
     * If set, CGLIB will attempt to load classes from the specified
     * {@code ClassLoader} before generating them. Because generated
     * class names are not guaranteed to be unique, the default is {@code false}.
     */
    public void setAttemptLoad(boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }

    /**
     * @see #setStrategy
     */
    public GeneratorStrategy getStrategy() {
        return strategy;
    }
    
    /**
     * Set the strategy to use to create the bytecode from this generator.
     * By default an instance of {@see DefaultGeneratorStrategy} is used.
     */
    public void setStrategy(GeneratorStrategy strategy) {
        if (strategy == null)
            strategy = DefaultGeneratorStrategy.INSTANCE;
        this.strategy = strategy;
    }

    public ClassLoader getClassLoader() {
        ClassLoader t = classLoader;
        if (t == null) {
            t = getDefaultClassLoader();
        }
        if (t == null) {
            t = getClass().getClassLoader();
        }
        if (t == null) {
            t = Thread.currentThread().getContextClassLoader();
        }
        if (t == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return t;
    }

    /**
     * Set the {@code ClassLoader} in which the class will be generated.
     * Concrete subclasses of {@code AbstractClassGenerator} (such as {@code Enhancer})
     * will try to choose an appropriate default if this is unset.
     * <p>
     * Classes are cached per-{@code ClassLoader} using a {@code WeakHashMap}, to allow
     * the generated classes to be removed when the associated loader is garbage collected.
     * @param classLoader the loader to generate the new class with, or null to use the default
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    abstract protected ClassLoader getDefaultClassLoader();

    protected Object create(Object key) {
        try {
        	Class gen = null;

            synchronized (source) {
                ClassLoader loader = getClassLoader();
                Map cache2 = null;
                cache2 = (Map)source.cache.get(loader);
                if (cache2 == null) {
                    cache2 = new HashMap();
                    cache2.put(NAME_KEY, new HashSet());
                    source.cache.put(loader, cache2);
                } else if (useCache) {
                    Reference ref = (Reference)cache2.get(key);
                    gen = (Class) (( ref == null ) ? null : ref.get());
                }
                if (gen == null) {
                    Object save = CURRENT.get();
                    CURRENT.set(this);
                    try {
                        this.key = key;

                        if (attemptLoad) {
                            try {
                                gen = loader.loadClass(getClassName());
                            } catch (ClassNotFoundException e) {
                                // ignore
                            }
                        }
                        if (gen == null) {
                            byte[] b = strategy.generate(this);
                            String className = ClassNameReader.getClassName(new ClassReader(b));
                            getClassNameCache(loader).add(className);
                            gen = ReflectUtils.defineClass(className, b, loader);
                        }

                        if (useCache) {
                            cache2.put(key, new WeakReference(gen));
                        }
                        return firstInstance(gen);
                    } finally {
                        CURRENT.set(save);
                    }
                }
            }
            return firstInstance(gen);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }

    abstract protected Object firstInstance(Class type) throws Exception;

    abstract protected Object nextInstance(Object instance) throws Exception;

    protected static class Source {
        String name;
        Map cache = new WeakHashMap();
        public Source(String name) {
            this.name = name;
        }
    }
}
