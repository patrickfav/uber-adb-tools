package at.favre.tools.uberadb;

/**
 * Determines the location of the adb tool
 */
public interface AdbLocationFinder {
    enum Location {PATH, CUSTOM, WIN_DEFAULT, MAC_DEFAULT, LINUX_DEFAULT, ANDROID_HOME}

    /**
     * Returns the location of the adb
     *
     * @param cmdProvider used to test if adb is executable
     * @param customPath  to check provided by user
     * @return the location
     */
    LocationResult find(CmdProvider cmdProvider, String customPath);

    class LocationResult {
        public final AdbLocationFinder.Location location;
        public final String[] args;

        public LocationResult(AdbLocationFinder.Location location, String[] args) {
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
