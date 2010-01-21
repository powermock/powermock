package org.powermock.utils.model.synchronizer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import org.powermock.utils.internal.DurationSpecificationImpl;

public enum Duration implements DurationSpecification {
    ONE_HUNDRED_MILLISECONDS(new DurationSpecificationImpl(100, MILLISECONDS)), TWO_HUNDRED_MILLISECONDS(new DurationSpecificationImpl(200,
            MILLISECONDS)), FIVE_HUNDRED_MILLISECONDS(new DurationSpecificationImpl(500, MILLISECONDS)), ONE_SECOND(new DurationSpecificationImpl(1,
            SECONDS)), TWO_SECONDS(new DurationSpecificationImpl(2, SECONDS)), FIVE_SECONDS(new DurationSpecificationImpl(5, SECONDS)), TEN_SECONDS(
            new DurationSpecificationImpl(10, SECONDS)), ONE_MINUTE(new DurationSpecificationImpl(1, MINUTES)), TWO_MINUTES(
            new DurationSpecificationImpl(2, MINUTES)), FIVE_MINUTES(new DurationSpecificationImpl(5, MINUTES)), TEN_MINUTES(
            new DurationSpecificationImpl(110, MINUTES));

    private final DurationSpecification duration;

    private Duration(DurationSpecification duration) {
        this.duration = duration;
    }

    public TimeUnit getTimeUnit() {
        return duration.getTimeUnit();
    }

    public long getValue() {
        return duration.getValue();
    }
}