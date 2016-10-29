package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.AdbTool;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.util.CmdUtil;
import at.favre.tools.uberadb.util.MiscUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class BugReport {

    public static void create(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, AdbDevice device, List<String> allPackages) throws Exception {
        Commons.logLoud("create bug report:");

        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());

        File outFolder;
        if (arguments.mainArgument != null && !arguments.mainArgument.isEmpty()) {
            outFolder = new File(arguments.mainArgument);
            if (!outFolder.exists() && !outFolder.mkdirs()) {
                throw new IllegalStateException("could not create directory " + arguments.mainArgument);
            }
        } else {
            outFolder = new File(AdbTool.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        }

        if (arguments.reportFilterIntent != null && arguments.reportFilterIntent.length >= 2) {
            Set<String> filteredPackages = new PackageMatcher(allPackages).findMatches(
                    PackageMatcher.parseFiltersArg(arguments.reportFilterIntent[0]));

            for (String filteredPackage : filteredPackages) {
                String[] copy = Arrays.copyOfRange(arguments.reportFilterIntent, 1, arguments.reportFilterIntent.length);
                for (int i = 0; i < copy.length; i++) {
                    copy[i] = copy[i].replace("${package}", filteredPackage);
                }

                Commons.runAdbCommand(CmdUtil.concat(new String[]{"-s", device.serial, "shell", "am",}, copy), cmdProvider, adbLocation);

                String intentStatus = "\texecute command for " + filteredPackage + " - adb shell am " + Arrays.toString(copy);
                Commons.log(intentStatus, arguments);
                Thread.sleep(100);
            }
        }

        File zipFile = new File(outFolder, "bugreport-" + dateTimeString + "--" + device.model + "--" + device.serial + ".zip");

        List<BugReportAction> actions = new ArrayList<>();

        actions.add(new BugReportAction(
                "\twake up screen and take screenshot",
                "/sdcard/bugreport_tempfile_screenshot.png",
                new File(outFolder.getAbsolutePath(), "screen-" + dateTimeString + "--" + device.model + ".png"),
                new String[]{"shell", "screencap", "/sdcard/bugreport_tempfile_screenshot.png"}));

        actions.add(new BugReportAction(
                "\tcreate logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat",
                new File(outFolder.getAbsolutePath(), "logcat-" + dateTimeString + "--" + device.model + ".txt"),
                new String[]{"logcat", "-b", "main", "-d", "-f", "/sdcard/bugreport_tempfile_logcat"}));
        actions.add(new BugReportAction(
                "\tcreate events logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat_events",
                new File(outFolder.getAbsolutePath(), "events-" + dateTimeString + "--" + device.model + ".txt"),
                new String[]{"logcat", "-b", "events", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_events"}));
        actions.add(new BugReportAction(
                "\tcreate radio logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat_radio",
                new File(outFolder.getAbsolutePath(), "radio-" + dateTimeString + "--" + device.model + ".txt"),
                new String[]{"logcat", "-b", "radio", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_radio"}));

        Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "input", "keyevent", "KEYCODE_WAKEUP"}, cmdProvider, adbLocation);

        for (BugReportAction action : actions) {
            Commons.runAdbCommand(CmdUtil.concat(new String[]{"-s", device.serial}, action.command), cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "pull", action.deviceTempFile, action.localTempFile.getAbsolutePath()}, cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "rm", "-f", action.deviceTempFile}, cmdProvider, adbLocation);
            Commons.log(String.format(Locale.US, action.log + " (%.2fkB)", (double) action.localTempFile.length() / 1024.0), arguments);
        }


        List<File> tempFilesToZip = new ArrayList<>();
        for (BugReportAction action : actions) {
            if (action.localTempFile.exists()) {
                tempFilesToZip.add(action.localTempFile);
            } else {
                Commons.log("could not find local file " + action.localTempFile, arguments);
            }
        }

        MiscUtil.zip(zipFile, tempFilesToZip);

        for (BugReportAction action : actions) {
            if (!action.localTempFile.delete()) {
                Commons.log("could not delete " + action.localTempFile.getAbsolutePath(), arguments);
            }
        }

        if (!zipFile.exists()) {
            throw new IllegalStateException("could not create zip file " + zipFile);
        }

        Commons.log(String.format(Locale.US, "\ttemp files removed and zip %s (%.2fkB) created", zipFile.getAbsolutePath(), (double) zipFile.length() / 1024.0), arguments);
    }


    private static class BugReportAction {
        final String deviceTempFile;
        final File localTempFile;
        final String[] command;
        final String log;

        BugReportAction(String log, String deviceTempFile, File localTempFile, String[] command) {
            this.deviceTempFile = deviceTempFile;
            this.localTempFile = localTempFile;
            this.command = command;
            this.log = log;
        }
    }

}
