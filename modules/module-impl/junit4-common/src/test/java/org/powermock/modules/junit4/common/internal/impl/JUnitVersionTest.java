package org.powermock.modules.junit4.common.internal.impl;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JUnitVersionTest {

    @Test public void
    parses_version_numbers_with_chars() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.9b2"), is(false));
    }

    @Test public void
    parses_version_numbers_with_dash_snapshot() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.9-SNAPSHOT"), is(false));
    }

    @Test public void
    parses_version_numbers_with_dash_snapshot_where_version_is_before_current() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.1-SNAPSHOT"), is(true));
    }

    @Test public void
    parses_version_numbers_major_and_minor_versions() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.1.5"), is(true));
    }

    @Test public void
    parses_version_numbers_major_and_several_minor_versions() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.10.5.6"), is(false));
    }

    @Test public void
    parses_4_11_beta_1() {
        assertThat(JUnitVersion.isGreaterThanOrEqualTo("4.11-beta-1"), is(false));
    }
}
