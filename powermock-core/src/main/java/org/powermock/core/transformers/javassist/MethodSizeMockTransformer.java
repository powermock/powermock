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
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import org.powermock.core.transformers.TransformStrategy;

/**
 * According to JVM specification method size must be lower than 65536 bytes.
 * When that limit is exceeded class loader will fail to load the class.
 * Since instrumentation can increase method size significantly it must be
 * ensured that JVM limit is not exceeded.
 * <p/>
 * When the limit is exceeded method's body is replaced by exception throw.
 * Method is then instrumented again to allow mocking and suppression.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3">JVM specification</a>
 */
public class MethodSizeMockTransformer extends MethodMockTransformer {
    
    private static final int MAX_METHOD_CODE_LENGTH_LIMIT = 65536;
    
    public MethodSizeMockTransformer(final TransformStrategy strategy) {
        super(strategy);
    }
    
    public CtClass transform(final CtClass clazz) throws CannotCompileException, NotFoundException {
        for (CtMethod method : clazz.getDeclaredMethods()) {
            if (isMethodSizeExceeded(method)) {
                String code = "{throw new IllegalAccessException(\"" +
                                  "Method was too large and after instrumentation exceeded JVM limit. " +
                                  "PowerMock modified the method to allow JVM to load the class. " +
                                  "You can use PowerMock API to suppress or mock this method behaviour." +
                                  "\");}";
                method.setBody(code);
                modifyMethod(method);
            }
        }
        return clazz;
    }
    
    private boolean isMethodSizeExceeded(CtMethod method) {
        CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
        return codeAttribute != null && codeAttribute.getCodeLength() >= MAX_METHOD_CODE_LENGTH_LIMIT;
    }
}
