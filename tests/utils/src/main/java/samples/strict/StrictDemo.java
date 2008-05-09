package samples.strict;

public class StrictDemo {

	public void callAThenB() {
		A();
		B();
	}

	private void A() {
		// Does nothing
	}

	private void B() {
		// Does nothing
	}
}
