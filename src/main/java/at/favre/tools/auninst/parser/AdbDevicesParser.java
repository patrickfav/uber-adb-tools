package at.favre.tools.auninst.parser;

import java.util.ArrayList;
import java.util.List;


public class AdbDevicesParser {
    private static final String OFFLINE = "offline";
    private static final String DEVICE = "device";
    private static final String UNAUTHORIZED = "unauthorized";

    private static final java.lang.String EMULATOR = "emulator-";

    public List<AdbDevice> parse(String adbOutput) {
        List<AdbDevice> devices = new ArrayList<>();

        String[] lines = adbOutput.split("\\n");

        if (lines.length > 1) {
            for (int i = 1; i < lines.length; i++) {
                AdbDevice d = parseDeviceLine(lines[i]);
                if (d != null) {
                    devices.add(d);
                }
            }
        }
        return devices;
    }

    static AdbDevice parseDeviceLine(String line) {
        char[] chars = line.replace("\t"," ").toCharArray();
        StringBuilder deviceName = new StringBuilder();
        String status = null;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ' ') {
                deviceName.append(chars[i]);
            } else {
                status = line.substring(i, line.length()).trim();
                break;
            }
        }

        if (status != null) {
            boolean isEmulator = false;
            if (deviceName.toString().startsWith(EMULATOR)) {
                isEmulator = true;
            }

            return new AdbDevice(deviceName.toString(), translate(status), isEmulator);
        }

        return null;
    }

    private static AdbDevice.Status translate(String status) {
        switch (status) {
            case OFFLINE:
                return AdbDevice.Status.OFFLINE;
            case DEVICE:
                return AdbDevice.Status.OK;
            case UNAUTHORIZED:
                return AdbDevice.Status.UNAUTHORIZED;
            default:
                return AdbDevice.Status.UNKOWN;
        }
    }
}
