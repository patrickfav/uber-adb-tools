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

package at.favre.tools.uberadb;

import at.favre.tools.uberadb.util.CmdUtil;

import java.io.File;

public class AdbLocationFinderImpl implements AdbLocationFinder {

    private static final String WIN_DEFAULT_SDK = "\\AppData\\Local\\Android\\sdk\\platform-tools\\adb.exe";
    private static final String WIN_DEFAULT_ANDROID_HOME = "\\platform-tools\\adb.exe";

    private static final String[] MAC_DEFAULT = new String[]{"/usr/local/opt/android-sdk/platform-tools/adb"};
    private static final String MAC_DEFAULT_HOME = "/Library/Android/sdk/platform-tools/adb";
    private static final String MAC_DEFAULT_ANDROID_HOME = "/platform-tools/adb";

    private static final String[] LINUX_DEFAULT = MAC_DEFAULT;
    private static final String LINUX_DEFAULT_ANDROID_HOME = MAC_DEFAULT_ANDROID_HOME;
    private static final String LINUX_DEFAULT_2 = "/Android/Sdk/platform-tools/adb";

    @Override
    public LocationResult find(CmdProvider cmdProvider, String customPath) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (customPath != null && new File(customPath).exists() && cmdProvider.canRunCmd(new String[]{customPath})) {
            return new LocationResult(Location.CUSTOM, new String[]{customPath});
        }

        File pathAdbExe = CmdUtil.checkAndGetFromPATHEnvVar(cmdProvider, "adb");

        if (pathAdbExe != null) {
            return new LocationResult(Location.PATH, new String[]{pathAdbExe.getAbsolutePath()});
        }

        String androidHome = System.getenv().get("ANDROID_HOME");
        String userPath = System.getProperty("user.home");

        if (osName.contains("win")) {
            userPath = System.getenv().get("USERPROFILE");

            if (userPath != null && cmdProvider.canRunCmd(new String[]{userPath + WIN_DEFAULT_SDK})) {
                return new LocationResult(Location.WIN_DEFAULT, new String[]{userPath + WIN_DEFAULT_SDK});
            }
            if (androidHome != null && cmdProvider.canRunCmd(new String[]{androidHome + WIN_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{userPath + WIN_DEFAULT_ANDROID_HOME});
            }
        } else if (osName.contains("mac")) {
            if (cmdProvider.canRunCmd(MAC_DEFAULT)) {
                return new LocationResult(Location.MAC_DEFAULT, MAC_DEFAULT);
            }
            if (userPath != null && cmdProvider.canRunCmd(new String[]{userPath + MAC_DEFAULT_HOME})) {
                return new LocationResult(Location.MAC_DEFAULT, new String[]{userPath + MAC_DEFAULT_HOME});
            }
            if (androidHome != null && cmdProvider.canRunCmd(new String[]{androidHome + MAC_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{androidHome + MAC_DEFAULT_ANDROID_HOME});
            }
        } else if (osName.contains("nix")) {
            if (cmdProvider.canRunCmd(LINUX_DEFAULT)) {
                return new LocationResult(Location.LINUX_DEFAULT, LINUX_DEFAULT);
            }
            if (userPath != null && cmdProvider.canRunCmd(new String[]{userPath + LINUX_DEFAULT_2})) {
                return new LocationResult(Location.LINUX_DEFAULT, new String[]{userPath + LINUX_DEFAULT_2});
            }
            if (androidHome != null && cmdProvider.canRunCmd(new String[]{androidHome + LINUX_DEFAULT_ANDROID_HOME})) {
                return new LocationResult(Location.ANDROID_HOME, new String[]{androidHome + LINUX_DEFAULT_ANDROID_HOME});
            }
        }

        throw new IllegalStateException("Could not find adb. Not found in PATH or the usual default locations. Did you install " +
                "the Android SDK and set adb to PATH? As alternative you could use the '-adbPath' argument. See: https://stackoverflow.com/questions/20564514");
    }

}
