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
package samples.classwithinnermembers;

/**
 * Class that is used to test that local and member class works with PowerMock.
 */
public class ClassWithInnerMembers {

	private interface InnerInterface {
		String doStuff();
	}

	private static class MyInnerClass implements InnerInterface {

		public String doStuff() {
			return "member class";
		}
	}

	private static class StaticInnerClassWithConstructorArgument implements InnerInterface {

		private final String value;

		public StaticInnerClassWithConstructorArgument(String value) {
			this.value = value;
		}

		public String doStuff() {
			return value;
		}
	}

	private class MyInnerClassWithConstructorArgument implements InnerInterface {

		private final String value;

		public MyInnerClassWithConstructorArgument(String value) {
			this.value = value;
		}

		public String doStuff() {
			return value;
		}
	}

	public String getValue() {
		return new MyInnerClass().doStuff();
	}

	public String getValueForInnerClassWithConstructorArgument() {
		return new MyInnerClassWithConstructorArgument("value").doStuff();
	}

	public String getValueForStaticInnerClassWithConstructorArgument() {
		return new StaticInnerClassWithConstructorArgument("value").doStuff();
	}

	public String getLocalClassValue() {
		class MyLocalClass implements InnerInterface {
			public String doStuff() {
				return "local class";
			}
		}

		return new MyLocalClass().doStuff();
	}

	public String getLocalClassValueWithArgument() {
		class MyLocalClass implements InnerInterface {

			private final String value;

			public MyLocalClass(String value) {
				this.value = value;
			}

			public String doStuff() {
				return value;
			}
		}

		return new MyLocalClass("my value").doStuff();
	}

	public String getValueForAnonymousInnerClass() {

		InnerInterface inner = new InnerInterface() {
			public String doStuff() {
				return "value";
			}
		};

		return inner.doStuff();
	}
}
