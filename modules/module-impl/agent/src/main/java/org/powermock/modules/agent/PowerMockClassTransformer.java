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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;

class PowerMockClassTransformer implements ClassFileTransformer {

    private static final List<String> STARTS_WITH_IGNORED = new LinkedList<String>();
    private static final List<String> CONTAINS_IGNORED = new LinkedList<String>();

    static {
        STARTS_WITH_IGNORED.add("org/powermock");
        STARTS_WITH_IGNORED.add("org/junit");
        STARTS_WITH_IGNORED.add("org/mockito");
        STARTS_WITH_IGNORED.add("javassist");
        STARTS_WITH_IGNORED.add("org/objenesis");
        STARTS_WITH_IGNORED.add("junit");
        STARTS_WITH_IGNORED.add("org/hamcrest");
        STARTS_WITH_IGNORED.add("sun/");
        STARTS_WITH_IGNORED.add("$Proxy");

        CONTAINS_IGNORED.add("CGLIB$$");
        CONTAINS_IGNORED.add("$$PowerMock");
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (loader == null || shouldIgnore(className)) {
            return classfileBuffer;
        }
        try {
            final ClassReader reader = new ClassReader(classfileBuffer);
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            reader.accept(new PowerMockClassVisitor(writer), ClassReader.SKIP_FRAMES);
            return writer.toByteArray();
        } catch(Exception e) {
            throw new RuntimeException("Failed to redefine class "+className, e);
        }
    }

    private boolean shouldIgnore(String className) {
        for (String ignore : STARTS_WITH_IGNORED) {
            if(className.startsWith(ignore)) {
                return true;
            }
        }

        for (String ignore : CONTAINS_IGNORED) {
            if(className.contains(ignore)) {
                return true;
            }
        }
        return false;
    }
}
