package at.favre.tools.uberadb;

import at.favre.tools.uberadb.actions.Commons;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.ui.Arg;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class AdbToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private MockAdbCmdProvider adbMockCmdProviderMultiDevices;
    private MockAdbCmdProvider adbMockCmdProviderSingleDevice;
    private List<String> installedPackages;
    private List<AdbDevice> adbDevices;
    private File apks;

    @Before
    public void setup() throws Exception {
        installedPackages = Arrays.asList("com.example.app1", "com.example.app2", "com.example.app3", "com.example.app4");
        adbDevices = Arrays.asList(
                new AdbDevice("S128376", AdbDevice.Status.OK, "TestBrand", "iAndroid", false),
                new AdbDevice("S943584", AdbDevice.Status.OK, "TestSam", "Galaxy S0", false),
                new AdbDevice("emulator-5154", AdbDevice.Status.OK, "Android_SDK_built_for_x86", "sdk_google_phone_x86", true));
        adbMockCmdProviderMultiDevices = new MockAdbCmdProvider(adbDevices, installedPackages, true);
        adbMockCmdProviderSingleDevice = new MockAdbCmdProvider(Collections.singletonList(adbDevices.get(0)), installedPackages, true);
        apks = new File(getClass().getClassLoader().getResource("apks").toURI().getPath());
    }

    @After
    public void tearDown() {
        System.out.println(Arrays.toString(adbMockCmdProviderMultiDevices.getHistory().toArray()));
    }

    @Test
    public void testSimpleUninstallMultiDevices() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, adbMockCmdProviderMultiDevices.installedCount() * adbMockCmdProviderMultiDevices.deviceCount(), 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleInstallMultiDevices() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        assertNotNull(result);
        check(result, apks.listFiles().length * adbDevices.size(), 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleInstallMultiDevicesMultipleFiles() throws Exception {
        File[] files = apks.listFiles();
        Arg arg = new Arg(new String[]{files[0].getAbsolutePath(), files[1].getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, true, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        assertNotNull(result);
        check(result, 2 * adbDevices.size(), 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleInstallMultiDevicesGrant() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, true, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        assertNotNull(result);
        check(result, apks.listFiles().length * adbDevices.size(), 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleUninstallOneDevice() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderSingleDevice);
        check(result, installedPackages.size(), 0, adbMockCmdProviderSingleDevice.deviceCount());
    }

    @Test
    public void testSimpleUninstallOneDeviceTwoPackages() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.app1", "com.example.app2"}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderSingleDevice);
        check(result, 2, 0, adbMockCmdProviderSingleDevice.deviceCount());
    }

    @Test
    public void testSimpleInstallOneDevice() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderSingleDevice);
        check(result, apks.listFiles().length, 0, adbMockCmdProviderSingleDevice.deviceCount());
    }

    @Test
    public void testSimpleInstallMultiDevicesSingleApk() throws Exception {
        Arg arg = new Arg(new String[]{apks.listFiles()[0].getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, adbMockCmdProviderMultiDevices.deviceCount(), 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleUninstallOneDeviceWithFail() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, new MockAdbCmdProvider(Collections.singletonList(adbDevices.get(0)), installedPackages, false));
        check(result, 0, installedPackages.size(), 1);
    }

    @Test
    public void testSimpleInstallOneDeviceWithFail() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, new MockAdbCmdProvider(Collections.singletonList(adbDevices.get(0)), installedPackages, false));
        check(result, 0, apks.listFiles().length, 1);
    }

    @Test
    public void testSimpleUninstallMultiDevicesSelectSpecific() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, adbDevices.get(0).serial, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, adbMockCmdProviderMultiDevices.installedCount(), 0, 1);
    }

    @Test
    public void testSimpleUninstallMultiDevicesDryRun() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, null, null, null, 0, true, false, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, 0, 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleUninstallMultiDevicesSkipEmu() throws Exception {
        Arg arg = new Arg(new String[]{"com.example.*"}, null, null, null, null, 0, false, true, false, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, adbMockCmdProviderMultiDevices.installedCount() * (adbMockCmdProviderMultiDevices.deviceCount() - 1), 0, adbMockCmdProviderMultiDevices.deviceCount() - 1);
    }

    @Test
    public void testSimpleInstallMultiDevicesSelectSpecific() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, adbDevices.get(0).serial, null, null, 0, false, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, apks.listFiles().length, 0, 1);
    }

    @Test
    public void testSimpleInstallMultiDevicesDryRun() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, true, false, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, 0, 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleInstallMultiDevicesSkipEmu() throws Exception {
        Arg arg = new Arg(new String[]{apks.getAbsolutePath()}, null, null, null, null, 0, false, true, false, false, false, true, false, false, false, Arg.Mode.INSTALL);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, apks.listFiles().length * (adbDevices.size() - 1), 0, adbMockCmdProviderMultiDevices.deviceCount() - 1);
    }

    @Test
    public void testSimpleBugReport() throws Exception {
        Arg arg = new Arg(new String[]{temporaryFolder.newFolder().getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, false, false, false, false, Arg.Mode.BUGREPORT);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderMultiDevices);
        check(result, 0, 0, adbMockCmdProviderMultiDevices.deviceCount());
    }

    @Test
    public void testSimpleBugReportSingleDevice() throws Exception {
        Arg arg = new Arg(new String[]{temporaryFolder.newFolder().getAbsolutePath()}, null, null, null, null, 0, false, false, false, false, false, false, false, false, false, Arg.Mode.BUGREPORT);
        Commons.ActionResult result = AdbTool.execute(arg, adbMockCmdProviderSingleDevice);
        check(result, 0, 0, adbMockCmdProviderSingleDevice.deviceCount());
    }

    private static void check(Commons.ActionResult result, int expectedSuccess, int expectedFail, int expectedDevices) {
        assertNotNull(result);
        assertEquals(expectedSuccess, result.successCount);
        assertEquals(expectedFail, result.failureCount);
        assertEquals(expectedDevices, result.deviceCount);
    }
}

