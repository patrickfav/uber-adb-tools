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

package at.favre.tools.uberadb.ui;


import java.util.Arrays;

public class Arg {
    public enum Mode {INSTALL, UNINSTALL, BUGREPORT, FORCE_STOP, CLEAR, INFO, START_ACTIVITY}

    public String[] mainArgument;

    public String adbPath;
    public String device;
    public String[] reportFilterIntent;
    public String[] dumpsysServices;
    public int delayStartActivitySec;

    public boolean dryRun = false;
    public boolean skipEmulators = false;
    public boolean keepData = false;
    public boolean quiet = false;
    public boolean debug = false;
    public boolean force = false;
    public boolean grantPermissions = false;
    public boolean simpleBugReport = false;
    public boolean waitForDevice = false;

    public Mode mode;

    public Arg() {
    }

    public Arg(String[] mainArgument, String adbPath, String device, String[] reportFilterIntent, String[] dumpsysServices, int delayStartActivitySec, boolean dryRun, boolean skipEmulators, boolean keepData, boolean quiet, boolean debug, boolean force, boolean grantPermissions, boolean simpleBugReport, boolean waitForDevice, Mode mode) {
        this.mainArgument = mainArgument;
        this.adbPath = adbPath;
        this.device = device;
        this.reportFilterIntent = reportFilterIntent;
        this.dumpsysServices = dumpsysServices;
        this.delayStartActivitySec = delayStartActivitySec;
        this.dryRun = dryRun;
        this.skipEmulators = skipEmulators;
        this.keepData = keepData;
        this.quiet = quiet;
        this.debug = debug;
        this.force = force;
        this.grantPermissions = grantPermissions;
        this.simpleBugReport = simpleBugReport;
        this.waitForDevice = waitForDevice;
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arg arg = (Arg) o;

        if (dryRun != arg.dryRun) return false;
        if (skipEmulators != arg.skipEmulators) return false;
        if (keepData != arg.keepData) return false;
        if (quiet != arg.quiet) return false;
        if (debug != arg.debug) return false;
        if (force != arg.force) return false;
        if (grantPermissions != arg.grantPermissions) return false;
        if (simpleBugReport != arg.simpleBugReport) return false;
        if (waitForDevice != arg.waitForDevice) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(mainArgument, arg.mainArgument)) return false;
        if (adbPath != null ? !adbPath.equals(arg.adbPath) : arg.adbPath != null) return false;
        if (device != null ? !device.equals(arg.device) : arg.device != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(reportFilterIntent, arg.reportFilterIntent)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(dumpsysServices, arg.dumpsysServices)) return false;
        return mode == arg.mode;

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mainArgument);
        result = 31 * result + (adbPath != null ? adbPath.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(reportFilterIntent);
        result = 31 * result + Arrays.hashCode(dumpsysServices);
        result = 31 * result + (dryRun ? 1 : 0);
        result = 31 * result + (skipEmulators ? 1 : 0);
        result = 31 * result + (keepData ? 1 : 0);
        result = 31 * result + (quiet ? 1 : 0);
        result = 31 * result + (debug ? 1 : 0);
        result = 31 * result + (force ? 1 : 0);
        result = 31 * result + (grantPermissions ? 1 : 0);
        result = 31 * result + (simpleBugReport ? 1 : 0);
        result = 31 * result + (waitForDevice ? 1 : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Arg{" +
                "mainArgument=" + Arrays.toString(mainArgument) +
                ", adbPath='" + adbPath + '\'' +
                ", device='" + device + '\'' +
                ", reportFilterIntent=" + Arrays.toString(reportFilterIntent) +
                ", dumpsysServices=" + Arrays.toString(dumpsysServices) +
                ", dryRun=" + dryRun +
                ", skipEmulators=" + skipEmulators +
                ", keepData=" + keepData +
                ", quiet=" + quiet +
                ", debug=" + debug +
                ", force=" + force +
                ", grantPermissions=" + grantPermissions +
                ", simpleBugReport=" + simpleBugReport +
                ", waitForDevice=" + waitForDevice +
                ", mode=" + mode +
                '}';
    }
}
