package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;

import java.util.List;

public class PackageAction {

    public static void execute(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, boolean preview, Commons.ActionResult actionResult, AdbDevice device, List<String> allPackages) {
        List<String> filteredPackages = new PackageMatcher(allPackages).findMatches(arguments.mainArgument);

        for (String filteredPackage : filteredPackages) {
            String uninstallStatus = "\t" + filteredPackage;
            if (!arguments.dryRun) {
                if (!preview) {
                    if (arguments.mode == Arg.Mode.UNINSTALL) {
                        CmdProvider.Result uninstallCmdResult = Commons.runAdbCommand(createUninstallCmd(device, filteredPackage, arguments), cmdProvider, adbLocation);
                        uninstallStatus += "\t" + (uninstallCmdResult.out != null ? uninstallCmdResult.out.trim() : "");
                        if (InstalledPackagesParser.wasSuccessfulUninstalled(uninstallCmdResult.out)) {
                            actionResult.successCount++;
                        } else {
                            actionResult.failureCount++;
                        }
                    } else if (arguments.mode == Arg.Mode.FORCE_STOP) {
                        Commons.runAdbCommand(new String[]{"shell", "am", "force-stop", filteredPackage}, cmdProvider, adbLocation);
                        uninstallStatus += "\tstopped";
                        actionResult.successCount++;
                    } else if (arguments.mode == Arg.Mode.CLEAR) {
                        Commons.runAdbCommand(new String[]{"shell", "pm", "clear", filteredPackage}, cmdProvider, adbLocation);
                        uninstallStatus += "\tdata cleared";
                        actionResult.successCount++;
                    }
                } else {
                    actionResult.successCount++;
                }
            } else {
                uninstallStatus += "\tskip";
            }
            Commons.log(uninstallStatus, arguments);
        }

        if (filteredPackages.isEmpty()) {
            Commons.log("\t No apps found for given filter", arguments);
        }
    }

    private static String[] createUninstallCmd(AdbDevice device, String filteredPackage, Arg arguments) {
        if (!arguments.keepData) {
            return new String[]{"-s", device.serial, "shell", "pm", "uninstall", filteredPackage};
        } else {
            return new String[]{"-s", device.serial, "shell", "cmd", "package", "uninstall", "-k", filteredPackage};
        }
    }
}
