package org.powermock.core.classloader;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.core.classloader.MockClassLoaderFactoryTest.TestContainer.ExceptionTestClass;
import org.powermock.core.classloader.MockClassLoaderFactoryTest.TestContainer.JavassistTestClass;
import org.powermock.core.classloader.MockClassLoaderFactoryTest.TestContainer.PrepareEverythingForTestTestClass;
import org.powermock.core.classloader.MockClassLoaderFactoryTest.TestContainer.SuppressStaticInitializationForTestClass;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import org.powermock.reflect.Whitebox;
import powermock.test.support.MainMockTransformerTestSupport.SupportClasses;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.powermock.core.test.ContainsCondition.contains;


@RunWith(Enclosed.class)
public class MockClassLoaderFactoryTest {
    
    @RunWith(Parameterized.class)
    public static class AnnotationTestOnClassLevelCases extends BasePrepareForTestCases {
    
        public AnnotationTestOnClassLevelCases(final Class<?> testClass, String expectedClassToModify) {
            super(testClass, expectedClassToModify);
        }
    
        @Test
        public void should_extract_classes_to_modify_from_class_level_annotation() {
            final ClassLoader classLoader = objectUnderTest.createForClass();
            
            assertThat(classLoader)
                .as("An instance of MockClassLoader is created")
                .isInstanceOf(MockClassLoader.class);
    
            assertThat(((MockClassLoader) classLoader).getConfiguration())
                .as("MockClassLoader configuration contains expected class: %s", expectedClassToModify)
                .extracting("modify")
                .is(contains(expectedClassToModify));
        }
    }
    
    @RunWith(Parameterized.class)
    public static class AnnotationOnMethodLevelCases extends BasePrepareForTestCases {
    
        @Parameters(name = "Test parameter: {0}")
        public static Collection<Object[]> parameters() {
            final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
        
            parameters.add(new Object[]{JavassistTestClass.class, "powermock.test.support.MainMockTransformerTestSupport$SupportClasses$FinalInnerClass"});
            parameters.add(new Object[]{SuppressStaticInitializationForTestClass.class, "SupportClasses.FinalInnerClass"});
            parameters.add(new Object[]{PrepareEverythingForTestTestClass.class, "*"});
        
            return parameters;
        }
        
        public AnnotationOnMethodLevelCases(final Class<?> testClass, String expectedClassToModify) {
            super(testClass, expectedClassToModify);
        }
    
        @Test
        public void should_extract_classes_to_modify_method_level_annotation_if_exist() {
            final Method method = Whitebox.getMethod(testClass, "someTestWithPrepareForTest");
            
            final ClassLoader classLoader = objectUnderTest.createForMethod(method);
            
            assertThat(classLoader)
                .as("An instance of MockClassLoader is created")
                .isInstanceOf(MockClassLoader.class);
            
            assertThat(((MockClassLoader) classLoader).getConfiguration())
                .as("MockClassLoader configuration contains expected class: %s", expectedClassToModify)
                .extracting("modify")
                .is(contains(expectedClassToModify));
        }
    
    }
    
    @RunWith(Parameterized.class)
    public static class DifferentByteCodeFrameworkCases {
        @Parameters(name = "Test parameter: {0}")
        public static Collection<Object[]> parameters() {
            final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
            
            parameters.add(new Object[]{JavassistTestClass.class, JavassistMockClassLoader.class});
            
            return parameters;
        }
        
        private final Class<?> testClass;
        
        private final Class<? extends MockClassLoader> expectedClassLoaderClass;
        private MockClassLoaderFactory objectUnderTest;
        
        public DifferentByteCodeFrameworkCases(final Class<?> testClass, final Class<? extends MockClassLoader> expectedClassLoaderClass) {
            this.testClass = testClass;
            this.expectedClassLoaderClass = expectedClassLoaderClass;
        }
        
        @Before
        public void setUp() {
            objectUnderTest = new MockClassLoaderFactory(testClass);
        }
        
        @Test
        public void should_create_a_correct_instance_of_class_loader_depends_on_PrepareForTest_parameter_of_class() {
            assertThat(objectUnderTest.createForClass())
                .as("A classloader of the expected classes %s is created.", expectedClassLoaderClass.getName())
                .isExactlyInstanceOf(expectedClassLoaderClass);
        }
        
        @Test
        public void should_create_a_correct_instance_of_class_loader_depends_on_PrepareForTest_parameter_of_method() {
            final Method method = Whitebox.getMethod(testClass, "someTestWithPrepareForTest");
            
            assertThat(objectUnderTest.createForMethod(method))
                .as("A classloader of the expected classes %s is created.", expectedClassLoaderClass.getName())
                .isExactlyInstanceOf(expectedClassLoaderClass);
        }
    
        @Test
        public void should_create_a_correct_instance_of_class_loader_depends_and_use_PrepareForTest_from_class_if_method_does_not_have_annotation() {
        
            final Method method = Whitebox.getMethod(testClass, "someTestWithoutPrepareForTest");
        
            assertThat(objectUnderTest.createForMethod(method))
                .as("A classloader of the expected classes %s is created.", expectedClassLoaderClass.getName())
                .isExactlyInstanceOf(expectedClassLoaderClass);
        }
    }
    
    public static class ExceptionCases{
    
        MockClassLoaderFactory objectUnderTest;
        private Class<?> testClass;
    
        @Before
        public void setUp() {
            testClass =  ExceptionTestClass.class;
            objectUnderTest = new MockClassLoaderFactory(testClass);
        }
    
        @Test
        public void should_throw_exception_if_trying_to_create_an_instance_of_class_loader_for_method_without_annotations_and_class_without_annotation() {
            final Method method = Whitebox.getMethod(testClass, "someTestWithoutPrepareForTest");
        
            assertThatThrownBy(new ThrowingCallable() {
                @Override
                public void call() {
                    objectUnderTest.createForMethod(method);
                }
            }).as("Exception is thrown.")
              .isExactlyInstanceOf(IllegalArgumentException.class);
        
        }

    }
    
    public abstract static class BasePrepareForTestCases {
    
        @Parameters(name = "Test parameter: {0}")
        public static Collection<Object[]> parameters() {
            final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
            
            parameters.add(new Object[]{JavassistTestClass.class, "powermock.test.support.MainMockTransformerTestSupport$SupportClasses"});
            parameters.add(new Object[]{SuppressStaticInitializationForTestClass.class, "SupportClasses.FinalInnerClass"});
            parameters.add(new Object[]{PrepareEverythingForTestTestClass.class, "*"});
            
            return parameters;
        }
    
        MockClassLoaderFactory objectUnderTest;
        
        final String expectedClassToModify;
        final Class<?> testClass;
    
        BasePrepareForTestCases(final Class<?> testClass, String expectedClassToModify) {
            this.testClass = testClass;
            this.expectedClassToModify = expectedClassToModify;
        }
    
        @Before
        public void setUp() {
            objectUnderTest = new MockClassLoaderFactory(testClass);
        }
    }
    
    @SuppressWarnings("WeakerAccess")
    public abstract static class TestContainer {
        
        @PrepareForTest(SupportClasses.class)
        public static class JavassistTestClass {
            
            @Test
            @PrepareForTest(SupportClasses.FinalInnerClass.class)
            public void someTestWithPrepareForTest() {
            }
            
            @Test
            public void someTestWithoutPrepareForTest() {
            }
            
        }
        
        @PrepareEverythingForTest
        public static class PrepareEverythingForTestTestClass {
            
            @Test
            @PrepareEverythingForTest
            public void someTestWithPrepareForTest() {
            }
            
            @Test
            public void someTestWithoutPrepareForTest() {
            }
            
        }
        
        
        @SuppressStaticInitializationFor("SupportClasses.FinalInnerClass")
        public static class SuppressStaticInitializationForTestClass {
            
            @Test
            @SuppressStaticInitializationFor("SupportClasses.FinalInnerClass")
            public void someTestWithPrepareForTest() {
            }
            
            @Test
            public void someTestWithoutPrepareForTest() {
            }
            
        }
        
        public static class ExceptionTestClass {
            @Test
            public void someTestWithoutPrepareForTest() {
            }
        }
    }
}