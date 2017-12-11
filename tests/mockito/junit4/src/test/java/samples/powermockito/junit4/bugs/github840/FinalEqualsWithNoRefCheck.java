package samples.powermockito.junit4.bugs.github840;

class FinalEqualsWithNoRefCheck {

    boolean equalsCalled = false;
    boolean actualDetermination = false;

    public double aMethod() {
        return 19d;
    }

    @Override
    public final boolean equals(Object other) {
        equalsCalled = true;
        return actualDetermination;
    }

}
