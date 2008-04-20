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
package org.powermock.core.invocationcontrol.newinstance;


/**
 * The purpose of an invocation control is to determine whether a certain method
 * is mocked or not. This is determined by pairing up an InvocationHandler (that
 * is associated with an entire object) and the Methods for this object that
 * should be mocked.
 * 
 * @author Johan Haleby
 */
public interface NewInvocationControl<T> {

	public T createInstance();
}
