/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.transformers.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import javassist.CtPrimitiveType;

import static javassist.CtClass.*;

/**
 * Simple utility that maps constant fields of {@link javassist.CtClass} to
 * their corresponding java class-objects for primitive types.
 */
class Primitives {

    private static final Map<CtPrimitiveType,Class<?>> ct2primitiveClass =
            lookupMappings();

    static Class<?> getClassFor(CtPrimitiveType ctPrimitiveType) {
        return ct2primitiveClass.get(ctPrimitiveType);
    }

    private static Map<CtPrimitiveType, Class<?>> lookupMappings() {
        Map<CtPrimitiveType,Class<?>> mappings = new IdentityHashMap<CtPrimitiveType, Class<?>>(10);
        for (Object[] each : new Object[][] {
            {booleanType, boolean.class},
            {byteType, byte.class},
            {charType, char.class},
            {doubleType, double.class},
            {floatType, float.class},
            {intType, int.class},
            {longType, long.class},
            {shortType, short.class},
            {voidType, void.class}
        }) {
            mappings.put( (CtPrimitiveType)each[0], (Class<?>)each[1]);
        }
        return Collections.unmodifiableMap(mappings);
    }
}
