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

package org.powermock.modules.agent;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.impl.MainMockTransformer;

class PowerMockClassTransformer extends AbstractClassTransformer implements ClassFileTransformer {

	private volatile Set<String> classesToTransform;
    
    public void setClassesToTransform(Collection<String> classesToTransform) {
    	this.classesToTransform = new HashSet<String>(classesToTransform);
    }
    
    private static final MainMockTransformer mainMockTransformer = new MainMockTransformer(TransformStrategy.INST_REDEFINE);

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (loader == null || shouldIgnore(className)) {
            return null;
        }
        try {            
            if (classesToTransform != null && classesToTransform.contains(className.replace("/", "."))) {            
                ByteArrayInputStream is = new ByteArrayInputStream(classfileBuffer);
                CtClass ctClass = null;
                try {
                    ctClass = ClassPool.getDefault().makeClass(is);                               
                } finally {
                    is.close();
                }
                
                ctClass = mainMockTransformer.transform(ctClass);

                /*
                 * ClassPool may cause huge memory consumption if the number of CtClass
                 * objects becomes amazingly large (this rarely happens since Javassist
                 * tries to reduce memory consumption in various ways). To avoid this
                 * problem, you can explicitly remove an unnecessary CtClass object from
                 * the ClassPool. If you call detach() on a CtClass object, then that
                 * CtClass object is removed from the ClassPool.
                 */
                ctClass.detach();

                return ctClass.toBytecode();                      
            } 
            
            return null;           
        } catch(Exception e) {
            throw new RuntimeException("Failed to redefine class "+className, e);
        }
        

    }
}
