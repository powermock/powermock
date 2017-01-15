package samples.powermockito.junit4.bugs.github701;

abstract class OverridesEquals {
    @Override
    public boolean equals (final Object other) {
        return this == other;
    }
}