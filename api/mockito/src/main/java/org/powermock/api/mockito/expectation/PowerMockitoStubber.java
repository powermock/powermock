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
package org.powermock.api.mockito.expectation;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;

/**
 *Setup stubbing for void methods in final class, final void methods, or static
 * (final) methods. Note that for private void methods you should use the
 * standard <code>when</code> method in <code>PowerMockito</code>.
 */
public interface PowerMockitoStubber extends Stubber {
	/**
	 * Allows to choose a static void method when stubbing in
	 * doThrow()|doAnswer()|doNothing()|doReturn() style
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * doThrow(new RuntimeException()).when();
	 * StaticList.clear();
	 * 
	 * //following throws RuntimeException:
	 * StaticList.clear();
	 * </pre>
	 * 
	 * Read more about those methods:
	 * <p>
	 * {@link Mockito#doThrow(Throwable)}
	 * <p>
	 * {@link Mockito#doAnswer(Answer)}
	 * <p>
	 * {@link Mockito#doNothing()}
	 * <p>
	 * {@link Mockito#doReturn(Object)}
	 * <p>
	 * 
	 * See examples in javadoc for {@link Mockito}
	 */
	void when(Class<?> classMock);
}
