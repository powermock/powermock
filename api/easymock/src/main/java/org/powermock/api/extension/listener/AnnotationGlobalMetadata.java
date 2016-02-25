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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
class AnnotationGlobalMetadata {

    private final List<AnnotationMockMetadata> qualifiedInjections = new ArrayList<AnnotationMockMetadata>(5);

    private final List<AnnotationMockMetadata> unqualifiedInjections = new ArrayList<AnnotationMockMetadata>(5);

    private final Set<String> qualifiers = new HashSet<String>();

    public List<AnnotationMockMetadata> getQualifiedInjections() {
        return qualifiedInjections;
    }

    public List<AnnotationMockMetadata> getUnqualifiedInjections() {
        return unqualifiedInjections;
    }

    public void add(List<AnnotationMockMetadata> mocksMetadata) {
        for (AnnotationMockMetadata mockMetadata : mocksMetadata) {
            add(mockMetadata);
        }
    }

    private void add(AnnotationMockMetadata mockMetadata) {

        String qualifier = mockMetadata.getQualifier();

        if (qualifier.length() != 0) {
            blockDuplicateQualifiers(qualifier);
            qualifiedInjections.add(mockMetadata);
        } else {
            unqualifiedInjections.add(mockMetadata);
        }
    }

    private void blockDuplicateQualifiers(String qualifier) {
        if (!qualifiers.add(qualifier)) {
            throw new RuntimeException(String.format("At least two mocks have fieldName qualifier '%s'", qualifier));
        }
    }


}
