/*
 * Copyright 2009 the original author or authors.
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
package org.powermock.api.support.membermodification.strategy;

/**
 * Specifies the replace strategy for a class.
 */
public interface ClassReplaceStrategy {

	/**
	 * Replaces all method invocations on class specified class with method
	 * invocation to <code>cls</code>. Also replaces all constructor
	 * invocations. You can see this as duck typing.
	 * 
	 * @param cls
	 *            The class that will replace the other class.
	 */
	void with(Class<?> cls);
}
