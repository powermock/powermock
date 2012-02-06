package org.powermock.modules.junit4.common.internal.impl;

import junit.runner.Version;

public class VersionCompatibility {

    private int version;
    private int major;

    public boolean isGreaterOrEquals( int version, int major ) {
        return this.version > version || this.version == version && this.major >= major;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public void setMajor(short major) {
        this.major = major;
    }

    public static VersionCompatibility getJUnitVersion() {
        String version = Version.id();
        int dot = version.indexOf('.');

        VersionCompatibility compat = new VersionCompatibility();
        if (dot > 0) {
            compat.setVersion( Short.parseShort( version.substring(0, dot) ) );
            // Make sure that only one dot exists
            dot = version.indexOf('.', dot + 1);
            if (dot > 0) {
                /*
                            * If minor version such as 4.8.1 then remove the last digit,
                            * e.g. "4.8.1" becomes "4.8".
                            */
                version = version.substring(0, dot);
            }
            compat.setMajor( Short.parseShort( version.substring( version.indexOf('.') + 1 ) ) );
        } else {
            compat.setVersion( Short.parseShort( version ) );
        }
        try {
            return compat;
        } catch (NumberFormatException e) {
            // If this happens we revert to JUnit 4.4 runner
            compat.setVersion( ( short ) 4 );
            compat.setMajor( ( short ) 4 );
            return compat;
        }
    }

}
