package samples.powermockito.junit4.bugs.github840;

class FinalEquals2 {

    boolean standardEqualsCalled = false;
    boolean nonstandardEqualsCalled = false;
    boolean nonstandardEqualsValue = false;

    public String method6() {
        return "something";
    }

    @Override
    public final boolean equals(Object other) {
        standardEqualsCalled = true;
        return other instanceof FinalEquals2 &&
               other == this;
    }

    public boolean equals(FinalEquals2 other) {
        nonstandardEqualsCalled = true;
        return nonstandardEqualsValue;
    }

}
