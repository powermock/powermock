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

package org.powermock.core.classloader.annotations;

import java.lang.annotation.*;

/**
 * Use this annotation to suppress static initializers (constructors) for one or
 * more classes.
 * <p>
 * The reason why an annotation is needed for this is because we need to know at
 * <strong>load-time</strong> if the static constructor execution for this
 * class should be skipped or not. Unfortunately we cannot pass the class as the
 * value parameter to the annotation (and thus get type-safe values) because
 * then the class would be loaded before PowerMock could have suppressed its
 * constructor.
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SuppressStaticInitializationFor {
	String[] value() default "";
}
