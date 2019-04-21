package org.powermock.utils;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NumberUtilsTest {

    @Test
    public void should_parse_and_return_float() {
        assertThat(NumberUtils.toFloat("133.1", 0.0f))
                .as("String parsed to float")
                .isEqualTo(133.1f, Offset.offset(0.01f));

    }

    @Test
    public void should_return_default_value_when_cannot_parse() {
        assertThat(NumberUtils.toFloat("11,1", 0.0f))
                .as("String parsed to float")
                .isEqualTo(0.0f, Offset.offset(0.01f));
    }

    @Test
    public void should_return_default_value_when_string_is_null() {
        assertThat(NumberUtils.toFloat(null, 0.0f))
                .as("String parsed to float")
                .isEqualTo(0.0f, Offset.offset(0.01f));
    }

}