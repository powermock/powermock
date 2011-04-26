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

import javassist.ClassPool;
import javassist.CtClass;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.impl.MainMockTransformer;

import java.lang.instrument.ClassDefinition;

public class PowerMockClassRedefiner {

    private static final MainMockTransformer mainMockTransformer = new MainMockTransformer(TransformStrategy.INST_REDEFINE);


    public static void redefine(Class<?> cls) {
        if(cls == null) {
            throw new IllegalArgumentException("Class to redefine cannot be null");
        }
        try {
            CtClass ctClass = ClassPool.getDefault().get(cls.getName());
            ctClass = mainMockTransformer.transform(ctClass);
            final ClassDefinition classDefinition = new ClassDefinition(cls, ctClass.toBytecode());
            PowerMockAgent.instrumentation().redefineClasses(classDefinition);

        } catch(Exception e){
            throw new RuntimeException("Failed to redefine class "+cls.getName(), e);
        }
    }

    public static void redefine(String className) {
        if(className == null) {
            throw new IllegalArgumentException("Class name to redefine cannot be null");
        }
        try {
            redefine(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
