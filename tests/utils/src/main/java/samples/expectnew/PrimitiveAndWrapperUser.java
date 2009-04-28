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
package samples.expectnew;

/**
 * Used to demonstrate PowerMocks ability to deal with overloaded constructors
 * of primitive/wrapper types.
 */
public class PrimitiveAndWrapperUser {

	public int useThem() {
		PrimitiveAndWrapperDemo demo1 = new PrimitiveAndWrapperDemo(Integer.valueOf(42));
		PrimitiveAndWrapperDemo demo2 = new PrimitiveAndWrapperDemo(21);
		return demo1.getMyInt() + demo2.getMyInt();
	}
}