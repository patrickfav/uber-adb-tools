package at.favre.tools.uberadb;

import at.favre.tools.uberadb.util.CmdUtil;

import java.io.File;

public class AdbLocationFinder {
    public enum Location {PATH, CUSTOM, WIN_DEFAULT, MAC_DEFAULT, LINUX_DEFAULT, ANDROID_HOME}


    private static final String WIN_DEFAULT_SDK = "\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe";
    private static final String WIN_DEFAULT_ANDROID_HOME = "\\platform-tools\\adb.exe";

    private static final String[] MAC_DEFAULT = new String[]{"/usr/local/opt/android-sdk/platform-tools/adb"};
    private static final String MAC_DEFAULT_HOME = "/Library/Android/sdk/platform-tools/adb";
    private static final String MAC_DEFAULT_ANDROID_HOME = "/platform-tools/adb";

    private static final String[] LINUX_DEFAULT = MAC_DEFAULT;
    private static final String LINUX_DEFAULT_ANDROID_HOME = MAC_DEFAULT_ANDROID_HOME;


    LocationResult find(String customPath) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (customPath != null && new File(customPath).exists() && CmdUtil.canRunCmd(new String[]{customPath})) {
            return new LocationResult(Location.CUSTOM, new String[]{customPath});
        }

        File pathAdbExe = CmdUtil.checkAndGetFromPATHEnvVar("adb");

        if (pathAdbExe != null) {
            return new LocationResult(Location.PATH, new String[]{pathAdbExe.getAbsolutePath()});
        }

        String androidHome = System.getenv().get("ANDROID_HOME");
        String userPath = System.getProperty("user.home");

        if (osName.contains("win")) {
            userPath = System.getenv().get("USERPROFILE");

            if (userPath != null && CmdUtil.canRunCmd(new String[]{userPath + WIN_DEFAULT_SDK})) {
                return new LocationResult(Location.WIN_DEFAULT, new String[]{userPath + WIN_DEFAULT_SDK});
            }
            if (androidHome != null && CmdUtil.canRunCmd(new String[]{androidHome + WIN_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{userPath + WIN_DEFAULT_ANDROID_HOME});
            }
        } else if (osName.contains("mac")) {
            if (CmdUtil.canRunCmd(MAC_DEFAULT)) {
                return new LocationResult(Location.MAC_DEFAULT, MAC_DEFAULT);
            }
            if (userPath != null && CmdUtil.canRunCmd(new String[]{userPath + MAC_DEFAULT_HOME})) {
                return new LocationResult(Location.MAC_DEFAULT, new String[]{userPath + MAC_DEFAULT_HOME});
            }
            if (androidHome != null && CmdUtil.canRunCmd(new String[]{androidHome + MAC_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{androidHome + MAC_DEFAULT_ANDROID_HOME});
            }
        } else if (osName.contains("nix")) {
            if (CmdUtil.canRunCmd(LINUX_DEFAULT)) {
                return new LocationResult(Location.LINUX_DEFAULT, LINUX_DEFAULT);
            }
            if (androidHome != null && CmdUtil.canRunCmd(new String[]{androidHome + LINUX_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{androidHome + LINUX_DEFAULT_ANDROID_HOME});
            }

        }

        throw new IllegalStateException("Could not find adb. Not found in PATH or the usual default locations. Did you install " +
                "the Android SDK and set adb to PATH? As alternative you could use the '-adbPath' argument. See: http://stackoverflow.com/questions/20564514");
    }

    public static class LocationResult {
        public final Location location;
        public final String[] args;

        public LocationResult(Location location, String[] args) {
            this.location = location;
            this.args = args;
        }

        public String arg() {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg + " ");
            }
            return sb.toString();
        }
    }
}
