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
package org.powermock.api.mockito.verification;

import org.mockito.Mockito;

public interface PrivateMethodVerification {

    /**
     * Expect calls to private methods without having to specify the method
     * name. The method will be looked up using the parameter types (if
     * possible).
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(Object... arguments) throws Exception;

    /**
     * Expect a private or inner class method call in cases where PowerMock
     * cannot automatically determine the type of the parameters, for example
     * when mixing primitive types and wrapper types in the same method. For
     * most situations use {@link #invoke(Object, Object...)} instead.
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(String methodToExecute, Class<?>[] argumentTypes, Object... arguments) throws Exception;

    /**
     * Expected a private or inner class method call in a subclass (defined by
     * <code>definedIn</code>) in cases where PowerMock cannot automatically
     * determine the type of the parameters, for example when mixing primitive
     * types and wrapper types in the same method. For most situations use
     * {@link #invokeinvoke(Object, Object...)} instead.
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(String methodToExecute, Class<?> definedIn, Class<?>[] argumentTypes, Object... arguments) throws Exception;

    /**
     * Expect a private or inner class method call that is located in a subclass
     * of the instance.
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(Class<?> declaringClass, String methodToExecute, Object... arguments) throws Exception;

    /**
     * Expect a private or inner class method call in that is located in a
     * subclass of the instance. This might be useful to test private methods.
     * <p>
     * Use this for overloaded methods.
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(Class<?> declaringClass, String methodToExecute, Class<?>[] parameterTypes, Object... arguments) throws Exception;

    /**
     * Expect a static private or inner class method call.
     * 
     * @see {@link Mockito#invoke(Object)}
     * @throws Exception
     *             If something unexpected goes wrong.
     */
    public void invoke(String methodToExecute, Object... arguments) throws Exception;
}
