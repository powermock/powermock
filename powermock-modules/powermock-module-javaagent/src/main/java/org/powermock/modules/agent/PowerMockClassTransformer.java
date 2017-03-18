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

package org.powermock.modules.agent;

import javassist.ClassPool;
import javassist.CtClass;
import org.powermock.core.agent.JavaAgentClassRegister;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.ClassWrapperFactory;
import org.powermock.core.transformers.TransformStrategy;
import org.powermock.core.transformers.impl.ClassMockTransformer;
import org.powermock.core.transformers.impl.InterfaceMockTransformer;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class PowerMockClassTransformer extends AbstractClassTransformer implements ClassFileTransformer {

	private volatile Set<String> classesToTransform;
    private volatile JavaAgentClassRegister javaAgentClassRegister;
    private final ClassWrapperFactory wrapperFactory;
    
    PowerMockClassTransformer() {
        super();
        wrapperFactory = new ClassWrapperFactory();
    }
    
    public void setClassesToTransform(Collection<String> classesToTransform) {
    	this.classesToTransform = new HashSet<String>(classesToTransform);
    }

    public void setJavaAgentClassRegister(JavaAgentClassRegister javaAgentClassRegister) {
        this.javaAgentClassRegister = javaAgentClassRegister;
    }

    private static final ClassMockTransformer CLASS_MOCK_TRANSFORMER = new ClassMockTransformer(TransformStrategy.INST_REDEFINE);
    private static final InterfaceMockTransformer INTERFACE_MOCK_TRANSFORMER = new InterfaceMockTransformer(TransformStrategy.INST_REDEFINE);

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (loader == null || shouldIgnore(className)) {
            return null;
        }
        try {
            String normalizedClassName = className.replace("/", ".");
            if (classesToTransform != null && classesToTransform.contains(normalizedClassName)) {
                ByteArrayInputStream is = new ByteArrayInputStream(classfileBuffer);
                CtClass ctClass = null;
                try {
                    ctClass = ClassPool.getDefault().makeClass(is);                               
                } finally {
                    is.close();
                }
    
                ctClass = transform(ctClass);

                /*
                 * ClassPool may cause huge memory consumption if the number of CtClass
                 * objects becomes amazingly large (this rarely happens since Javassist
                 * tries to reduce memory consumption in various ways). To avoid this
                 * problem, you can explicitly remove an unnecessary CtClass object from
                 * the ClassPool. If you call detach() on a CtClass object, then that
                 * CtClass object is removed from the ClassPool.
                 */
                ctClass.detach();

                javaAgentClassRegister.registerClass(loader, normalizedClassName);

                return ctClass.toBytecode();                      
            } 
            
            return null;           
        } catch(Exception e) {
            throw new RuntimeException("Failed to redefine class "+className, e);
        }
        

    }
    
    private CtClass transform(CtClass ctClass) throws Exception {
        ClassWrapper<CtClass> wrapped = wrapperFactory.wrap(ctClass);
        if (wrapped.isInterface()){
            wrapped = INTERFACE_MOCK_TRANSFORMER.transform(wrapped);
        }else{
            wrapped = CLASS_MOCK_TRANSFORMER.transform(wrapped);
        }
        return wrapped.unwrap();
    }
}
