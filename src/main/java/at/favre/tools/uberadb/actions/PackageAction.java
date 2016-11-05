package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.DumpsysPackageParser;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;

import java.util.List;

public class PackageAction {

    public static void execute(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, boolean preview, Commons.ActionResult actionResult, AdbDevice device, List<String> allPackages) {
        List<String> filteredPackages = new PackageMatcher(allPackages).findMatches(arguments.mainArgument);

        for (String filteredPackage : filteredPackages) {
            DumpsysPackageParser.PackageInfo packageInfo = getPackageInfo(device, filteredPackage, cmdProvider, adbLocation);
            String packgeActionLog = "\t" + filteredPackage + getShortPackageInfo(packageInfo);
            if (!arguments.dryRun) {
                if (!preview) {
                    if (arguments.mode == Arg.Mode.UNINSTALL) {
                        CmdProvider.Result uninstallCmdResult = Commons.runAdbCommand(createUninstallCmd(device, filteredPackage, arguments), cmdProvider, adbLocation);
                        packgeActionLog += "\t" + (uninstallCmdResult.out != null ? uninstallCmdResult.out.trim() : "");
                        if (InstalledPackagesParser.wasSuccessfulUninstalled(uninstallCmdResult.out)) {
                            actionResult.successCount++;
                        } else {
                            actionResult.failureCount++;
                        }
                    } else if (arguments.mode == Arg.Mode.FORCE_STOP) {
                        Commons.runAdbCommand(new String[]{"shell", "am", "force-stop", filteredPackage}, cmdProvider, adbLocation);
                        packgeActionLog += "\tstopped";
                        actionResult.successCount++;
                    } else if (arguments.mode == Arg.Mode.CLEAR) {
                        Commons.runAdbCommand(new String[]{"shell", "pm", "clear", filteredPackage}, cmdProvider, adbLocation);
                        packgeActionLog += "\tdata cleared";
                        actionResult.successCount++;
                    } else if (arguments.mode == Arg.Mode.INFO) {
                        packgeActionLog += "\n" + getFullPackageInfo(packageInfo);
                        actionResult.successCount++;
                    }
                } else {
                    actionResult.successCount++;
                }
            } else {
                packgeActionLog += "\tskip";
            }
            Commons.log(packgeActionLog, arguments);
        }

        if (filteredPackages.isEmpty()) {
            Commons.log("\t No apps found for given filter", arguments);
        }
    }

    private static String getShortPackageInfo(DumpsysPackageParser.PackageInfo info) {
        if (info != null && info.versionName != null && !info.versionName.isEmpty()) {
            return " (" + (info.versionName.charAt(0) == 'v' ? "" : "v") + info.versionName + ")";
        }
        return "";
    }

    private static String getFullPackageInfo(DumpsysPackageParser.PackageInfo info) {
        StringBuilder sb = new StringBuilder();
        if (info != null) {
            sb.append("\t\tversionCode: ").append(info.versionCode).append("\n");
            sb.append("\t\tpath: ").append(info.codePath).append("\n");
            sb.append("\t\tinstallTime: ").append(info.firstInstallTime).append("\n");
        } else {
            sb.append("\t\tcould not read package info");
        }
        return sb.toString();
    }

    private static String[] createUninstallCmd(AdbDevice device, String filteredPackage, Arg arguments) {
        if (!arguments.keepData) {
            return new String[]{"-s", device.serial, "shell", "pm", "uninstall", filteredPackage};
        } else {
            return new String[]{"-s", device.serial, "shell", "cmd", "package", "uninstall", "-k", filteredPackage};
        }
    }

    private static DumpsysPackageParser.PackageInfo getPackageInfo(AdbDevice device, String filteredPackage, CmdProvider cmdProvider, AdbLocationFinder.LocationResult locationResult) {
        return new DumpsysPackageParser().parseSingleDumpsysPackage(filteredPackage, Commons.runAdbCommand(new String[]{"-s", device.serial, "shell", "dumpsys", "package", filteredPackage}, cmdProvider, locationResult).out);
    }
}
