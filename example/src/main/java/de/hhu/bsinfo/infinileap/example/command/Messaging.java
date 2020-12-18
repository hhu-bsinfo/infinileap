package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.Configuration;
import de.hhu.bsinfo.infinileap.binding.Context;
import de.hhu.bsinfo.infinileap.binding.ContextParameters;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "messaging",
        description = ""
)
public class Messaging implements Runnable {

    private static final long DEFAULT_REQUEST_SIZE = 1024;

    @Override
    public void run() {
        // Create context parameters
        var parameters = new ContextParameters()
                .setFeatures(ContextParameters.Feature.TAG)
                .setRequestSize(DEFAULT_REQUEST_SIZE);

        // Read configuration (Environment Variables)
        var configuration = Configuration.read();

        // Initialize UCP context
        var context = Context.initialize(parameters, configuration);

        // Print configuration
        configuration.print();
    }
}
