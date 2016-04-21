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
package org.powermock.tests.utils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A test chunk consists of a list of methods that should be executed by a
 * particular classloader.
 * */
public interface TestChunk {

	ClassLoader getClassLoader();

	List<Method> getTestMethodsToBeExecutedByThisClassloader();

	boolean isMethodToBeExecutedByThisClassloader(Method method);
}