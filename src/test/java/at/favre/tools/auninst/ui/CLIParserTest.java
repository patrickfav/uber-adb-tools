package at.favre.tools.auninst.ui;

import org.apache.tools.ant.types.Commandline;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CLIParserTest {
    @Test
    public void testSimpleWithOnlyFilter() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.*"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, false, false, false, false, false);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.*,com.google.*"));
        Arg expectedArg1 = new Arg("com.android.*,com.google.*", null, null, false, false, false, false, false, false);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testAdbPathArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -adbPath \"C:\\test\\my path\\adb.exe\""));
        Arg expectedArg = new Arg("com.android.*", "C:\\test\\my path\\adb.exe", null, false, false, false, false, false, false);
        assertEquals(parsedArg, expectedArg);

        Arg parsedArg1 = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -adbPath C:\\test\\mypath\\adb.exe"));
        Arg expectedArg1 = new Arg("com.android.*", "C:\\test\\mypath\\adb.exe", null, false, false, false, false, false, false);
        assertEquals(parsedArg1, expectedArg1);
    }

    @Test
    public void testDeviceArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -" + CLIParser.ARG_DEVICE_SERIAL + " IR2131236"));
        Arg expectedArg = new Arg("com.android.*", null, "IR2131236", false, false, false, false, false, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testDryrunArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -dryRun"));
        Arg expectedArg = new Arg("com.android.*", null, null, true, false, false, false, false, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testSkipEmusArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -skipEmulators"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, true, false, false, false, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testKeepDataArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -keepData"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, false, true, false, false, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testQuietArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -quiet"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, false, false, true, false, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testDebugArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -debug"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, false, false, false, true, false);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testForceArg() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -force"));
        Arg expectedArg = new Arg("com.android.*", null, null, false, false, false, false, false, true);
        assertEquals(parsedArg, expectedArg);
    }

    @Test
    public void testArgCombination() throws Exception {
        Arg parsedArg = CLIParser.parse(asArgArray("-" + CLIParser.ARG_PACKAGE_FILTER + " com.android.* -" + CLIParser.ARG_DEVICE_SERIAL + " IR2131236 -adbPath C:\\test\\mypath\\adb.exe -skipEmulators -keepData"));
        Arg expectedArg = new Arg("com.android.*", "C:\\test\\mypath\\adb.exe", "IR2131236", false, true, true, false, false, false);
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
