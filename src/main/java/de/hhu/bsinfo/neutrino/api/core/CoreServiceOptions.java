package de.hhu.bsinfo.neutrino.api.core;

import de.hhu.bsinfo.neutrino.api.util.service.ServiceOptions;

@SuppressWarnings("FieldMayBeFinal")
public class CoreServiceOptions extends ServiceOptions {

    private int deviceNumber = 0;

    private int portNumber = 1;

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }
}