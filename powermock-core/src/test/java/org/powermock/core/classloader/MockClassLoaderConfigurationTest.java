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


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MockClassLoaderConfigurationTest {
    
    private MockClassLoaderConfiguration configuration;
    
    @Before
    public void setUp() throws Exception {
        configuration = new MockClassLoaderConfiguration();
    }
    
    @Test
    public void should_add_ignoredPackage_to_defer() {
        final String packageToIgnore = "test*";
        
        configuration.addIgnorePackage(packageToIgnore);
        
        String[] deferPackages = configuration.getDeferPackages();
        
        assertThat(deferPackages)
            .hasSize(MockClassLoaderConfiguration.PACKAGES_TO_BE_DEFERRED.length + 1)
            .contains(packageToIgnore);
    }
    
    @Test
    public void classes_to_modify_should_have_precedence_over_package_to_ignore() throws Exception {
        
        configuration.addClassesToModify("org.mytest.myclass");
        configuration.addIgnorePackage("*mytest*");
        
        assertThat(configuration.shouldModify("org.mytest.myclass")).isTrue();
    }
    
    @Test
    public void classes_from_packages_to_modify_should_modify() throws Exception {
    
        configuration.addClassesToModify("*mytest*");
    
        assertThat(configuration.shouldModify("org.mytest.myclass.SomeClass")).isTrue();
    }
    
    
}