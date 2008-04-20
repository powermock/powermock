/*
 * Copyright 2008 the original author or authors.
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
package org.powermock.core.transformers;

import javassist.CtClass;

/**
 * Interface that all mock transformers must implement. The purpose of a mock
 * transformer is to create a modified version of a <code>Class</code> so that
 * it is mock enabled.
 * 
 * @author Johan Haleby
 */
public interface MockTransformer {

	/**
	 * Transforms the <code>Class</code> with name
	 * <code>fullyQualifiedName</code>.
	 * 
	 * @param fullyQualifiedName
	 *            The fully qualified name of the <code>Class</code> to
	 *            transform into a mock enabled class.
	 * @return A <code>CtClass</code> representation of the mocked class.
	 */
	public CtClass transform(CtClass clazz) throws Exception;
}
