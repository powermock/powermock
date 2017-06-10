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
package org.powermock.tests.utils.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.configuration.Configuration;
import org.powermock.configuration.ConfigurationFactory;
import org.powermock.configuration.GlobalConfiguration;
import org.powermock.configuration.PowerMockConfiguration;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.utils.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;

public class PowerMockIgnorePackagesExtractorImplTest {
    
    private PowerMockIgnorePackagesExtractorImpl objectUnderTest;
    
    @Before
    public void setUp() throws Exception {
        GlobalConfiguration.clear();
        objectUnderTest = new PowerMockIgnorePackagesExtractorImpl();
    }
    
    @After
    public void tearDown() throws Exception {
        GlobalConfiguration.clear();
    }
    
    @Test
    public void should_find_ignore_packages_in_the_whole_class_hierarchy() throws Exception {
        final String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(IgnoreAnnotatedDemoClass.class);
        
        assertThat(packagesToIgnore)
            .as("Packages added to ignore")
            .hasSize(4)
            .containsExactlyInAnyOrder("ignore0", "ignore1", "ignore2", "ignore3");
    }
    
    @Test
    public void should_scan_interfaces_when_search_package_to_ignore() {
        final String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(IgnoreAnnotationFromInterfaces.class);
        
        assertThat(packagesToIgnore)
            .as("Packages from interfaces added to ignore")
            .hasSize(3)
            .containsExactlyInAnyOrder("ignore4", "ignore5", "ignore6");
        
    }
    
    @Test
    public void should_include_global_powermock_ignore_to_list_of_package_to_ignore() {
        final String[] globalIgnore = {"org.somepacakge.*", "org.otherpackage.Class"};
        
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                
                return (T) powerMockConfiguration;
            }
        });
        
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(ClassWithoutAnnotation.class);
        
        assertThat(packagesToIgnore)
            .as("Packages from configuration is added to ignore")
            .hasSize(2)
            .containsOnly(globalIgnore);
    }
    
    @Test
    public void should_not_include_global_powermock_ignore_when_annotation_use_global_ignore_false() {
        
        final String[] globalIgnore = {"org.somepacakge.*", "org.otherpackage.Class"};
        
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                
                return (T) powerMockConfiguration;
            }
        });
        
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(ClassWithAnnotationUseFalse.class);
        
        assertThat(packagesToIgnore)
            .as("Packages from global ignore is not added")
            .hasSize(2)
            .containsOnly("ignore6", "ignore5");
    }
    
    @Test
    public void should_not_include_global_powermock_ignore_when_annotation_use_global_ignore_false_on_parent_class() {
        
        final String[] globalIgnore = {"org.somepacakge.*", "org.otherpackage.Class"};
        
        GlobalConfiguration.setConfigurationFactory(new ConfigurationFactory() {
            @Override
            public <T extends Configuration<T>> T create(final Class<T> configurationType) {
                PowerMockConfiguration powerMockConfiguration = new PowerMockConfiguration();
                
                powerMockConfiguration.setGlobalIgnore(globalIgnore);
                
                return (T) powerMockConfiguration;
            }
        });
        
        String[] packagesToIgnore = objectUnderTest.getPackagesToIgnore(IgnoreAnnotatedWithGlobalIgnoreParent.class);
        
        assertThat(packagesToIgnore)
            .as("Packages from global ignore is not added")
            .hasSize(4)
            .containsOnly("ignore0", "ignore1", "ignore6", "ignore5");
    }
    
    private static class ClassWithoutAnnotation {
    
    }
    
    @PowerMockIgnore({"ignore0", "ignore1"})
    private class IgnoreAnnotatedDemoClass extends IgnoreAnnotatedDemoClassParent {
    
    }
    
    @PowerMockIgnore("ignore2")
    private class IgnoreAnnotatedDemoClassParent extends IgnoreAnnotatedDemoClassGrandParent {
    
    }
    
    @PowerMockIgnore("ignore3")
    private class IgnoreAnnotatedDemoClassGrandParent {
    
    }
    
    private static class IgnoreAnnotationFromInterfaces implements IgnoreAnnotatedDemoInterfaceParent1, IgnoreAnnotatedDemoInterfaceParent2 {
    
    }
    
    @PowerMockIgnore("ignore5")
    private interface IgnoreAnnotatedDemoInterfaceGrandParent {
    
    }
    
    @PowerMockIgnore("ignore4")
    private interface IgnoreAnnotatedDemoInterfaceParent1 extends IgnoreAnnotatedDemoInterfaceGrandParent {
    
    }
    
    @PowerMockIgnore("ignore6")
    private interface IgnoreAnnotatedDemoInterfaceParent2 extends IgnoreAnnotatedDemoInterfaceGrandParent {
    }
    
    @PowerMockIgnore(value = "ignore6", globalIgnore = false)
    private class ClassWithAnnotationUseFalse implements IgnoreAnnotatedDemoInterfaceGrandParent {
    
    }
    
    @PowerMockIgnore({"ignore0", "ignore1"})
    private class IgnoreAnnotatedWithGlobalIgnoreParent extends ClassWithAnnotationUseFalse {
        
    }
}
