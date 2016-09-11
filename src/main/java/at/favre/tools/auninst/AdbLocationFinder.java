package at.favre.tools.auninst;

public class AdbLocationFinder {
    public enum Location {PATH, CUSTOM, WIN_DEFAULT}

    public static final String[] PATH_ADB = new String[]{"cmd", "/c", "adb"};
    public static final String[] WIN_DEFAULT = new String[]{"cmd","/c","%USERPROFILE%\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe"};

    public LocationResult find(String customPath) {
        if (customPath != null && CmdUtil.canRunCmd(new String[]{customPath})) {
            return new LocationResult(Location.CUSTOM, new String[]{customPath});
        }

        if (CmdUtil.canRunCmd(PATH_ADB)) {
            return new LocationResult(Location.PATH, PATH_ADB);
        } else {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                if (CmdUtil.canRunCmd(WIN_DEFAULT)) {
                    return new LocationResult(Location.WIN_DEFAULT, WIN_DEFAULT);
                }
            }
        }

        throw new IllegalStateException("Could not find adb. Not found in PATH or the usual default locations. Did you install " +
                "the Android SDK and set adb to PATH? See: http://stackoverflow.com/questions/20564514");

    }

    public static class LocationResult {
        public final Location location;
        public final String[] args;

        public LocationResult(Location location, String[] args) {
            this.location = location;
            this.args = args;
        }
    }
}
