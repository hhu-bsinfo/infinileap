package de.hhu.bsinfo.neutrino.util;

import java.util.Locale;

public final class SystemUtil {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

    public static final OperatingSystem CURRENT_OS;
    public static final Architecture CURRENT_ARCH;

    static {
        CURRENT_OS = getOperatingSystem();
        CURRENT_ARCH = getArchitecture();
    }

    private SystemUtil() {}

    private static OperatingSystem getOperatingSystem() {
        if (OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.indexOf("aix") > 0) {
            return OperatingSystem.UNIX;
        }

        if (OS_NAME.contains("win")) {
            return OperatingSystem.WINDOWS;
        }

        if (OS_NAME.contains("mac")) {
            return OperatingSystem.OSX;
        }

        if (OS_NAME.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }

        return OperatingSystem.UNKNOWN;
    }

    private static Architecture getArchitecture() {
        if ("x86".equals(OS_ARCH) || "i386".equals(OS_ARCH) || "i486".equals(OS_ARCH)
                                  || "i586".equals(OS_ARCH) || "i686".equals(OS_ARCH)) {
            return Architecture.X86_32;
        }

        if ("x86_64".equals(OS_ARCH) || "amd64".equals(OS_ARCH)) {
            return Architecture.X86_64;
        }

        if ("powerpc".equals(OS_ARCH)) {
            return Architecture.PPC;
        }

        return Architecture.UNKNOWN;
    }


}
