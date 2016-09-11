package at.favre.tools.auninst.parser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdbDeviceParserTest {
    @Test
    public void testParseVariousDevices() throws Exception {
        AdbDevice device1 = AdbDevicesParser.parseDeviceLine("emulator-5154\toffline");
        AdbDevice device2 = AdbDevicesParser.parseDeviceLine("emulator-5154\tdevice");
        AdbDevice device3 = AdbDevicesParser.parseDeviceLine("ENU8N15B13003437\t\tunauthorized");
        AdbDevice device4 = AdbDevicesParser.parseDeviceLine("ENU8N15B13003437\t\tdevice");

        assertEquals(new AdbDevice("emulator-5154", AdbDevice.Status.OFFLINE, true), device1);
        assertEquals(new AdbDevice("emulator-5154", AdbDevice.Status.OK, true), device2);
        assertEquals(new AdbDevice("ENU8N15B13003437", AdbDevice.Status.UNAUTHORIZED, false), device3);
        assertEquals(new AdbDevice("ENU8N15B13003437", AdbDevice.Status.OK, false), device4);
    }

    @Test
    public void testParseAdbDevices() throws Exception {
        AdbDevicesParser parser = new AdbDevicesParser();
        List<AdbDevice> devices = parser.parse("List of devices attached\n" +
                "ENU8N15B13003437\t\tdevice\n" +
                "emulator-5154\tdevice\n");
        assertTrue(devices.size() == 2);
        assertEquals(new AdbDevice("ENU8N15B13003437", AdbDevice.Status.OK, false),devices.get(0));
        assertEquals(new AdbDevice("emulator-5154", AdbDevice.Status.OK, true),devices.get(1));
    }

    @Test
    public void testParseAdbSingleDevice() throws Exception {
        AdbDevicesParser parser = new AdbDevicesParser();
        List<AdbDevice> devices = parser.parse("List of devices attached\n" +
                "ENU8N15B13003437\t\tunauthorized\n");
        assertTrue(devices.size() == 1);
        assertEquals(new AdbDevice("ENU8N15B13003437", AdbDevice.Status.UNAUTHORIZED, false),devices.get(0));
    }

    @Test
    public void testParseAdbNoDevices() throws Exception {
        AdbDevicesParser parser = new AdbDevicesParser();
        List<AdbDevice> devices = parser.parse("List of devices attached\n");
        assertTrue(devices.isEmpty());
    }
}
