package samples.powermockito.junit4.bugs.github716;

public class C {
	A a;
	B b;
	public C(A a, B b) {
		this.a = a;
		this.b = b;
	}
	public int multiply() { return 42; }
	public A getA() { return a; }
	public B getB() { return b; }
}
