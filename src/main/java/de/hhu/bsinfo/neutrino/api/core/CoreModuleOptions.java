package de.hhu.bsinfo.neutrino.api.core;

import de.hhu.bsinfo.neutrino.api.module.Module;
import de.hhu.bsinfo.neutrino.api.module.ModuleOptions;
import de.hhu.bsinfo.neutrino.api.util.InitializationException;
import de.hhu.bsinfo.neutrino.verbs.Context;
import de.hhu.bsinfo.neutrino.verbs.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("FieldMayBeFinal")
public class CoreModuleOptions extends ModuleOptions {

    private int deviceNumber = 0;

    private int portNumber = 1;

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }
}