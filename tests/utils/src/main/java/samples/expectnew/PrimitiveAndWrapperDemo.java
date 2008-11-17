package samples.expectnew;

/**
 * Used to demonstrate PowerMocks ability to deal with overloaded constructors
 * of primitive/wrapper types.
 */
public class PrimitiveAndWrapperDemo {

	private final int myInt;

	public PrimitiveAndWrapperDemo(int myInt) {
		this.myInt = myInt;
	}

	public PrimitiveAndWrapperDemo(Integer myInt) {
		this.myInt = myInt;
	}

	public int getMyInt() {
		return myInt;
	}
}
