package samples.powermockito.junit4.bugs.github583;

/**
 *
 */
public class ChildClass extends ParenClass {
    private int b;
    public ChildClass() {
        super();
    }
    public int getB() {
        return b;
    }
    public void setB(int b) {
        this.b = b;
    }
    public void test(){

    }
}