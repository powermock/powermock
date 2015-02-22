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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestPrimitives {

    final CtPrimitiveType ctType;

    public TestPrimitives(CtPrimitiveType ctType) {
        this.ctType = ctType;
    }

    @Parameterized.Parameters
    public static List<?> values() throws Exception {
        List<Object[]> valuesList = new ArrayList<Object[]>();
        for (Field f : CtClass.class.getFields()) {
            if (CtClass.class.isAssignableFrom(f.getType())) {
                valuesList.add(new Object[] {f.get(null)});
            }
        }
        return valuesList;
    }

    @Test
    public void testMapping() {
        Class<?> mapping = Primitives.getClassFor(ctType);
        assertEquals("Mapping for ctType=" + ctType.getName(),
                ctType.getSimpleName(), mapping.getSimpleName());
    }
}
