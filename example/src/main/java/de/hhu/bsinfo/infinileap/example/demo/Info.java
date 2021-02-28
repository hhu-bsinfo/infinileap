package de.hhu.bsinfo.infinileap.example.demo;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.binding.ContextParameters.Feature;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(
        name = "info",
        description = "Creates a context and prints out its information."
)
public class Info implements Runnable {

    @Override
    public void run() {

        // Create context parameters
        var contextParameters = new ContextParameters()
                .setFeatures(Feature.TAG);

        try (var context = Context.initialize(contextParameters)) {
            context.printInfo();
        } catch (ControlException e) {
            log.info("Native operation failed", e);
        }
    }
}
