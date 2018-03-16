package samples.expectnew;

/**
 * Mocking object construction with {@code withAnyArguments()} does not treat all
 * the object's constructors equally. This test fixture provides an object which
 * has multiple constructors so that tests can cover both code-paths of constructor
 * matching.
 */
public class MultiConstructor {

    private final String first;

    public MultiConstructor(String first, String second) {
        this.first = first;
    }

    public MultiConstructor(String first) {
        this.first = first;

    }

    public MultiConstructor(String first, Runnable something, boolean b) {
        this.first = first;
    }

    public String getFirst() {
        return this.first;
    }
}
