package at.favre.tools.uberadb;

import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.AdbDevicesParser;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.ui.CLIParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdbTool {

    public static void main(String[] args) {
        Arg arguments = CLIParser.parse(args);

        if (arguments != null) {
            execute(arguments);
        }
    }

    private static void execute(Arg arguments) {
        List<CmdUtil.Result> executedCommands = new ArrayList<>();

        try {
            AdbLocationFinder.LocationResult adbLocation = new AdbLocationFinder().find(arguments.adbPath);

            executedCommands.add(runAdbCommand(new String[]{"start-server"}, adbLocation));

            CmdUtil.Result devicesCmdResult = runAdbCommand(new String[]{"devices", "-l"}, adbLocation);
            executedCommands.add(devicesCmdResult);
            List<AdbDevice> devices = new AdbDevicesParser().parse(devicesCmdResult.out);

            List<File> installFiles = new ArrayList<>();

            if (!devices.isEmpty()) {
                checkSpecificDevice(devices, arguments);

                String statusLog = "Found " + devices.size() + " device(s).";

                if (arguments.mode == Arg.Mode.INSTALL) {
                    statusLog += " Installing '" + arguments.mainArgument + "'.";
                    installFiles = getFilesToInstall(arguments);
                } else if (arguments.mode == Arg.Mode.UNINSTALL) {
                    statusLog += " Uninstalling with filter '" + arguments.mainArgument + "'.";
                    if (arguments.keepData) {
                        statusLog += " Keep data/caches.";
                    }
                } else if (arguments.mode == Arg.Mode.BUGREPORT && arguments.mainArgument != null && !arguments.mainArgument.isEmpty()) {
                    statusLog += " Creating bugreport and save to '" + arguments.mainArgument + "'.";
                }

                if (arguments.force) {
                    statusLog += " Skips user prompt.";
                }

                if (adbLocation.location == AdbLocationFinder.Location.WIN_DEFAULT ||
                        adbLocation.location == AdbLocationFinder.Location.MAC_DEFAULT ||
                        adbLocation.location == AdbLocationFinder.Location.LINUX_DEFAULT) {
                    statusLog += " Adb not found in PATH, use default location: " + adbLocation.arg() + ".";
                }

                statusLog += "\n";

                logLoud(statusLog);
            }

            if (arguments.dryRun || arguments.force || arguments.mode == Arg.Mode.BUGREPORT || iterateDevices(devices, adbLocation, arguments, executedCommands, installFiles, true)) {
                iterateDevices(devices, adbLocation, arguments, executedCommands, installFiles, false);
            }

            if (arguments.debug) {
                logLoud(getCommandHistory(executedCommands));
            }
        } catch (Exception e) {
            logErr(e.getMessage());

            if (arguments.debug) {
                e.printStackTrace();
                logErr(getCommandHistory(executedCommands));
            } else {
                logErr("Run with '-debug' parameter to get additional information.");
            }
            System.exit(1);
        }
    }

    private static boolean iterateDevices(List<AdbDevice> devices, AdbLocationFinder.LocationResult adbLocation, Arg arguments,
                                          List<CmdUtil.Result> executedCommands, List<File> installFiles, boolean preview) throws Exception {
        int deviceCount = 0;
        int successCount = 0;
        int failureCount = 0;
        long startDuration = System.currentTimeMillis();

        for (AdbDevice device : devices) {
            if (arguments.device == null || arguments.device.equals(device.serial)) {
                CmdUtil.Result packagesCmdResult = runAdbCommand(new String[]{"-s", device.serial, "shell", "pm list packages -f"}, adbLocation);
                executedCommands.add(packagesCmdResult);

                String modelName = "Device";
                if (device.model != null) {
                    modelName = device.model;
                }

                String deviceLog = modelName + " [" + device.serial + "]";

                if (device.status != AdbDevice.Status.OK) {
                    deviceLog += ": " + device.status;
                }

                if (arguments.skipEmulators && device.isEmulator) {
                    deviceLog += " (skip)";
                }

                log(deviceLog, arguments);

                if (device.status == AdbDevice.Status.OK && (!arguments.skipEmulators || !device.isEmulator)) {
                    deviceCount++;
                    List<String> allPackages = new InstalledPackagesParser().parse(packagesCmdResult.out);

                    if (arguments.mode == Arg.Mode.BUGREPORT) {
                        logLoud("create bug report:");

                        String dateTimeString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());

                        File outFolder;
                        if (arguments.mainArgument != null && !arguments.mainArgument.isEmpty()) {
                            outFolder = new File(arguments.mainArgument);
                            if (!outFolder.exists() && !outFolder.mkdirs()) {
                                throw new IllegalStateException("could not create directory " + arguments.mainArgument);
                            }
                        } else {
                            outFolder = new File(AdbTool.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                        }

                        String tempFileScreenshot = "/sdcard/bugreport_tempfile_screenshot.png";
                        String tempFileLogcat = "/sdcard/bugreport_tempfile_logcat";
                        File localTempFileScreenshot = new File(outFolder.getAbsolutePath(), "screen-" + device.model + "-" + dateTimeString + ".png");
                        File localTempFileLogcat = new File(outFolder.getAbsolutePath(), "logcat-" + device.model + "-" + dateTimeString + ".txt");
                        File zipFile = new File(outFolder, "bugreport-" + device.model + "-" + dateTimeString + ".zip");

                        log("\twake up screen and take screenshot", arguments);
                        CmdUtil.Result wakeupScreenCmd = runAdbCommand(new String[]{"-s", device.serial, "shell", "input", "keyevent", "KEYCODE_WAKEUP"}, adbLocation);
                        CmdUtil.Result screecapCmd = runAdbCommand(new String[]{"-s", device.serial, "shell", "screencap", tempFileScreenshot}, adbLocation);
                        CmdUtil.Result pullscreenCmd = runAdbCommand(new String[]{"-s", device.serial, "pull", tempFileScreenshot, localTempFileScreenshot.getAbsolutePath()}, adbLocation);
                        log("\tcreate logcat file and pull from device", arguments);
                        CmdUtil.Result logcat = runAdbCommand(new String[]{"-s", device.serial, "logcat", "-d", "-f", tempFileLogcat}, adbLocation);
                        CmdUtil.Result pullLogcatCmd = runAdbCommand(new String[]{"-s", device.serial, "pull", tempFileLogcat, localTempFileLogcat.getAbsolutePath()}, adbLocation);
                        log(String.format(Locale.US, "\t%.2fkB screenshot, %.2fkB logcat",
                                (double) localTempFileScreenshot.length() / 1024.0, (double) localTempFileLogcat.length() / 1024.0), arguments);
                        CmdUtil.Result removeTempFiles1Cmd = runAdbCommand(new String[]{"-s", device.serial, "shell", "rm", "-f", tempFileScreenshot}, adbLocation);
                        CmdUtil.Result removeTempFiles2Cmd = runAdbCommand(new String[]{"-s", device.serial, "shell", "rm", "-f", tempFileLogcat}, adbLocation);

                        executedCommands.add(wakeupScreenCmd);
                        executedCommands.add(screecapCmd);
                        executedCommands.add(pullscreenCmd);
                        executedCommands.add(logcat);
                        executedCommands.add(pullLogcatCmd);
                        executedCommands.add(removeTempFiles1Cmd);
                        executedCommands.add(removeTempFiles2Cmd);

                        MiscUtil.zip(zipFile, Arrays.asList(localTempFileScreenshot, localTempFileLogcat));
                        localTempFileScreenshot.delete();
                        localTempFileLogcat.delete();

                        if (!zipFile.exists()) {
                            throw new IllegalStateException("could not create zip file " + zipFile);
                        }
                        log(String.format(Locale.US, "\ttemp files removed and zip %s (%.2fkB) created", zipFile.getAbsolutePath(), (double) zipFile.length() / 1024.0), arguments);

                    } else if (arguments.mode == Arg.Mode.INSTALL) {
                        for (File installFile : installFiles) {
                            String installStatus = "\t" + installFile.getName();

                            if (!arguments.dryRun) {
                                if (!preview) {
                                    CmdUtil.Result installCmdResult = runAdbCommand(createInstallCmd(device,
                                            installFile.getAbsolutePath(), arguments), adbLocation);
                                    executedCommands.add(installCmdResult);


                                    if (InstalledPackagesParser.wasSuccessfulInstalled(installCmdResult.out)) {
                                        installStatus += "\tSuccess";
                                        successCount++;
                                    } else {
                                        installStatus += "\t" + InstalledPackagesParser.parseShortenedInstallStatus(installCmdResult.out);
                                        failureCount++;
                                    }
                                } else {
                                    successCount++;
                                }
                            } else {
                                installStatus += "\tskip";
                            }
                            log(installStatus, arguments);
                        }
                    } else if (arguments.mode == Arg.Mode.UNINSTALL) {
                        Set<String> filteredPackages = new PackageMatcher(allPackages).findMatches(
                                PackageMatcher.parseFiltersArg(arguments.mainArgument));

                        for (String filteredPackage : filteredPackages) {
                            String uninstallStatus = "\t" + filteredPackage;
                            if (!arguments.dryRun) {
                                if (!preview) {
                                    if (arguments.mode == Arg.Mode.UNINSTALL) {
                                        CmdUtil.Result uninstallCmdResult = runAdbCommand(createUninstallCmd(device, filteredPackage, arguments), adbLocation);
                                        executedCommands.add(uninstallCmdResult);
                                        uninstallStatus += "\t" + (uninstallCmdResult.out != null ? uninstallCmdResult.out.trim() : "");
                                        if (InstalledPackagesParser.wasSuccessfulUninstalled(uninstallCmdResult.out)) {
                                            successCount++;
                                        } else {
                                            failureCount++;
                                        }
                                    } else if (arguments.mode == Arg.Mode.BUGREPORT) {

                                        uninstallStatus += "\treport created";
                                        successCount++;
                                    }
                                } else {
                                    successCount++;
                                }
                            } else {
                                uninstallStatus += "\tskip";
                            }
                            log(uninstallStatus, arguments);
                        }

                        if (filteredPackages.isEmpty()) {
                            log("\t No apps found for given filter", arguments);
                        }
                    }
                }
                log("", arguments);
            }
        }

        if (preview) {
            if (successCount == 0) {
                logLoud("No apps " + getCorrectAction(arguments.mode, "installed.", "uninstalled.", "found for bug report."));
                return false;
            } else {
                logLoud(successCount + " apps would be " + getCorrectAction(arguments.mode, "installed", "uninstalled", "used for creating bug reports") + " on " + deviceCount + " device(s). Use '-force' to omit this prompt. Continue? [y/n]");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                    String input = br.readLine();
                    return input.trim().toLowerCase().equals("y");
                } catch (IOException e) {
                    throw new IllegalStateException("could not read form console", e);
                }
            }
        } else {
            if (deviceCount == 0) {
                logLoud("No ready devices found.");
                if (hasUnauthorizedDevices(devices)) {
                    logLoud("Check if you authorized your computer on your Android device. See http://stackoverflow.com/questions/23081263");
                }
            } else {
                logLoud(generateReport(arguments.mode, deviceCount, successCount, failureCount, System.currentTimeMillis() - startDuration));
            }
        }

        return true;
    }


    private static List<File> getFilesToInstall(Arg arguments) {
        List<File> installFiles = new ArrayList<>();

        File apkFileRef = new File(arguments.mainArgument);
        if (!apkFileRef.exists()) {
            throw new IllegalArgumentException("could not find " + arguments.mainArgument + " for install");
        }
        if (apkFileRef.isFile()) {
            if (apkFileRef.getName().toLowerCase().endsWith(".apk")) {
                installFiles.add(apkFileRef);
            }
        } else if (apkFileRef.isDirectory()) {
            for (File apkFile : apkFileRef.listFiles()) {
                if (apkFile.getName().toLowerCase().endsWith(".apk")) {
                    installFiles.add(apkFile);
                }
            }
        }

        if (installFiles.isEmpty()) {
            throw new IllegalArgumentException("could not find any apk files in " + arguments.mainArgument + " to install");
        }
        return installFiles;
    }

    private static String[] createUninstallCmd(AdbDevice device, String filteredPackage, Arg arguments) {
        if (!arguments.keepData) {
            return new String[]{"-s", device.serial, "shell", "pm", "uninstall", filteredPackage};
        } else {
            return new String[]{"-s", device.serial, "shell", "cmd", "package", "uninstall", "-k", filteredPackage};
        }
    }

    private static String[] createInstallCmd(AdbDevice device, String absolutPath, Arg arguments) {
        if (!arguments.keepData) {
            return new String[]{"-s", device.serial, "install", absolutPath};
        } else {
            return new String[]{"-s", device.serial, "install", "-r", absolutPath};
        }
    }

    private static boolean hasUnauthorizedDevices(List<AdbDevice> devices) {
        for (AdbDevice device : devices) {
            if (device.status == AdbDevice.Status.UNAUTHORIZED) {
                return true;
            }
        }
        return false;
    }

    private static String getCommandHistory(List<CmdUtil.Result> executedCommands) {
        StringBuilder sb = new StringBuilder("\nCmd history for debugging purpose:\n-----------------------\n");
        for (CmdUtil.Result executedCommand : executedCommands) {
            sb.append(executedCommand.toString());
        }
        return sb.toString();
    }


    private static String generateReport(Arg.Mode mode, int deviceCount, int successUninstallCount, int failureUninstallCount, long executionDurationMs) {
        String report;
        if (mode == Arg.Mode.BUGREPORT) {
            report = String.format(Locale.US, "Bug reports generated from %d device(s).", deviceCount);
        } else {
            report = String.format(Locale.US, "%d apps were " + getCorrectAction(mode, "installed", "uninstalled", "used for creating bug reports") + " on %d device(s).", successUninstallCount, deviceCount);
            if (failureUninstallCount > 0) {
                report += String.format(Locale.US, " %d apps could not be " + getCorrectAction(mode, "installed", "uninstalled", "used for creating bug reports") + " due to errors.", failureUninstallCount);
            }
        }
        report += " Took " + String.format(Locale.US, "%.2f", (double) executionDurationMs / 1000.0) + " seconds.";
        return report;
    }

    private static void checkSpecificDevice(List<AdbDevice> devices, Arg arguments) {
        if (arguments.device != null) {
            boolean found = false;
            for (AdbDevice device : devices) {
                if (device.serial.equals(arguments.device) && device.status == AdbDevice.Status.OK) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("There is no ready device attached with id '" + arguments.device + "'. Found devices: " + devices);
            }
        }
    }

    private static void logErr(String msg) {
        System.err.println(msg);
    }

    private static void logLoud(String msg) {
        System.out.println(msg);
    }

    private static void log(String msg, Arg arg) {
        if (!arg.quiet) {
            System.out.println(msg);
        }
    }

    private static CmdUtil.Result runAdbCommand(String[] adbArgs, AdbLocationFinder.LocationResult locationResult) {
        return CmdUtil.runCmd(CmdUtil.concat(locationResult.args, adbArgs));
    }


    private static String getCorrectAction(Arg.Mode mode, String install, String uninstall, String bugreport) {
        switch (mode) {
            case INSTALL:
                return install;
            case UNINSTALL:
                return uninstall;
            case BUGREPORT:
                return bugreport;
            default:
                return "unknown";
        }
    }
}
