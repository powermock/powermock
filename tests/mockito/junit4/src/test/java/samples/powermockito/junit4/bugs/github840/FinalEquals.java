package samples.powermockito.junit4.bugs.github840;

class FinalEquals {

    boolean standardEqualsCalled = false;
    boolean nonstandardEqualsCalled = false;

    public int method0() {
        return 17;
    }

    @Override
    public final boolean equals(Object other) {
        standardEqualsCalled = true;
        return other instanceof FinalEquals &&
               other == this;
    }

    public final boolean equals(FinalEquals other) {
        nonstandardEqualsCalled = true;
        return other == this;
    }

}
