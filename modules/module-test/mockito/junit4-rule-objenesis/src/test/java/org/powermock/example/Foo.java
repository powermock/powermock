package org.powermock.example;

public class Foo {

	public Bar m() {
		return new Bar(1);
	}

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
