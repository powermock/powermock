package samples.simplereturn;

public class SimpleReturnExampleUser {

	private SimpleReturnExample simpleReturnExample;

	public SimpleReturnExampleUser(SimpleReturnExample intReturn2) {
		super();
		this.simpleReturnExample = intReturn2;
	}

	public int myMethod() {
		return simpleReturnExample.mySimpleMethod();
	}
}
