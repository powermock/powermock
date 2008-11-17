package samples.expectnew;

/**
 * Used to demonstrate PowerMocks ability to deal with overloaded constructors
 * of primitive/wrapper types.
 */
public class PrimitiveAndWrapperUser {

	public int useThem() {
		PrimitiveAndWrapperDemo demo1 = new PrimitiveAndWrapperDemo(Integer.valueOf(42));
		PrimitiveAndWrapperDemo demo2 = new PrimitiveAndWrapperDemo(21);
		return demo1.getMyInt() + demo2.getMyInt();
	}
}