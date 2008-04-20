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
package samples.privatemocking;

/**
 * A class used to test the functionality to mock private methods.
 * 
 * @author Johan Haleby
 */
public class PrivateMethodDemo {
	public String say(String name) {
		return sayIt(name);
	}

	private String sayIt(String name) {
		return "Hello " + name;
	}

	@SuppressWarnings("unused")
	private String sayIt() {
		return "Hello world";
	}

	public int methodCallingPrimitiveTestMethod() {
		return aTestMethod(10);
	}

	public int methodCallingWrappedTestMethod() {
		return aTestMethod(new Integer(15));
	}

	private int aTestMethod(int aValue) {
		return aValue;
	}

	private Integer aTestMethod(Integer aValue) {
		return aValue;
	}

}
