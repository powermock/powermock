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

package org.powermock.api.mockito.expectation.reporter;

import org.powermock.api.mockito.ClassNotPreparedException;
import org.powermock.core.reporter.PowerMockReporter;

import static org.powermock.utils.StringJoiner.join;

public class MockitoPowerMockReporter implements PowerMockReporter {

    @Override
    public <T> void classNotPrepared(Class<T> type) {
        throw new ClassNotPreparedException(join(String.format("The class %s not prepared for test.", type.getName()),
                "To prepare this class, add class to the '@PrepareForTest' annotation.",
                "In case if you don't use this annotation, add the annotation on class or  method level. "));
    }

}
