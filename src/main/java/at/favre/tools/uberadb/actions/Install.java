package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.ui.Arg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Install {
    public static void execute(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, List<File> installFiles, boolean preview, Commons.ActionResult actionResult, AdbDevice device) {
        for (File installFile : installFiles) {
            String installStatus = "\t" + installFile.getName();

            if (!arguments.dryRun) {
                if (!preview) {
                    CmdProvider.Result installCmdResult = Commons.runAdbCommand(createInstallCmd(device,
                            installFile.getAbsolutePath(), arguments), cmdProvider, adbLocation);

                    if (InstalledPackagesParser.wasSuccessfulInstalled(installCmdResult.out)) {
                        installStatus += "\tSuccess";
                        actionResult.successCount++;
                    } else {
                        installStatus += "\tFail " + InstalledPackagesParser.parseShortenedInstallStatus(installCmdResult.out);
                        actionResult.failureCount++;
                    }
                } else {
                    actionResult.successCount++;
                }
            } else {
                installStatus += "\tskip";
            }
            Commons.log(installStatus, arguments);
        }
    }

    private static String[] createInstallCmd(AdbDevice device, String absolutPath, Arg arguments) {
        if (!arguments.keepData) {
            return new String[]{"-s", device.serial, "install", absolutPath};
        } else {
            return new String[]{"-s", device.serial, "install", "-r", absolutPath};
        }
    }

    public static List<File> getFilesToInstall(Arg arguments) {
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
}
