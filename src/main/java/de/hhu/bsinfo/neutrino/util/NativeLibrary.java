package de.hhu.bsinfo.neutrino.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

@SuppressWarnings("LoadLibraryWithNonConstantString")
public class NativeLibrary {

    private static final String BASE_PATH = "/NATIVE/x86_64/";
    private static final String LIBRARY_FORMAT = "lib%s.so";

    private NativeLibrary() {}

    public static void load(final String name) {
        if (SystemUtil.CURRENT_OS != OperatingSystem.UNIX || SystemUtil.CURRENT_ARCH != Architecture.X86_64) {
            throw new IllegalStateException("Neutrino is only supported on linux running a 64 bit JRE");
        }

        try {
            System.loadLibrary(name);
        } catch (Throwable ignored) {
            loadFromResources(name);
        }
    }

    private static void loadFromResources(final String name) {
        var libFilename = String.format(LIBRARY_FORMAT, name);
        try (var resource = NativeLibrary.class.getResourceAsStream(BASE_PATH + libFilename)) {
            var tmpDir = Files.createTempDirectory(null);
            var target = tmpDir.resolve(libFilename);
            Files.copy(resource, tmpDir.resolve(libFilename), StandardCopyOption.REPLACE_EXISTING);
            System.load(target.toAbsolutePath().toString());
            deleteDirectory(tmpDir);
        } catch (IOException ignored) {
            throw new UnsatisfiedLinkError("Could not load native library " + name);
        }
    }

    private static void deleteDirectory(final Path path) {
        try (var pathStream = Files.walk(path)){
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ignored) {
            // Can't do anything if deleting directory failed
        }
    }
}
