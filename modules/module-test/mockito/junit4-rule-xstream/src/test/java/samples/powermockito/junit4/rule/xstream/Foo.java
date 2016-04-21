package samples.powermockito.junit4.rule.xstream;

public class Foo {

	public Bar m() {
		return new Bar(1);
	}

	@SuppressWarnings("SameParameterValue")
	public static class Bar {

		private final int i;

		Bar(final int i) {
			this.i = i;
		}

		public int getI() {
			return i;
		}
	}

}
