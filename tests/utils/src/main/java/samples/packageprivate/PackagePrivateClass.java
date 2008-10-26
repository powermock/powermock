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
package samples.packageprivate;

/**
 * This class demonstrates the ability for PowerMock to mock package private
 * classes. This is normally not an issue but since we've changed the CgLib
 * naming policy to allow for signed mocking PowerMock needs to byte-code
 * manipulate this class.
 */
class PackagePrivateClass {

	public int getValue() {
		return returnAValue();
	}

	private int returnAValue() {
		return 82;
	}
}
