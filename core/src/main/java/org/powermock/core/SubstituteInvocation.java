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
package org.powermock.core;

/**
 * This interface is used to let the mock framework handle and store state as
 * usual even when dealing with new instance calls. A proxy should be
 * dynamically created by the mock framework and for each new call in the code
 * the {@link #invokeSubstituteMethod()} method should be invoked instead. This
 * way the mock framework can be used to record and verify new calls in the same
 * way that it does when recording and verifying normal methods calls.
 * 
 * @author Johan Haleby
 */
public interface SubstituteInvocation<T> {

	public T invokeSubstituteMethod() throws Throwable;

}
