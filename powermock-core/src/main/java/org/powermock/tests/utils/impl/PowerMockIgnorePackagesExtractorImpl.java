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

import org.powermock.configuration.GlobalConfiguration;
import org.powermock.configuration.PowerMockConfiguration;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.tests.utils.IgnorePackagesExtractor;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PowerMockIgnorePackagesExtractorImpl implements IgnorePackagesExtractor {
    
    @Override
    public String[] getPackagesToIgnore(AnnotatedElement element) {
    
        PowerMockIgnore annotation = element.getAnnotation(PowerMockIgnore.class);
        boolean useGlobal = true;
        
        if (annotation != null){
            useGlobal = annotation.globalIgnore();
        }
        
        Set<String> ignoredPackages = new HashSet<String>();
        useGlobal &= extractPackageToIgnore(element, ignoredPackages);
        
        final String[] packageToIgnore = ignoredPackages.toArray(new String[ignoredPackages.size()]);
        if (useGlobal) {
            return getPackageToIgnoreWithGlobal(packageToIgnore);
        } else {
            return packageToIgnore;
        }
    }
    
    private String[] getPackageToIgnoreWithGlobal(final String[] packageToIgnore) {
        String[] globalIgnore = getGlobalIgnore();
        
        final String[] allPackageToIgnore;
        if (globalIgnore != null) {
            allPackageToIgnore = addGlobalIgnore(packageToIgnore, globalIgnore);
        } else {
            allPackageToIgnore = packageToIgnore;
        }
        
        return allPackageToIgnore;
    }
    
    private String[] getGlobalIgnore() {
        final PowerMockConfiguration powerMockConfiguration = GlobalConfiguration.powerMockConfiguration();
        return powerMockConfiguration.getGlobalIgnore();
    }
    
    private boolean extractPackageToIgnore(final AnnotatedElement element, final Set<String> ignoredPackages) {
        boolean useGlobalFromAnnotation = addValueFromAnnotation(element, ignoredPackages);
        boolean useGlobalFromSuperclass = addValuesFromSuperclass((Class<?>) element, ignoredPackages);
        
        return useGlobalFromAnnotation & useGlobalFromSuperclass;
    }
    
    private boolean  addValuesFromSuperclass(final Class<?> element, final Set<String> ignoredPackages) {
        final Collection<Class<?>> superclasses = new ArrayList<Class<?>>();
        Collections.addAll(superclasses, element.getSuperclass());
        Collections.addAll(superclasses, element.getInterfaces());
        
        boolean useGlobalIgnore = true;
        
        for (Class<?> superclass : superclasses) {
            if (superclass != null && !superclass.equals(Object.class)) {
                useGlobalIgnore &= extractPackageToIgnore(superclass, ignoredPackages);
            }
        }
        
        return useGlobalIgnore;
    }
    
    private boolean addValueFromAnnotation(final AnnotatedElement element, final Set<String> ignoredPackages) {
        PowerMockIgnore annotation = element.getAnnotation(PowerMockIgnore.class);
        
        if (annotation != null) {
            String[] ignores = annotation.value();
            Collections.addAll(ignoredPackages, ignores);
            return annotation.globalIgnore();
        }
        
        return true;
    }
    
    private String[] addGlobalIgnore(final String[] packageToIgnore, final String[] globalIgnore) {
        final String[] allPackageToIgnore;
        
        allPackageToIgnore = new String[globalIgnore.length + packageToIgnore.length];
        
        System.arraycopy(globalIgnore, 0, allPackageToIgnore, 0, globalIgnore.length);
        System.arraycopy(packageToIgnore, 0, allPackageToIgnore, globalIgnore.length, packageToIgnore.length);
        
        return allPackageToIgnore;
    }
    
}
