package samples.powermockito.junit4.bugs.github840;

class FinalEqualsWithOtherCall {

    boolean equalsCalled = false;
    boolean afterInternalCall = false;
    boolean nonstandardEqualsValue = false;

    public int method1() {
        return 1;
    }

    @Override
    public final boolean equals(Object other) {
        if (other instanceof FinalEqualsWithOtherCall) {
            System.out.println("FinalEqualsWithOtherCall compared to FinalEqualsWithNoRefCheck");
        }
        equalsCalled = true;
        otherMethod();
        afterInternalCall = true;
        return other instanceof FinalEqualsWithOtherCall &&
               other == this;
    }

    public final boolean equals(FinalEqualsWithOtherCall other) {
        System.out.println("Equals with specialized parameter");
        return nonstandardEqualsValue;
    }

    private void otherMethod() {
        System.out.println("Calling this method from equals shouldn't cause stack overflows");
    }

}
