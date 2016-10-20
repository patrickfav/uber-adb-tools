package at.favre.tools.uberadb.ui;


import java.util.Arrays;

public class Arg {
    public enum Mode {INSTALL, UNINSTALL, BUGREPORT}

    public String mainArgument;

    public String adbPath;
    public String device;
    public String[] reportFilterIntent;

    public boolean dryRun = false;
    public boolean skipEmulators = false;
    public boolean keepData = false;
    public boolean quiet = false;
    public boolean debug = false;
    public boolean force = false;

    public Mode mode;

    public Arg() {
    }

    public Arg(String mainArgument, String adbPath, String device, String[] reportFilterIntent, boolean dryRun, boolean skipEmulators, boolean keepData, boolean quiet, boolean debug, boolean force, Mode mode) {
        this.mainArgument = mainArgument;
        this.adbPath = adbPath;
        this.device = device;
        this.reportFilterIntent = reportFilterIntent;
        this.dryRun = dryRun;
        this.skipEmulators = skipEmulators;
        this.keepData = keepData;
        this.quiet = quiet;
        this.debug = debug;
        this.force = force;
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
        if (mainArgument != null ? !mainArgument.equals(arg.mainArgument) : arg.mainArgument != null) return false;
        if (adbPath != null ? !adbPath.equals(arg.adbPath) : arg.adbPath != null) return false;
        if (device != null ? !device.equals(arg.device) : arg.device != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(reportFilterIntent, arg.reportFilterIntent)) return false;
        return mode == arg.mode;

    }

    @Override
    public int hashCode() {
        int result = mainArgument != null ? mainArgument.hashCode() : 0;
        result = 31 * result + (adbPath != null ? adbPath.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(reportFilterIntent);
        result = 31 * result + (dryRun ? 1 : 0);
        result = 31 * result + (skipEmulators ? 1 : 0);
        result = 31 * result + (keepData ? 1 : 0);
        result = 31 * result + (quiet ? 1 : 0);
        result = 31 * result + (debug ? 1 : 0);
        result = 31 * result + (force ? 1 : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Arg{" +
                "mainArgument='" + mainArgument + '\'' +
                ", adbPath='" + adbPath + '\'' +
                ", device='" + device + '\'' +
                ", reportFilterIntent='" + Arrays.toString(reportFilterIntent) + '\'' +
                ", dryRun=" + dryRun +
                ", skipEmulators=" + skipEmulators +
                ", keepData=" + keepData +
                ", quiet=" + quiet +
                ", debug=" + debug +
                ", force=" + force +
                ", mode=" + mode +
                '}';
    }
}
