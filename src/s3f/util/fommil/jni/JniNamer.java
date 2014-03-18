package s3f.util.fommil.jni;

import java.io.File;

import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;

/**
 * Calculates predictable JNI library names from a library name and the
 * platform.
 *
 * @author Sam Halliday
 */
public class JniNamer {

    public static Logger getLogger(){
        throw new Error("LoggerNotFound");
    }
    
    /**
     * @param stem
     * @return a predictable library name for the stem and platform.
     */
    public static String getJniName(String stem) {
        String arch = arch();
        String abi = abi(arch);
        String os = os();
        String extension = extension(os);
        String jniName = stem + "-" + os + "-" + arch + abi + "." + extension;
        System.out.println(jniName);
        return jniName;
    }

    public static String arch() {
        String arch = System.getProperty("os.arch", "").toLowerCase();
        if (arch.equals("x86") || arch.equals("i386") || arch.equals("i486")
                || arch.equals("i586") || arch.equals("i686")) {
            return "i686";
        }
        if (arch.equals("x86_64") || arch.equals("amd64")) {
            return "x86_64";
        }
        if (arch.equals("ia64")) {
            return "ia64";
        }
        if (arch.equals("arm")) {
            return "arm";
        }
        if (arch.equals("armv5l")) {
            return "armv5l";
        }
        if (arch.equals("armv6l")) {
            return "armv6l";
        }
        if (arch.equals("armv7l")) {
            return "armv7l";
        }
        if (arch.equals("sparc")) {
            return "sparc";
        }
        if (arch.equals("sparcv9")) {
            return "sparcv9";
        }
        if (arch.equals("pa_risc2.0")) {
            return "risc2";
        }
        if (arch.equals("ppc")) {
            return "ppc";
        }
        if (arch.startsWith("ppc")) {
            return "ppc64";
        }

        getLogger().warning("unrecognised architecture: " + arch);
        return "unknown";

    }

    // alternative: https://github.com/sgothel/gluegen/blob/master/src/java/jogamp/common/os/PlatformPropsImpl.java#L211
    public static String abi(String arch) {
        if (!arch.startsWith("arm")) {
            return "";
        }
        try {
            // http://docs.oracle.com/javase/tutorial/deployment/doingMoreWithRIA/properties.html
            for (String prop : new String[]{"sun.boot.library.path", "java.library.path", "java.home"}) {
                String value = System.getProperty(prop, "");
                getLogger().config(prop + ": " + value);
                if (value.matches(".*(gnueabihf|armhf).*")) {
                    return "hf";
                }
            }
            for (String dir : new String[]{"/lib/arm-linux-gnueabihf", "/usr/lib/arm-linux-gnueabihf"}) {
                File file = new File(dir);
                if (file.exists()) {
                    return "hf";
                }
            }
            return "";
        } catch (SecurityException e) {
            getLogger().log(WARNING, "unable to detect ABI", e);
            return "unknown";
        }
    }

    public static String os() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.startsWith("linux")) {
            return "linux";
        }
        if (os.startsWith("windows")) {
            return "win";
        }
        if (os.startsWith("mac os x") || os.startsWith("darwin")) {
            return "osx";
        }
        if (os.startsWith("freebsd")) {
            return "freebsd";
        }
        if (os.startsWith("android")) {
            return "android";
        }
        if (os.startsWith("sunos")) {
            return "sun";
        }
        if (os.startsWith("hp-ux")) {
            return "hpux";
        }
        if (os.startsWith("kd")) {
            return "kd";
        }
        getLogger().warning("unable to detect OS type: " + os);
        return "unknown";
    }

    public static String extension(String os) {
        if (os.equals("win")) {
            return "dll";
        }
        if (os.equals("osx")) {
            return "jnilib";
        }
        return "so";
    }
}
