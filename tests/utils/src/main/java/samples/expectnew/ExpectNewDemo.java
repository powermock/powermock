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

import samples.newmocking.MyClass;

public class ExpectNewDemo {

	public String getMessage() {
		MyClass myClass = new MyClass();
		return myClass.getMessage();
	}

	public String getMessageWithArgument() {
		MyClass myClass = new MyClass();
		return myClass.getMessage("test");
	}

	public void invokeVoidMethod() {
		MyClass myClass = new MyClass();
		myClass.voidMethod();
	}

	/**
	 * The purpose of the method is to demonstrate that a test case can mock the
	 * new instance call and throw an exception upon instantiation.
	 */
	public void throwExceptionWhenInvoction() {
		new MyClass();
	}

	public String multipleNew() {
		MyClass myClass1 = new MyClass();
		MyClass myClass2 = new MyClass();

		final String message1 = myClass1.getMessage();
		final String message2 = myClass2.getMessage();
		return message1 + message2;
	}

	public void simpleMultipleNew() {
		new MyClass();
		new MyClass();
		new MyClass();
	}

	public void simpleSingleNew() {
		new MyClass();
	}
}
