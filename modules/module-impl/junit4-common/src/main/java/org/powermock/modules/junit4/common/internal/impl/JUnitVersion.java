package org.powermock.modules.junit4.common.internal.impl;

import junit.runner.Version;

public class JUnitVersion {

    public static boolean isGreaterThanOrEqualTo(String version) {
        final String currentVersion = getJUnitVersion();
        return new VersionComparator().compare(currentVersion, version) >= 0;
    }

    public static String getJUnitVersion() {
        return Version.id();
    }
}
