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

package org.powermock.core.spi;

import org.powermock.core.spi.support.InvocationSubstitute;

/**
 * A new invocation control pairs up a {@link InvocationSubstitute} with the
 * mock object created when invoking
 * {@link InvocationSubstitute#performSubstitutionLogic(Object...)} object.
 * 
 */
public interface NewInvocationControl<T> extends DefaultBehavior {

    /**
     * Invoke the invocation control
     */
    Object invoke(Class<?> type, Object[] args, Class<?>[] sig) throws Exception;

    /**
     * Expect a call to the new instance substitution logic.
     */
    T expectSubstitutionLogic(Object... arguments) throws Exception;
}
