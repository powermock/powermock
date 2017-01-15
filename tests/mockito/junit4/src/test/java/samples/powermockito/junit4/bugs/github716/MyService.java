package samples.powermockito.junit4.bugs.github716;

public class MyService {
	public int doSomething(A a, B b) {
		C c = new C(a, b);
		return c.multiply();
	}
}
