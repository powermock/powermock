package org.powermock.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class JavaVersionTest {

    @Test
    public void should_return_true_for_current_version_that_if_high_than16() {
        assertThat(JavaVersion.JAVA_RECENT.atLeast(JavaVersion.JAVA_1_6))
                .isTrue();
    }

}