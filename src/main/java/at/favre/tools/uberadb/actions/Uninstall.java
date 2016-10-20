package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.parser.PackageMatcher;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.util.CmdUtil;

import java.util.List;
import java.util.Set;

public class Uninstall {

    public static void execute(AdbLocationFinder.LocationResult adbLocation, Arg arguments, List<CmdUtil.Result> executedCommands, boolean preview, Commons.ActionResult actionResult, AdbDevice device, List<String> allPackages) {
        Set<String> filteredPackages = new PackageMatcher(allPackages).findMatches(
                PackageMatcher.parseFiltersArg(arguments.mainArgument));

        for (String filteredPackage : filteredPackages) {
            String uninstallStatus = "\t" + filteredPackage;
            if (!arguments.dryRun) {
                if (!preview) {
                    if (arguments.mode == Arg.Mode.UNINSTALL) {
                        CmdUtil.Result uninstallCmdResult = Commons.runAdbCommand(createUninstallCmd(device, filteredPackage, arguments), adbLocation);
                        executedCommands.add(uninstallCmdResult);
                        uninstallStatus += "\t" + (uninstallCmdResult.out != null ? uninstallCmdResult.out.trim() : "");
                        if (InstalledPackagesParser.wasSuccessfulUninstalled(uninstallCmdResult.out)) {
                            actionResult.successCount++;
                        } else {
                            actionResult.failureCount++;
                        }
                    } else if (arguments.mode == Arg.Mode.BUGREPORT) {

                        uninstallStatus += "\treport created";
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
