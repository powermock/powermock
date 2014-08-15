/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.powermock.modules.agent;

import java.util.Arrays;

public class PowerMockClassRedefiner {

    public static void redefine(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class to redefine cannot be null");
        }

        PowerMockAgent.getClasstransformer().setClassesToTransform(Arrays.asList(cls.getName()));

        try {
            PowerMockAgent.instrumentation().retransformClasses(cls);
        } catch (Exception e) {
            throw new RuntimeException("Failed to redefine class " + cls.getName(), e);
        }
    }

    public static void redefine(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Class name to redefine cannot be null");
        }
        try {
            redefine(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void redefine(String[] classes, String[] packagesToIgnore) {
        PowerMockClassTransformer transformer = PowerMockAgent.getClasstransformer();
        transformer.setClassesToTransform(Arrays.asList(classes));
        transformer.setPackagesToIgnore(Arrays.asList(packagesToIgnore));

        try {
            for (int i = classes.length - 1; i >= 0; i--) {
                String className = classes[i];
                Class<?> clazz;
                try {
                    clazz = Class.forName(className);

                    PowerMockAgent.instrumentation().retransformClasses(clazz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            transformer.resetPackagesToIgnore();
        }
    }
}
