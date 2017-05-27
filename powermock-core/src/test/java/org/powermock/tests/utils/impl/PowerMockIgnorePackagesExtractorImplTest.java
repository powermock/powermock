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

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertEquals;

public class PowerMockIgnorePackagesExtractorImplTest {

    @Test
    public void should_find_ignore_packages_in_the_whole_class_hierarchy() throws Exception {
        final PowerMockIgnorePackagesExtractorImpl tested = new PowerMockIgnorePackagesExtractorImpl();
        final String[] packagesToIgnore = tested.getPackagesToIgnore(IgnoreAnnotatedDemoClass.class);
    
        Assertions.assertThat(packagesToIgnore)
                  .as("Packages added to ignore")
                  .hasSize(4)
                  .containsExactlyInAnyOrder("ignore0", "ignore1","ignore2","ignore3");
    }
    
    @Test
    public void should_scan_interfaces_when_search_package_to_ignore() {
        final PowerMockIgnorePackagesExtractorImpl tested = new PowerMockIgnorePackagesExtractorImpl();
        final String[] packagesToIgnore = tested.getPackagesToIgnore(IgnoreAnnotationFromInterfaces.class);
    
        Assertions.assertThat(packagesToIgnore)
                  .as("Packages from interfaces added to ignore")
                  .hasSize(3)
                  .containsExactlyInAnyOrder("ignore4", "ignore5","ignore6");
        
    }

    @PowerMockIgnore( { "ignore0", "ignore1" })
    private class IgnoreAnnotatedDemoClass extends IgnoreAnnotatedDemoClassParent {

    }

    @PowerMockIgnore("ignore2")
    private class IgnoreAnnotatedDemoClassParent extends IgnoreAnnotatedDemoClassGrandParent {

    }

    @PowerMockIgnore("ignore3")
    private class IgnoreAnnotatedDemoClassGrandParent {

    }
    
    private static class IgnoreAnnotationFromInterfaces implements IgnoreAnnotatedDemoInterfaceParent1, IgnoreAnnotatedDemoInterfaceParent2{
    
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
}
