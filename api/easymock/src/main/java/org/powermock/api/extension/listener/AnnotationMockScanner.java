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

package org.powermock.api.extension.listener;

import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class AnnotationMockScanner {
    private final Class<? extends Annotation> annotation;

    public AnnotationMockScanner(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public List<MockMetadata> scan(Object instance) throws Exception {
        final List<MockMetadata> mocksMetadata = new ArrayList<MockMetadata>();
        final Set<Field> fields = getFields(instance);
        for (Field field : fields) {
            if (field.get(instance) != null) {
                continue;
            }
            mocksMetadata.add(new AnnotationMockMetadata(annotation, field));
        }
        return mocksMetadata;
    }

    @SuppressWarnings("unchecked")
    private Set<Field> getFields(Object instance) {
        final Set<Field> fields;
        if (annotation != null) {
            fields = Whitebox.getFieldsAnnotatedWith(instance, annotation);
        }else{
            fields = Whitebox.getAllInstanceFields(instance);
        }
        return fields;
    }

}
