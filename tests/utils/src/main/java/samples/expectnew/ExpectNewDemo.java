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
		 System.out.println("myClass1 before = " + myClass1);
		MyClass myClass2 = new MyClass();
		
		 System.out.println("myClass1 = " + myClass1);
		 System.out.println("myClass2 = " + myClass2);
		 System.out.println("myClass1 = " + System.identityHashCode(myClass1));
		 System.out.println("myClass2 = " + System.identityHashCode(myClass2));
//		if (myClass2 == null) {
//			System.out.println("hello!!!!!");
//		} else {
//			System.out.println("before");
//			 String qwe = myClass2.toString();
//			System.out.println("myClass2 = " + qwe);
//				System.out.println("qweqwe");
//		}
		// TODO THIS DOESN'T WORK!?! WHY???
//		 final String message1 = myClass1.getMessage();
//		 final String message2 = myClass2.getMessage();
//		 return message1 + message2;
		// System.out.println(myClass1.getMessage());
		// System.out.println(myClass2.getMessage());
		return myClass1.getMessage() + myClass2.getMessage();
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
