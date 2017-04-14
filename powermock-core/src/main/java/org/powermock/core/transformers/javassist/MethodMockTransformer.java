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

package org.powermock.core.transformers.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.powermock.core.MockGateway;
import org.powermock.core.transformers.TransformStrategy;

import static org.powermock.core.transformers.javassist.support.TransformerHelper.VOID;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.getCorrectReturnValueType;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.getReturnTypeAsString;
import static org.powermock.core.transformers.javassist.support.TransformerHelper.shouldSkipMethod;

abstract class MethodMockTransformer extends AbstractJavaAssistMockTransformer {
    
    MethodMockTransformer(final TransformStrategy strategy) {super(strategy);}
    
    void modifyMethod(final CtMethod method) throws NotFoundException, CannotCompileException {
        
        if (!shouldSkipMethod(method)) {
            
            // Lookup the method return type
            
            final CtClass returnTypeAsCtClass = method.getReturnType();
            final String returnTypeAsString = getReturnTypeAsString(method);
            
            if (Modifier.isNative(method.getModifiers())) {
                modifyNativeMethod(method, returnTypeAsCtClass, returnTypeAsString);
            } else {
                modifyMethod(method, returnTypeAsCtClass, returnTypeAsString);
            }
        }
    }
 
    
    private void modifyNativeMethod(CtMethod method, CtClass returnTypeAsCtClass,
                                    String returnTypeAsString) throws CannotCompileException {
        String methodName = method.getName();
        String returnValue = "($r)value";
        
        if (returnTypeAsCtClass.equals(CtClass.voidType)) {
            returnValue = VOID;
        }
        
        String classOrInstance = classOrInstance(method);
        method.setModifiers(method.getModifiers() - Modifier.NATIVE);
        String code = "Object value = "
                          + MockGateway.class.getName()
                          + ".methodCall("
                          + classOrInstance
                          + ", \""
                          + method.getName()
                          + "\", $args, $sig, \""
                          + returnTypeAsString
                          + "\");"
                          + "if (value != "
                          + MockGateway.class.getName() + ".PROCEED) "
                          + "return "
                          + returnValue + "; "
                          + "throw new java.lang.UnsupportedOperationException(\"" + methodName + " is native\");";
        method.setBody("{" + code + "}");
    }
    
    private String classOrInstance(CtMethod method) {
        String classOrInstance = "this";
        if (Modifier.isStatic(method.getModifiers())) {
            classOrInstance = "$class";
        }
        return classOrInstance;
    }
    
    private void modifyMethod(CtMethod method, CtClass returnTypeAsCtClass,
                              String returnTypeAsString) throws CannotCompileException {
        final String returnValue = getCorrectReturnValueType(returnTypeAsCtClass);
        
        String classOrInstance = classOrInstance(method);
        
        String code = "Object value = "
                          + MockGateway.class.getName()
                          + ".methodCall("
                          + classOrInstance + ", \""
                          + method.getName()
                          + "\", $args, $sig, \""
                          + returnTypeAsString
                          + "\");"
                          + "if (value != " + MockGateway.class.getName() + ".PROCEED) " + "return "
                          + returnValue + "; ";
        
        method.insertBefore("{ " + code + "}");
    }
  
}
