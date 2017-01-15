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

package org.powermock.tests.utils;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 */
public interface TestSuiteChunker {


    /**
     * Get the number of chunks defined in this suite.
     *
     * @return The number of chunks defined in the correct suite.
     */
    int getChunkSize();

    /**
     * Get all chunk entries.
     *
     * @return An set of entries that contains a list of methods contained in
     * the chunk and the class loader that loaded these methods.
     */
    List<TestChunk> getTestChunks();

    /**
     * Get all chunk entries for a specific class.
     *
     * @param testClass The class whose chunk entries to get.
     * @return An set of entries that contains a list of methods contained in
     * the chunk for the specific test class and the class loader that
     * loaded these methods.
     */
    List<TestChunk> getTestChunksEntries(Class<?> testClass);

    /**
     * Get TestChunk for the given method.
     *
     * @param method - method for which test chunk should be found.
     * @return TestChunk for this method.
     */
    TestChunk getTestChunk(Method method);

    /**
     * Should reflect whether or not this method is eligible for testing.
     *
     * @param testClass           The class that defines the method.
     * @param potentialTestMethod The method to inspect whether it should be executed in the
     *                            test suite or not.
     * @return {@code true} if the method is a test method and should be
     * executed, {@code false} otherwise.
     */
    boolean shouldExecuteTestForMethod(Class<?> testClass, Method potentialTestMethod);
}
