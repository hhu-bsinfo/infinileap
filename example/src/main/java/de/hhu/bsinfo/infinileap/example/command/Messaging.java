package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.binding.Configuration;
import de.hhu.bsinfo.infinileap.binding.Context;
import de.hhu.bsinfo.infinileap.binding.ContextParameters;
import de.hhu.bsinfo.infinileap.binding.WorkerParameters;
import de.hhu.bsinfo.infinileap.binding.util.ThreadMode;
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
        var contextParameters = new ContextParameters()
                .setFeatures(ContextParameters.Feature.TAG)
                .setRequestSize(DEFAULT_REQUEST_SIZE);

        // Read configuration (Environment Variables)
        var configuration = Configuration.read();

        // Initialize UCP context
        var context = Context.initialize(contextParameters, configuration);

        var workerParameters = new WorkerParameters()
                .setThreadMode(ThreadMode.SINGLE);

        var worker = context.createWorker(workerParameters);

        var address = worker.getAddress();

        System.out.println(address);
    }
}
