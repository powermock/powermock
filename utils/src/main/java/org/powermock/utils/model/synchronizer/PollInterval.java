package org.powermock.utils.model.synchronizer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.powermock.utils.internal.PollSpecificationImpl;

public enum PollInterval {
    ONE_HUNDRED_MILLISECONDS(new PollSpecificationImpl(100, MILLISECONDS)), TWO_HUNDRED_MILLISECONDS(new PollSpecificationImpl(200,
            MILLISECONDS)), FIVE_HUNDRED_MILLISECONDS(new PollSpecificationImpl(500, MILLISECONDS)), ONE_SECOND(new PollSpecificationImpl(
            1, SECONDS)), TWO_SECONDS(new PollSpecificationImpl(2, SECONDS)), FIVE_SECONDS(new PollSpecificationImpl(5, SECONDS)), TEN_SECONDS(
            new PollSpecificationImpl(10, SECONDS)), ONE_MINUTE(new PollSpecificationImpl(1, MINUTES)), TWO_MINUTES(
            new PollSpecificationImpl(2, MINUTES)), FIVE_MINUTES(new PollSpecificationImpl(5, MINUTES)), TEN_MINUTES(
            new PollSpecificationImpl(110, MINUTES));

    private final PollSpecification spec;

    private PollInterval(PollSpecification duration) {
        this.spec = duration;
    }

    public PollSpecification getPollSpecification() {
        return spec;
    }
}