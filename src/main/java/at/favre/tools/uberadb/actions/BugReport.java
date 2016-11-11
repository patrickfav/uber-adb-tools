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
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class BugReport {

    private static final int MAX_IMG_BYTE_SIZE = 1024 * 1024 * 2;

    public static void create(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, AdbDevice device, List<String> allPackages) throws Exception {
        Commons.logLoud("create bug report:");

        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());

        File outFolder;
        if (arguments.mainArgument != null && arguments.mainArgument.length != 0) {
            outFolder = new File(arguments.mainArgument[0]);
            if (!outFolder.exists() && !outFolder.mkdirs()) {
                throw new IllegalStateException("could not create directory " + arguments.mainArgument);
            }
        } else {
            outFolder = new File(AdbTool.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        }

        if (arguments.reportFilterIntent != null && arguments.reportFilterIntent.length >= 2) {
            List<String> filteredPackages = new PackageMatcher(allPackages).findMatches(
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
        List<BugReportDeviceFileAction> actions = new ArrayList<>();

        actions.add(new BugReportDeviceFileAction(
                "\twake up screen and take screenshot",
                "/sdcard/bugreport_tempfile_screenshot.png",
                new File(tmpFolder, "screen-" + dateTimeString + "-" + device.model + ".png"),
                new String[]{"shell", "screencap", "/sdcard/bugreport_tempfile_screenshot.png"}, null));
        actions.add(new BugReportDeviceFileAction(
                "\tcreate logcat file and pull from device",
                "/sdcard/bugreport_tempfile_logcat",
                new File(tmpFolder, "logcat-" + dateTimeString + "-" + device.model + ".txt"),
                new String[]{"logcat", "-b", "main", "-d", "-f", "/sdcard/bugreport_tempfile_logcat"}, null));

        if (!arguments.simpleBugReport) {
            actions.add(new BugReportDeviceFileAction(
                    "\tcreate events logcat file and pull from device",
                    "/sdcard/bugreport_tempfile_logcat_events",
                    new File(tmpFolder, "events-" + dateTimeString + "-" + device.model + ".txt"),
                    new String[]{"logcat", "-b", "events", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_events"}, "additional-logcat"));
            actions.add(new BugReportDeviceFileAction(
                    "\tcreate radio logcat file and pull from device",
                    "/sdcard/bugreport_tempfile_logcat_radio",
                    new File(tmpFolder, "radio-" + dateTimeString + "-" + device.model + ".txt"),
                    new String[]{"logcat", "-b", "radio", "-d", "-f", "/sdcard/bugreport_tempfile_logcat_radio"}, "additional-logcat"));
        }

        Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "input", "keyevent", "KEYCODE_WAKEUP"}, cmdProvider, adbLocation);

        for (BugReportDeviceFileAction action : actions) {
            long start = System.currentTimeMillis();
            Commons.runAdbCommand(CmdUtil.concat(new String[]{"-s", device.serial}, action.command), cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "pull", action.deviceTempFile, action.localTempFile.getAbsolutePath()}, cmdProvider, adbLocation);
            Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "rm", "-f", action.deviceTempFile}, cmdProvider, adbLocation);
            action.localTempFile = downscaleIfNeeded(action.localTempFile, arguments);
            Commons.log(String.format(Locale.US, action.log + " (%.2fkB) (%d ms)", (double) action.localTempFile.length() / 1024.0, System.currentTimeMillis() - start), arguments);
        }

        List<MiscUtil.ZipFileDescriptor> tempFilesToZip = new ArrayList<>();
        if (!arguments.simpleBugReport) {
            tempFilesToZip.add(new MiscUtil.ZipFileDescriptor("misc", createInstalledAppsFile(tmpFolder, dateTimeString, device, allPackages, arguments)));
            tempFilesToZip.add(new MiscUtil.ZipFileDescriptor("misc", createRunningAppsFile(tmpFolder, dateTimeString, device, adbLocation, cmdProvider, arguments)));

            List<File> dumpsysFiles = createDumpSysFiles(tmpFolder, dateTimeString, device, adbLocation, cmdProvider, arguments);
            for (File dumpsysFile : dumpsysFiles) {
                tempFilesToZip.add(new MiscUtil.ZipFileDescriptor("dumpsys", dumpsysFile));
            }

            List<File> pmFiles = createPackageManagerDebugFiles(tmpFolder, dateTimeString, device, adbLocation, cmdProvider, arguments);
            for (File pmFile : pmFiles) {
                tempFilesToZip.add(new MiscUtil.ZipFileDescriptor("pm", pmFile));
            }
        }
        for (BugReportDeviceFileAction action : actions) {
            if (action.localTempFile.exists()) {
                tempFilesToZip.add(new MiscUtil.ZipFileDescriptor(action.zipSubFolder, action.localTempFile));
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

    private static File downscaleIfNeeded(File localTempFile, Arg arg) {
        if (localTempFile.exists() && localTempFile.isFile() && FileUtil.getFileExtension(localTempFile).equalsIgnoreCase("png") && localTempFile.length() > MAX_IMG_BYTE_SIZE) {

            try {
                while (localTempFile.length() > MAX_IMG_BYTE_SIZE) {
                    Thumbnails.of(localTempFile).allowOverwrite(true).scale(0.5).toFile(localTempFile);
                }
                Commons.log("\tdownscaling screenshot", arg);
            } catch (IOException e) {
                throw new IllegalStateException("could not resize screenshot", e);
            }
        }
        return localTempFile;
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

    private static List<File> createDumpSysFiles(File tmpFolder, String dateTimeString, AdbDevice device, AdbLocationFinder.LocationResult adbLocation, CmdProvider cmdProvider, Arg arguments) throws IOException {

        List<File> files = new ArrayList<>();
        List<String> types;
        long start = System.currentTimeMillis();

        int size = 0;
        if (arguments.dumpsysServices != null) {
            types = Arrays.asList(arguments.dumpsysServices);
        } else {
            types = Arrays.asList("battery", "device_policy", "permission", "connectivity", "notification", "activity", "cpuinfo", "nfc", "-l");
        }

        for (String type : types) {
            String dumpsys = Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", type}, cmdProvider, adbLocation).toString();

            File file = new File(tmpFolder, "dumpsys-" + type + "-" + dateTimeString + "-" + device.model + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), dumpsys.getBytes("UTF-8"));
            size += file.length();
            files.add(file);
        }

        Commons.log(String.format(Locale.US, "\tcreate dumpsys files (%.2fkB) (%d ms)", (double) size / 1024.0, System.currentTimeMillis() - start), arguments);
        return files;
    }

    private static List<File> createPackageManagerDebugFiles(File tmpFolder, String dateTimeString, AdbDevice device, AdbLocationFinder.LocationResult adbLocation, CmdProvider cmdProvider, Arg arguments) throws IOException {

        List<String> pmCmds = Arrays.asList("libraries", "features", "users", "permission-groups", "packages");
        List<File> files = new ArrayList<>();
        long start = System.currentTimeMillis();

        int size = 0;
        for (String pmCmd : pmCmds) {
            CmdProvider.Result result = Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "pm", "list", pmCmd}, cmdProvider, adbLocation);
            File file = new File(tmpFolder, "pm_list_" + pmCmd + "-" + dateTimeString + "-" + device.model + ".txt");

            if (file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), result.toString().getBytes("UTF-8"));
            size += file.length();
            files.add(file);
        }
        Commons.log(String.format(Locale.US, "\tcreate pm files (%.2fkB) (%d ms)", (double) size / 1024.0, System.currentTimeMillis() - start), arguments);
        return files;
    }

    private static class BugReportDeviceFileAction {
        final String deviceTempFile;
        final String[] command;
        final String log;
        final String zipSubFolder;
        File localTempFile;

        BugReportDeviceFileAction(String log, String deviceTempFile, File localTempFile, String[] command, String zipSubFolder) {
            this.deviceTempFile = deviceTempFile;
            this.localTempFile = localTempFile;
            this.command = command;
            this.log = log;
            this.zipSubFolder = zipSubFolder;
        }
    }

}
