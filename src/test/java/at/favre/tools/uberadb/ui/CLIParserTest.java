package at.favre.tools.uberadb.ui;

import org.apache.tools.ant.types.Commandline;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CLIParserTest {
    @Test
    public void testSimpleUninstallWithOnlyFilter() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.*"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.*,com.google.*"));
        Arg expectedArg1 = new Arg("com.android.*,com.google.*", null, null, null, false, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testSimpleInstallWithOnlyFile() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_INSTALL + " app.apk"));
        Arg expectedArg = new Arg("app.apk", null, null, null, false, false, false, false, false, false, Arg.Mode.INSTALL);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_INSTALL + " ./"));
        Arg expectedArg1 = new Arg("./", null, null, null, false, false, false, false, false, false, Arg.Mode.INSTALL);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testSimpleBugReportWithOnlyFilter() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_BUGREPORT + " com.android.*"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, false, false, false, false, Arg.Mode.BUGREPORT);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_BUGREPORT + " com.android.*,com.google.*"));
        Arg expectedArg1 = new Arg("com.android.*,com.google.*", null, null, null, false, false, false, false, false, false, Arg.Mode.BUGREPORT);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testSimpleInstallAndUninstallShouldThrowexceptionWithOnlyFile() throws Exception {
        assertNull(CLIParser.parse(asArgArray("-" + CLIParser.ARG_INSTALL + " app.apk -" + CLIParser.ARG_UNINSTALL + " com.android.*")));
    }

    @Test
    public void testAdbPathArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -adbPath \"C:\\test\\my path\\adb.exe\""));
        Arg expectedArg = new Arg("com.android.*", "C:\\test\\my path\\adb.exe", null, null, false, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -adbPath C:\\test\\mypath\\adb.exe"));
        Arg expectedArg1 = new Arg("com.android.*", "C:\\test\\mypath\\adb.exe", null, null, false, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testDeviceArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -" + CLIParser.ARG_DEVICE_SERIAL + " IR2131236"));
        Arg expectedArg = new Arg("com.android.*", null, "IR2131236", null, false, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testDryrunArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -dryRun"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, true, false, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testSkipEmusArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -skipEmulators"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, true, false, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testKeepDataArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -keepData"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, true, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testUpgradeArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_INSTALL + " /data/app.apk -upgrade"));
        Arg expectedArg = new Arg("/data/app.apk", null, null, null, false, false, true, false, false, false, Arg.Mode.INSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testQuietArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -quiet"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, false, true, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testDebugArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -debug"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, false, false, true, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testForceArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -force"));
        Arg expectedArg = new Arg("com.android.*", null, null, null, false, false, false, false, false, true, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testForceArgInstall() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_INSTALL + " /test/app.apk -force"));
        Arg expectedArg = new Arg("/test/app.apk", null, null, null, false, false, false, false, false, true, Arg.Mode.INSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testArgCombination() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_UNINSTALL + " com.android.* -" + CLIParser.ARG_DEVICE_SERIAL + " IR2131236 -adbPath C:\\test\\mypath\\adb.exe -skipEmulators -keepData"));
        Arg expectedArg = new Arg("com.android.*", "C:\\test\\mypath\\adb.exe", "IR2131236", null, false, true, true, false, false, false, Arg.Mode.UNINSTALL);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testHelp() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-help"));
        assertNull(parsedArg);
    }

    @Test
    public void testVersion() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-version"));
        assertNull(parsedArg);
    }

    public static String[] asArgArray(String cmd) {
        return Commandline.translateCommandline(cmd);
    }
}
