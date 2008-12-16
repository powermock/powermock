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
package org.powermock.reflect.internal.matcherstrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.powermock.reflect.internal.WhiteboxImpl;

public class FieldAnnotationMatcherStrategy extends FieldMatcherStrategy {

	final Class<? extends Annotation>[] annotations;

	public FieldAnnotationMatcherStrategy(Class<? extends Annotation>[] annotations) {
		if (annotations == null || annotations.length == 0) {
			throw new IllegalArgumentException("You must specify atleast one annotation.");
		}
		this.annotations = annotations;
	}

	@Override
	public boolean matches(Field field) {
		for (Class<? extends Annotation> annotation : annotations) {
			if (field.isAnnotationPresent(annotation)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void notFound(Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException("No field that has any of the annotation types \"" + getAnnotationNames()
				+ "\" could be found in the class hierarchy of " + WhiteboxImpl.getType(object).getName() + ".");
	}

	@Override
	public String toString() {
		return "annotations " + getAnnotationNames();
	}

	private String getAnnotationNames() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < annotations.length; i++) {
			builder.append(annotations[i].getName());
			if (i != annotations.length - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}