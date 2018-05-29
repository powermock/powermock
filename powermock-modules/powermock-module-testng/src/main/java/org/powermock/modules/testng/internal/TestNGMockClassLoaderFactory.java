/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.modules.testng.internal;

import org.powermock.core.classloader.MockClassLoaderFactory;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.powermock.tests.utils.impl.ArrayMergerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;

class TestNGMockClassLoaderFactory {
    
    private final IgnorePackagesExtractor ignorePackagesExtractor;
    private final IgnorePackagesExtractor expectedExceptionsExtractor;
    private final ArrayMerger arrayMerger;
    
    TestNGMockClassLoaderFactory() {
        ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();
        expectedExceptionsExtractor = new PowerMockExpectedExceptionsExtractorImpl();
        arrayMerger = new ArrayMergerImpl();
    }
    
    
    ClassLoader createClassLoader(Class<?> testClass) {
        final String[] packagesToIgnore = arrayMerger.mergeArrays(
            String.class,
            ignorePackagesExtractor.getPackagesToIgnore(testClass),
            expectedExceptionsExtractor.getPackagesToIgnore(testClass)
        );
        return new MockClassLoaderFactory(testClass, packagesToIgnore).createForClass(null);
    }
    
}
