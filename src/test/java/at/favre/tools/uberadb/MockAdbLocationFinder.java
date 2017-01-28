package at.favre.tools.uberadb;

public class MockAdbLocationFinder implements AdbLocationFinder {
    @Override
    public LocationResult find(CmdProvider cmdProvider, String customPath) {
        return new LocationResult(Location.PATH, new String[]{"C:\\"});
    }
}
