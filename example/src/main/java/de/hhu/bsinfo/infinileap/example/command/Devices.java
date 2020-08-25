package de.hhu.bsinfo.infinileap.example.command;

import de.hhu.bsinfo.infinileap.Device;
import de.hhu.bsinfo.infinileap.Verbs;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;

@Slf4j
@CommandLine.Command(
        name = "devices",
        description = "Lists all InfiniBand Host Channel Adapters.%n",
        showDefaultValues = true,
        separator = " ")
public class Devices implements Runnable {

    @Override
    public void run() {
        try (var deviceList = Verbs.queryDevices()) {
            deviceList.forEach(this::printDevice);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printDevice(Device device) {
        try (var context = Verbs.openDevice(device);
             var attributes = context.queryDevice()){
            System.out.printf("[%s] Firmware Version '%s'\n", device.name(), attributes.getFirmwareVersion());
        } catch (IOException e) {
            System.err.printf("Opening device '%s' failed.\n", device.name());
        }
    }
}
