/*
 * Copyright 2013 Jonas Berlin
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

import javassist.ClassPool;

import org.powermock.core.classloader.annotations.UseClassPathAdjuster;

/**
 * This interface can be used to adjust the classpath used by powermock to locate
 * class files. Use the @{@link UseClassPathAdjuster} to activate.
 */
public interface ClassPathAdjuster {
    void adjustClassPath(ClassPool classPool);
}
