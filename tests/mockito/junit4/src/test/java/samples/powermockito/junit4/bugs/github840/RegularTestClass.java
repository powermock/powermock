package samples.powermockito.junit4.bugs.github840;

class RegularTestClass {

    boolean standardEqualsCalled = false;
    boolean nonstandardEqualsCalled = false;

    @Override
    public boolean equals(Object other) {
        standardEqualsCalled = true;
        return other instanceof RegularTestClass &&
               equals((RegularTestClass)other);
    }

    public boolean equals(RegularTestClass other) {
        nonstandardEqualsCalled = true;
        return other == this;
    }

}
