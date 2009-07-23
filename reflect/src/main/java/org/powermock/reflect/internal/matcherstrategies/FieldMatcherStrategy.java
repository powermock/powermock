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
package org.powermock.reflect.internal.matcherstrategies;

import java.lang.reflect.Field;

import org.powermock.reflect.exceptions.FieldNotFoundException;

/**
 * Class that should be implemented by field matching strategies.
 */
public abstract class FieldMatcherStrategy {

    /**
     * A field matcher that checks if a field matches a given criteria.
     * 
     * @param field
     *            The field to check whether it matches the strategy or not.
     * @return <code>true</code> if this field matches the strategy,
     *         <code>false</code> otherwise.
     * 
     */
    public abstract boolean matches(Field field);

    /**
     * Throws an {@link FieldNotFoundException} if the strategy criteria could
     * not be found.
     * 
     * @param type
     *            The type of the object that was not found.
     * @param isInstanceField
     *            <code>true</code> if the field that was looked after was an
     *            instance field or <code>false</code> if it was a static field.
     */
    public abstract void notFound(Class<?> type, boolean isInstanceField) throws FieldNotFoundException;
}