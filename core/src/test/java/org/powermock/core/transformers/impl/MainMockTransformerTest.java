package org.powermock.core.transformers.impl;

import java.util.Collections;

import javassist.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;

import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

public class MainMockTransformerTest {
    /**
     * This tests that a inner 'public static final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void staticFinalInnerClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.StaticFinalInnerClass.class.getName(), true, mockClassLoader);
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'public final class' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void finalInnerClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.FinalInnerClass.class.getName(), true, mockClassLoader);
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }

    /**
     * This tests that a inner 'enum' can be modified to drop the final modifier. Fixes <a
     * href="http://code.google.com/p/powermock/issues/detail?id=95">Issue 95</a>.
     */
    @Test
    public void enumClassesShouldBecomeNonFinal() throws Exception {
        MockClassLoader mockClassLoader = new MockClassLoader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES });
        mockClassLoader.setMockTransformerChain(Collections.<MockTransformer> singletonList(new MainMockTransformer()));
        Class<?> clazz = Class.forName(SupportClasses.EnumClass.class.getName(), true, mockClassLoader);
        Assert.assertFalse(Modifier.isFinal(clazz.getModifiers()));
    }
}
