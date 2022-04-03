package de.hhu.infinileap.engine.command.util;

import picocli.CommandLine;

import java.net.InetSocketAddress;

public class InetSocketAddressConverter implements CommandLine.ITypeConverter<InetSocketAddress> {

    private final int m_defaultPort;

    public InetSocketAddressConverter(int p_defaultPort) {
        m_defaultPort = p_defaultPort;
    }

    @Override
    public InetSocketAddress convert(final String p_address) throws Exception {

        final String[] splittedAddress = p_address.split(":");

        if (splittedAddress.length == 0 || splittedAddress.length > 2) {
            throw new CommandLine.TypeConversionException("No connection string specified");
        }

        String hostname = splittedAddress[0];

        int port = m_defaultPort;
        if (splittedAddress.length > 1) {
            try {
                port = Integer.parseInt(splittedAddress[1]);
            } catch (NumberFormatException e) {
                throw new CommandLine.TypeConversionException("Invalid port specified");
            }
        }

        try {
            return new InetSocketAddress(hostname, port);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}
