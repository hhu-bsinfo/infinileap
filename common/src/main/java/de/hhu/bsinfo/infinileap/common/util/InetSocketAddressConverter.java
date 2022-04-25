package de.hhu.bsinfo.infinileap.common.util;

import java.net.InetSocketAddress;

public class InetSocketAddressConverter {

    private final int defaultPort;

    public InetSocketAddressConverter(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public InetSocketAddress convert(final String address) {

        final var splittedAddress = address.split(":");
        if (splittedAddress.length == 0 || splittedAddress.length > 2) {
            throw new IllegalArgumentException("No connection string specified");
        }

        String hostname = splittedAddress[0];
        int port = defaultPort;
        if (splittedAddress.length > 1) {
            try {
                port = Integer.parseInt(splittedAddress[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid port specified");
            }
        }

        return new InetSocketAddress(hostname, port);
    }
}
