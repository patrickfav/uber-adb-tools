/*
 *
 *  *  Copyright 2016 Patrick Favre-Bulle
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package at.favre.tools.uberadb.actions;

import at.favre.tools.uberadb.AdbLocationFinder;
import at.favre.tools.uberadb.CmdProvider;
import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.parser.InstalledPackagesParser;
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.ui.FileArgParser;
import at.favre.tools.uberadb.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Install {
    private static final String EXTENSION_APK = "apk";

    public static void execute(AdbLocationFinder.LocationResult adbLocation, Arg arguments, CmdProvider cmdProvider, boolean preview, Commons.ActionResult actionResult, AdbDevice device) {
        List<File> installFiles = new FileArgParser().parseAndSortUniqueFilesNonRecursive(arguments.mainArgument, EXTENSION_APK);

        if (installFiles.isEmpty()) {
            throw new IllegalStateException("could not find any apk files in " + Arrays.toString(arguments.mainArgument) + " to install");
        }

        for (File installFile : installFiles) {
            String installStatus = "\t" + installFile.getName() + "\n\t\tchecksum: " + FileUtil.createChecksum(installFile, "SHA-256") + " (sha256)\n";

            if (!arguments.dryRun) {
                if (!preview) {
                    CmdProvider.Result installCmdResult = Commons.runAdbCommand(createInstallCmd(device,
                            installFile.getAbsolutePath(), arguments), cmdProvider, adbLocation);

                    if (InstalledPackagesParser.wasSuccessfulInstalled(installCmdResult.out)) {
                        installStatus += "\t\tSuccess";
                        actionResult.successCount++;
                    } else {
                        installStatus += "\t\tFail " + InstalledPackagesParser.parseShortenedInstallStatus(installCmdResult.out);
                        actionResult.failureCount++;
                    }
                } else {
                    actionResult.successCount++;
                }
            } else {
                installStatus += "\t\tskip";
            }
            Commons.log(installStatus, arguments);
        }
    }

    public static boolean isSingleFile(Arg arguments) {
        List<File> installFiles = new FileArgParser().parseAndSortUniqueFilesNonRecursive(arguments.mainArgument, EXTENSION_APK);
        return installFiles.size() == 1 && installFiles.get(0).isFile();
    }

    private static String[] createInstallCmd(AdbDevice device, String absolutePath, Arg arguments) {
        List<String> cmdList = new ArrayList<>();
        cmdList.add("-s");
        cmdList.add(device.serial);
        cmdList.add("install");

        if (arguments.keepData) {
            cmdList.add("-r");
        }
        if (arguments.grantPermissions) {
            cmdList.add("-g");
        }
        cmdList.add(absolutePath);

        return cmdList.toArray(new String[cmdList.size()]);
    }
}
