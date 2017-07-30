/*
 * Copyright 2017 the original Nicole Behlen.
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
package org.powermock.core.classloader;


import javassist.ClassClassPath;
import javassist.ClassPool;

/**
 * This abstract class can be used to provide a different {@link ClassLoader} than
 * the generic one MockClassLoader.class.getClassLoader(), e.g.
 * for OSGI-bundles. <br>Furthermore it will adjusts the classpath, implementing also the ClassPathAdjuster.
 * <p>
 * <b>Example:</b>
 * <p>
 * Define the simple class in the OSGI-bundle to use the PowerMockRunner like this:<p>
 * <code>public class MapClassLoader extends AbstractExternalClassLoader {<br>} </code>
 * <p>
 * Then add the following two annotations to your class using the
 * <br> <code>{@literal @}RunWith(PowerMockRunner.class)</code> annotation.<p>
 * <code>{@literal @}ClassLoaderProvider(value=MapClassLoader.class)</code><br>
 * <code>{@literal @}UseClassPathAdjuster(value=MapClassLoader.class)</code>.
 * @since 1.7.1
 */
public abstract class AbstractExternalClassLoader implements ClassLoaderOverride, ClassPathAdjuster {

    @Override
    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    public void adjustClassPath(ClassPool classPool) {
        classPool.appendClassPath(new ClassClassPath(this.getClass()));
    }
}