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
package samples.interfacefieldchange;

/**
 * The purpose of the simple class is to demonstrate PowerMocks (possibly
 * future) ability to change static final fields in an interface. This is
 * normally not possible (the simple byte-code manipulation approach of removing
 * the final modifier doesn't work for interfaces since all static fields
 * <i>must</i> be final in interfaces according to the specification).
 */
public interface InterfaceWithStaticFinalField {
	static final String MY_STRING = "My value";
}
