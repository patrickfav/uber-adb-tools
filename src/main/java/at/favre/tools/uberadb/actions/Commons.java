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
import at.favre.tools.uberadb.ui.Arg;
import at.favre.tools.uberadb.util.CmdUtil;

import java.util.List;

public final class Commons {
    private Commons() {
    }

    public static void logErr(String msg) {
        System.err.println(msg);
    }

    public static void logLoud(String msg) {
        System.out.println(msg);
    }

    public static void log(String msg, Arg arg) {
        if (!arg.quiet) {
            System.out.println(msg);
        }
    }

    public static CmdProvider.Result runAdbCommand(String[] adbArgs, CmdProvider cmdProvider, AdbLocationFinder.LocationResult locationResult) {
        return cmdProvider.runCmd(CmdUtil.concat(locationResult.args, adbArgs));
    }

    //CHECKSTYLE:OFF
    public static class ActionResult {
        public int deviceCount = 0;
        public int successCount = 0;
        public int failureCount = 0;
    }

    public static class IterationResult {
        public ActionResult result;
        public boolean proceed;

        public IterationResult(ActionResult result, boolean proceed) {
            this.result = result;
            this.proceed = proceed;
        }
    }
    //CHECKSTYLE:ON

    public static void checkSpecificDevice(List<AdbDevice> devices, Arg arguments) {
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

    public static String getCorrectAction(Arg.Mode mode, String install, String uninstall, String bugreport, String forceStop, String clear, String info, String start) {
        switch (mode) {
            case INSTALL:
                return install;
            case UNINSTALL:
                return uninstall;
            case BUGREPORT:
                return bugreport;
            case FORCE_STOP:
                return forceStop;
            case CLEAR:
                return clear;
            case INFO:
                return info;
            case START_ACTIVITY:
                return start;
            default:
                return "unknown";
        }
    }
}
