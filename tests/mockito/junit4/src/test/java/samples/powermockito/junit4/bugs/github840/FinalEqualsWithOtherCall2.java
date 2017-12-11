package samples.powermockito.junit4.bugs.github840;

class FinalEqualsWithOtherCall2 {

    private FinalEqualsWithOtherCall resource;
    boolean equalsCalled = false;
    boolean afterInternalCall = false;
    boolean callOnResourceSucceeded = false;
    boolean actualDetermination = false;

    public void setResource(FinalEqualsWithOtherCall resource) {
        this.resource = resource;
    }

    public float method2() {
        return 2f;
    }

    @Override
    public final boolean equals(Object other) {
        if (other instanceof FinalEqualsWithOtherCall) {
            System.out.println("FinalEqualsWithOtherCall2 compared to FinalEqualsWithOtherCall");
        }
        equalsCalled = true;
        if (resource != null) {
            resource.method1();
            callOnResourceSucceeded = true;
        } else {
            System.out.println("Resource is null, that's already a problem");
        }
        someMethod();
        afterInternalCall = true;
        return actualDetermination;
    }

    private void someMethod() {
        System.out.println("And you may ask yourself, \"How did I get here?\"");
    }

}
