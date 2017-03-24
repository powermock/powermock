package org.powermock.api.mockito.internal.pluginswitch;

import org.mockito.Mockito;
import org.mockito.plugins.PluginSwitch;
import org.powermock.api.mockito.internal.mockmaker.PowerMockMaker;

public class PowerMockPluginSwitch implements PluginSwitch {

    @Override
    public boolean isEnabled(String pluginClassName) {
        return true;
    }

    public static void disablePowerMockMaker() {
        PowerMockMaker.disablePowerMockMaker();
        Mockito.reset();
    }

    public static void enablePowerMockMaker() {
        PowerMockMaker.enablePowerMockMaker();
        Mockito.reset();
    }
}
