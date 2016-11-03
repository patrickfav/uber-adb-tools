package at.favre.tools.uberadb;

import at.favre.tools.uberadb.actions.BugReport;
import at.favre.tools.uberadb.actions.Commons;
import at.favre.tools.uberadb.actions.Install;
import at.favre.tools.uberadb.actions.PackageAction;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.AdbDevicesParser;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.ui.CLIParser;
import at.favre.tools.uberadb.util.CmdUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class AdbTool {

    public static void main(String[] args) {
        Arg arguments = CLIParser.parse(args);

        if (arguments != null) {
            Commons.ActionResult result = execute(arguments, new CmdProvider.DefaultCmdProvider());

            if (result == null) {
                System.exit(1);
            } else if (result.failureCount > 0) {
                System.exit(2);
            }
        }
    }

    static Commons.ActionResult execute(Arg arguments, CmdProvider cmdProvider) {
        Commons.ActionResult result = null;

        try {
            AdbLocationFinder.LocationResult adbLocation = new AdbLocationFinder().find(cmdProvider, arguments.adbPath);

            Commons.runAdbCommand(new String[]{"start-server"}, cmdProvider, adbLocation);

            CmdProvider.Result devicesCmdResult = Commons.runAdbCommand(new String[]{"devices", "-l"}, cmdProvider, adbLocation);
            List<AdbDevice> devices = new AdbDevicesParser().parse(devicesCmdResult.out);

            List<File> installFiles = new ArrayList<>();

            if (!devices.isEmpty()) {
                Commons.checkSpecificDevice(devices, arguments);

                String statusLog = "Found " + devices.size() + " device(s).";

                if (arguments.mode == Arg.Mode.INSTALL) {
                    statusLog += " Installing '" + CmdUtil.concat(arguments.mainArgument, ", ") + "'.";
                    installFiles = Install.getFilesToInstall(arguments);
                    if (arguments.grantPermissions) {
                        statusLog += " Grant permissions.";
                    }
                    if (arguments.keepData) {
                        statusLog += " Reinstall.";
                    }
                } else if (arguments.mode == Arg.Mode.UNINSTALL) {
                    statusLog += " Uninstalling with filter '" + CmdUtil.concat(arguments.mainArgument, ", ") + "'.";
                    if (arguments.keepData) {
                        statusLog += " Keep data/caches.";
                    }
                } else if (arguments.mode == Arg.Mode.FORCE_STOP) {
                    statusLog += " Force stopping packages '" + CmdUtil.concat(arguments.mainArgument, ", ") + "'.";
                } else if (arguments.mode == Arg.Mode.CLEAR) {
                    statusLog += " Clear data for packages '" + CmdUtil.concat(arguments.mainArgument, ", ") + "'.";
                } else if (arguments.mode == Arg.Mode.BUGREPORT && arguments.mainArgument != null && arguments.mainArgument.length == 1) {
                    statusLog += " Creating bugreport and save to '" + arguments.mainArgument[0] + "'.";
                    if (arguments.reportFilterIntent != null) {
                        statusLog += " Use activity manager command " + Arrays.toString(arguments.reportFilterIntent);
                    }
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

                Commons.logLoud(statusLog);
            }

            if (iterateDevices(devices, adbLocation, arguments, cmdProvider, installFiles, true).proceed) {
                result = iterateDevices(devices, adbLocation, arguments, cmdProvider, installFiles, false).result;
            }

            if (arguments.debug) {
                Commons.logLoud(getCommandHistory(cmdProvider));
            }
        } catch (Exception e) {
            Commons.logErr(e.getClass().getName() + ": " + e.getMessage());

            if (arguments.debug) {
                Commons.logErr(getCommandHistory(cmdProvider));
                e.printStackTrace();
            } else {
                Commons.logErr("Run with '-debug' parameter to get additional information.");
            }
            return null;
        }
        return result;
    }


    private static Commons.IterationResult iterateDevices(List<AdbDevice> devices, AdbLocationFinder.LocationResult adbLocation, Arg arguments,
                                                          CmdProvider cmdProvider, List<File> installFiles, boolean preview) throws Exception {
        Commons.ActionResult actionResult = new Commons.ActionResult();

        if (preview && (arguments.dryRun || arguments.force || arguments.mode == Arg.Mode.BUGREPORT || arguments.mode == Arg.Mode.FORCE_STOP)) {
            return new Commons.IterationResult(actionResult, true);
        }

        long startDuration = System.currentTimeMillis();

        for (AdbDevice device : devices) {
            if (arguments.device == null || arguments.device.equals(device.serial)) {
                CmdProvider.Result packagesCmdResult = Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "pm list packages -f"}, cmdProvider, adbLocation);

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

                Commons.log(deviceLog, arguments);

                if (device.status == AdbDevice.Status.OK && (!arguments.skipEmulators || !device.isEmulator)) {
                    actionResult.deviceCount++;
                    List<String> allPackages = new InstalledPackagesParser().parse(packagesCmdResult.out);

                    if (arguments.mode == Arg.Mode.BUGREPORT) {
                        BugReport.create(adbLocation, arguments, cmdProvider, device, allPackages);
                    } else if (arguments.mode == Arg.Mode.INSTALL) {
                        Install.execute(adbLocation, arguments, cmdProvider, installFiles, preview, actionResult, device);
                    } else if (arguments.mode == Arg.Mode.UNINSTALL || arguments.mode == Arg.Mode.FORCE_STOP || arguments.mode == Arg.Mode.CLEAR) {
                        PackageAction.execute(adbLocation, arguments, cmdProvider, preview, actionResult, device, allPackages);
                    }
                }
                Commons.log("", arguments);
            }
        }

        if (actionResult.deviceCount == 0) {
            Commons.logLoud("No ready devices found.");

            if (hasUnauthorizedDevices(devices)) {
                Commons.logLoud("Check if you authorized your computer on your Android device. See http://stackoverflow.com/questions/23081263");
            }
        }

        if (preview) {
            if (actionResult.successCount == 0) {
                Commons.logLoud("No apps " + Commons.getCorrectAction(arguments.mode, "installed.", "uninstalled.", "found for bug report.", " stopped.", "cleared."));
                return new Commons.IterationResult(actionResult, false);
            } else {
                return new Commons.IterationResult(actionResult, promptUser(actionResult, arguments));
            }
        } else {
            if (actionResult.deviceCount > 0) {
                Commons.logLoud(generateReport(arguments.mode, actionResult.deviceCount, actionResult.successCount, actionResult.failureCount, System.currentTimeMillis() - startDuration));
            }
        }

        return new Commons.IterationResult(actionResult, true);
    }

    private static boolean promptUser(Commons.ActionResult actionResult, Arg arguments) {
        Commons.logLoud(actionResult.successCount + " apps would be " + Commons.getCorrectAction(arguments.mode, "installed", "uninstalled", "used for creating bug reports", "stopped", "cleared")
                + " on " + actionResult.deviceCount + " device(s). Use '-force' to omit this prompt. Continue? [y/n]");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String rawInput = br.readLine();
            if (rawInput == null) {
                return false;
            }
            String input = rawInput.trim().toLowerCase();
            boolean cont = input.equals("y") || input.equals("yes");

            if (!cont) {
                Commons.log("cancel", arguments);
            }
            return cont;
        } catch (IOException e) {
            throw new IllegalStateException("could not read form console", e);
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

    private static String getCommandHistory(CmdProvider cmdProvider) {
        StringBuilder sb = new StringBuilder("\nCmd history for debugging purpose:\n-----------------------\n");
        for (CmdProvider.Result executedCommand : cmdProvider.getHistory()) {
            sb.append(executedCommand.toString());
        }
        return sb.toString();
    }

    private static String generateReport(Arg.Mode mode, int deviceCount, int successUninstallCount, int failureUninstallCount, long executionDurationMs) {
        String report = "[" + new Date().toString() + "][v" + CmdUtil.jarVersion() + "]\n";
        if (mode == Arg.Mode.BUGREPORT) {
            report += String.format(Locale.US, "Bug reports generated from %d device(s).", deviceCount);
        } else {
            report += String.format(Locale.US, "%d apps were " + Commons.getCorrectAction(mode, "installed", "uninstalled", "used for creating bug reports", "stopped", "cleared") + " on %d device(s).", successUninstallCount, deviceCount);
            if (failureUninstallCount > 0) {
                report += String.format(Locale.US, " %d apps could not be " + Commons.getCorrectAction(mode, "installed", "uninstalled", "used for creating bug reports", "stopped", "cleared") + " due to errors.", failureUninstallCount);
            }
        }
        report += " Took " + String.format(Locale.US, "%.2f", (double) executionDurationMs / 1000.0) + " seconds.";
        return report;
    }
}
