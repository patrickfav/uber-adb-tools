package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.AdbTool;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.util.CmdUtil;
import at.favre.tools.uberadb.util.FileUtil;
import at.favre.tools.uberadb.util.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

        File zipFile = new File(outFolder, "bugreport-" + dateTimeString + "-" + device.model + "-" + device.serial + ".zip");
        File tmpFolder = Files.createTempDirectory("adbtools-").toFile();
        List<BugReportAction> actions = new ArrayList<>();

        actions.add(new BugReportAction(
                "\twake up screen and take screenshot",
                "/sdcard/bugreport_tempfile_screenshot.png",
                new File(tmpFolder, "screen-" + dateTimeString + "-" + device.model + ".png"),
                new String[]{"shell", "screencap", "/sdcard/bugreport_tempfile_screenshot.png"}));
        actions.add(new BugReportAction(
                "\tcreate logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat",
                new File(tmpFolder, "logcat-" + dateTimeString + "-" + device.model + ".txt"),
                new String[]{"logcat", "-b", "main", "-d", "-f", "/sdcard/bugreport_tempfile_logcat"}));
        actions.add(new BugReportAction(
                "\tcreate events logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat_events",
                new File(tmpFolder, "events-" + dateTimeString + "-" + device.model + ".txt"),
                new String[]{"logcat", "-b", "events", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_events"}));
        actions.add(new BugReportAction(
                "\tcreate radio logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat_radio",
                new File(tmpFolder, "radio-" + dateTimeString + "-" + device.model + ".txt"),
                new String[]{"logcat", "-b", "radio", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_radio"}));

        Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "input", "keyevent", "KEYCODE_WAKEUP"}, cmdProvider, adbLocation);

        for (BugReportAction action : actions) {
            Commons.runAdbCommand(CmdUtil.concat(new String[]{"-s", device.serial}, action.command), cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "pull", action.deviceTempFile, action.localTempFile.getAbsolutePath()}, cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "rm", "-f", action.deviceTempFile}, cmdProvider, adbLocation);
            Commons.log(String.format(Locale.US, action.log + " (%.2fkB)", (double) action.localTempFile.length() / 1024.0), arguments);
        }

        File installedAppsFile = createInstalledAppsFile(tmpFolder, dateTimeString, device, allPackages, arguments);
        File runningAppsFile = createRunningAppsFile(tmpFolder, dateTimeString, device, adbLocation, cmdProvider, arguments);
        File dumpsysFile = createSelectedDumpSysFile(tmpFolder, dateTimeString, device, adbLocation, cmdProvider, arguments);

        List<File> tempFilesToZip = new ArrayList<>();
        tempFilesToZip.add(installedAppsFile);
        tempFilesToZip.add(runningAppsFile);
        tempFilesToZip.add(dumpsysFile);
        for (BugReportAction action : actions) {
            if (action.localTempFile.exists()) {
                tempFilesToZip.add(action.localTempFile);
            } else {
                Commons.log("could not find local file " + action.localTempFile, arguments);
            }
        }

        MiscUtil.zip(zipFile, tempFilesToZip);

        if (tmpFolder.exists()) {
            FileUtil.removeRecursive(tmpFolder.toPath());
        }

        if (!zipFile.exists()) {
            throw new IllegalStateException("could not create zip file " + zipFile);
        }

        Commons.log(String.format(Locale.US, "\ttemp files removed and zip %s (%.2fkB) created", zipFile.getAbsolutePath(), (double) zipFile.length() / 1024.0), arguments);
    }

    private static File createRunningAppsFile(File tmpFolder, String dateTimeString, AdbDevice device, AdbLocationFinder.LocationResult adbLocation, CmdProvider cmdProvider, Arg arguments) throws IOException {
        CmdProvider.Result result = Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "ps"}, cmdProvider, adbLocation);
        File file = new File(tmpFolder, "running_processes-" + dateTimeString + "-" + device.model + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), result.out.getBytes("UTF-8"));
        Commons.log(String.format(Locale.US, "\tcreate running process file (%.2fkB)", (double) file.length() / 1024.0), arguments);
        return file;
    }

    private static File createSelectedDumpSysFile(File tmpFolder, String dateTimeString, AdbDevice device, AdbLocationFinder.LocationResult adbLocation, CmdProvider cmdProvider, Arg arguments) throws IOException {
        List<String> dumsys = new ArrayList<>();
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "battery"}, cmdProvider, adbLocation).toString());
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "connectivity"}, cmdProvider, adbLocation).toString());
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "package"}, cmdProvider, adbLocation).toString());
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "notification"}, cmdProvider, adbLocation).toString());
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "activity"}, cmdProvider, adbLocation).toString());
        dumsys.add(Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "cpuinfo"}, cmdProvider, adbLocation).toString());


        File file = new File(tmpFolder, "dumpsys-" + dateTimeString + "-" + device.model + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), dumsys, Charset.forName("UTF-8"));
        Commons.log(String.format(Locale.US, "\tcreate dumpsys file (%.2fkB)", (double) file.length() / 1024.0), arguments);
        return file;
    }

    private static File createInstalledAppsFile(File tmpFolder, String dateTimeString, AdbDevice device, List<String> allPackages, Arg arguments) throws IOException {
        File file = new File(tmpFolder, "installed_packages-" + dateTimeString + "-" + device.model + ".txt");
        Collections.sort(allPackages);
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), allPackages, Charset.forName("UTF-8"));
        Commons.log(String.format(Locale.US, "\tcreate installed packages file (%.2fkB)", (double) file.length() / 1024.0), arguments);
        return file;
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
