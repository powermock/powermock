/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.testng;

import org.powermock.core.MockRepository;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.reporter.MockingFrameworkReporter;
import org.powermock.core.reporter.MockingFrameworkReporterFactory;
import org.powermock.reflect.Whitebox;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * A PowerMock base class that <i>may</i> be used as a base class for all TestNG
 * test cases that uses PowerMock.
 */
public class PowerMockTestCase {

    private Object annotationEnabler;

    private ClassLoader previousCl = null;

    private MockingFrameworkReporter frameworkReporter;

    public PowerMockTestCase() {
        try {
            Class<?> annotationEnablerClass = Class.forName("org.powermock.api.extension.listener.AnnotationEnabler");
            annotationEnabler = Whitebox.newInstance(annotationEnablerClass);
        } catch (ClassNotFoundException e) {
            annotationEnabler = null;
        }
    }

    @BeforeClass
    protected void beforePowerMockTestClass() throws Exception {
        // To make sure that the mock repository is not in an incorrect state when the test begins
        MockRepository.clear();
        if(isLoadedByPowerMockClassloader()) {
            final Thread thread = Thread.currentThread();
            previousCl = thread.getContextClassLoader();
            thread.setContextClassLoader(this.getClass().getClassLoader());
        }
    }

    @AfterClass
    protected void afterPowerMockTestClass() throws Exception {
        if(previousCl != null) {
            Thread.currentThread().setContextClassLoader(previousCl);
        }
    }


    /**
     * Must be executed before each test method. This method does the following:
     * <ol>
     * <li>Injects all mock fields (if they haven't been injected already)</li>
     * </ol>
     *
     *
     *
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    @BeforeMethod
    protected void beforePowerMockTestMethod() throws Exception {
        injectMocks();
        enableReporter();
    }

    private void enableReporter() {
        frameworkReporter = getFrameworkReporterFactory().create();
        frameworkReporter.enable();
    }

    private MockingFrameworkReporterFactory getFrameworkReporterFactory() {
        Class<MockingFrameworkReporterFactory> mockingFrameworkReporterFactoryClass;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            mockingFrameworkReporterFactoryClass = (Class<MockingFrameworkReporterFactory>) classLoader.loadClass("org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                                                   "Extension API internal error: org.powermock.api.org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl could not be located in classpath.");
        }

        return Whitebox.newInstance(mockingFrameworkReporterFactoryClass);
    }

    /**
     * Must be executed after each test method. This method does the following:
     * <ol>
     * <li>Clear all injection fields (those annotated with a Mock annotation)</li>
     * <li>Clears the PowerMock MockRepository</li>
     * </ol>
     *
     *
     *
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    @AfterMethod
    protected void afterPowerMockTestMethod() throws Exception {
        try {
            clearMockFields();
        } finally {
            MockRepository.clear();
        }
        disableReporter();
    }

    private void disableReporter() {
        frameworkReporter.disable();
    }

    /**
     * @return The PowerMock object factory.
     */
    @ObjectFactory
    public IObjectFactory create(ITestContext context) {
        try {
            final Class<?> powerMockObjectFactory = Class.forName("org.powermock.modules.testng.PowerMockObjectFactory");
            return (IObjectFactory) powerMockObjectFactory.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Missing org.powermock.modules.testng.PowerMockObjectFactory in classpath.");
        } catch (Exception e) {
            throw new RuntimeException("PowerMock internal error", e);
        }
    }

    private void clearMockFields() throws Exception, IllegalAccessException {
        if (annotationEnabler != null) {
            final Class<? extends Annotation>[] mockAnnotations = Whitebox.<Class<? extends Annotation>[]> invokeMethod(annotationEnabler,
                    "getMockAnnotations");
            Set<Field> mockFields = Whitebox.getFieldsAnnotatedWith(this, mockAnnotations);
            for (Field field : mockFields) {
                field.set(this, null);
            }
        }
    }

    private void injectMocks() throws Exception {
        if (annotationEnabler != null) {
            Whitebox.invokeMethod(annotationEnabler, "beforeTestMethod", new Class<?>[] { Object.class, Method.class, Object[].class }, this, null,
                    null);
        }
    }

    private boolean isLoadedByPowerMockClassloader() {
        if(this.getClass().getClassLoader() instanceof MockClassLoader) {
            return true;
        }
        return false;
    }

}
