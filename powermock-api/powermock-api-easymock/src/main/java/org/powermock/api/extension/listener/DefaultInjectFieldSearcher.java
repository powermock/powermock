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

import org.powermock.api.extension.InjectFieldSearcher;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@SuppressWarnings("unchecked")
class DefaultInjectFieldSearcher implements InjectFieldSearcher {

    @Override
    public Field findField(Object instance, MockMetadata mockMetadata) {
        Set<Field> candidates = Whitebox.getFieldsAnnotatedWith(instance, mockMetadata.getAnnotation());
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        candidates = filterByQualifier(candidates, mockMetadata.getQualifier());
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        candidates = filterByType(candidates, mockMetadata.getType());
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        candidates = filterByFieldName(candidates, mockMetadata.getFieldName());
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        return null;
    }
    
    private Set<Field> filterByFieldName(final Set<Field> candidates, final String fieldName) {
        if (fieldName == null || fieldName.length() == 0) {
            return candidates;
        }
        return doFilterByQualifier(candidates, fieldName);
    }
    
    private Set<Field> filterByType(Set<Field> candidates, Class<?> type) {
        if (type == null) {
            return candidates;
        }
        return doFilterByType(candidates, type);
    }

    private Set<Field> doFilterByType(Set<Field> candidates, Class<?> type) {
        Set<Field> fields = new HashSet<Field>();
        for (Field candidate : candidates) {
            if (candidate.getType().isAssignableFrom(type)) {
                fields.add(candidate);
            }
        }
        return fields;
    }

    private Set<Field> filterByQualifier(Set<Field> candidates, String qualifier) {
        if (qualifier == null || qualifier.length() == 0) {
            return candidates;
        }
        return doFilterByQualifier(candidates, qualifier);
    }

    private Set<Field> doFilterByQualifier(Set<Field> candidates, String qualifier) {
        Set<Field> fields = new HashSet<Field>();
        for (Field candidate : candidates) {
            if (candidate.getName().equals(qualifier)) {
                fields.add(candidate);
            }
        }
        return fields;
    }
}
