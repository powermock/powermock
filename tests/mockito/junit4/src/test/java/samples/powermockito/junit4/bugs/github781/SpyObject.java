package samples.powermockito.junit4.bugs.github781;

public class SpyObject {
    public boolean callEquals() {
        return EqualsStatic.equals();
    }
}
