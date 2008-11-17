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
package samples.innerclassmocking;

/**
 * Used for demonstration of the ability to mock methods from a private inner
 * class. Due to limitations in Javassist (which doesn't seem to load inner
 * classes correctly??) we cannot mock private methods in inner classes. It
 * doesn't seem to have any effect when modifing the method modifier and setting
 * the method to public when loading the class by the mock class loader (but
 * why? Could be because the outer class has already been loaded?!).
 * 
 */
public class ClassWithPrivateInnerClass {
	public String getMessage() {
		return new InnerClass().getInnerMessage();
	}

	private class InnerClass {
		public String getInnerMessage() {
			return "A message from an inner class!";
		}
	}
}
