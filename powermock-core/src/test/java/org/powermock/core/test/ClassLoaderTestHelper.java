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

package org.powermock.core.test;

import javassist.ClassPool;
import javassist.Loader;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformerChain;

import static org.junit.Assert.assertNotNull;

public class ClassLoaderTestHelper {
    
    public static Class<?> loadWithMockClassLoader(final String className, final MockTransformerChain mockTransformerChain,
                                                   final MockClassLoaderFactory mockClassloaderFactory) throws Exception {
        
        MockClassLoader loader = mockClassloaderFactory.getInstance(new String[]{MockClassLoader.MODIFY_ALL_CLASSES});
        loader.setMockTransformerChain(mockTransformerChain);
        
        Class<?> clazz = Class.forName(className, true, loader);
        
        assertNotNull("Class has been loaded", clazz);
        
        return clazz;
    }
    
    public static void runTestWithNewClassLoader(ClassPool classPool, String name) throws Throwable {
        Loader loader = new Loader(classPool);
        loader.run(name, new String[0]);
    }
    
    
}
