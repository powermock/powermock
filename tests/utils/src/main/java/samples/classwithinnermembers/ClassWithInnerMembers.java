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
