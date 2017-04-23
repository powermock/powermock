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

package org.powermock.core.transformers.javassist.support;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.FieldInfo;

public class TransformerHelper {
    
    public static final String VOID = "";
    
    private static boolean isAccessFlagSynthetic(CtMethod method) {
        int accessFlags = method.getMethodInfo2().getAccessFlags();
        return ((accessFlags & AccessFlag.SYNTHETIC) != 0) && !isBridgeMethod(method);
    }
    
    private static boolean isBridgeMethod(CtMethod method) {
        return (method.getMethodInfo2()
                      .getAccessFlags() & AccessFlag.BRIDGE) != 0;
    }
    
    /**
     * @return The correct return type, i.e. takes care of casting the a wrapper
     * type to primitive type if needed.
     */
    public static String getCorrectReturnValueType(final CtClass returnTypeAsCtClass) {
        final String returnTypeAsString = returnTypeAsCtClass.getName();
        final String returnValue;
        if (returnTypeAsCtClass.equals(CtClass.voidType)) {
            returnValue = VOID;
        } else if (returnTypeAsCtClass.isPrimitive()) {
            if (returnTypeAsString.equals("char")) {
                returnValue = "((java.lang.Character)value).charValue()";
            } else if (returnTypeAsString.equals("boolean")) {
                returnValue = "((java.lang.Boolean)value).booleanValue()";
            } else {
                returnValue = "((java.lang.Number)value)." + returnTypeAsString + "Value()";
            }
        } else {
            returnValue = "(" + returnTypeAsString + ")value";
        }
        return returnValue;
    }
    
    public static boolean isNotSyntheticField(FieldInfo fieldInfo) {
        return (fieldInfo.getAccessFlags() & AccessFlag.SYNTHETIC) == 0;
    }
    
    public static boolean shouldSkipMethod(CtMethod method) {
        return isAccessFlagSynthetic(method) || Modifier.isAbstract(method.getModifiers());
    }
    
    public static String getReturnTypeAsString(final CtMethod method) throws NotFoundException {
        CtClass returnType = method.getReturnType();
        String returnTypeAsString = VOID;
        if (!returnType.equals(CtClass.voidType)) {
            returnTypeAsString = returnType.getName();
        }
        return returnTypeAsString;
    }
    
    public static boolean shouldTreatAsSystemClassCall(CtClass declaringClass) {
        final String className = declaringClass.getName();
        return className.startsWith("java.");
    }
}
