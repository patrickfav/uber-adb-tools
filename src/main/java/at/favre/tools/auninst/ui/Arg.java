package at.favre.tools.auninst.ui;


public class Arg {
    public String filterString;

    public String adbPath;
    public String device;

    public boolean dryRun = false;
    public boolean skipEmulators = false;
    public boolean keepData = false;
    public boolean quiet = false;
    public boolean debug;

    public Arg() {
    }

    public Arg(String filterString, String adbPath, String device, boolean dryRun, boolean skipEmulators, boolean keepData,
               boolean quiet, boolean debug) {
        this.adbPath = adbPath;
        this.filterString = filterString;
        this.device = device;
        this.dryRun = dryRun;
        this.skipEmulators = skipEmulators;
        this.keepData = keepData;
        this.quiet = quiet;
        this.debug = debug;
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
        if (filterString != null ? !filterString.equals(arg.filterString) : arg.filterString != null) return false;
        if (adbPath != null ? !adbPath.equals(arg.adbPath) : arg.adbPath != null) return false;
        return device != null ? device.equals(arg.device) : arg.device == null;

    }

    @Override
    public int hashCode() {
        int result = filterString != null ? filterString.hashCode() : 0;
        result = 31 * result + (adbPath != null ? adbPath.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + (dryRun ? 1 : 0);
        result = 31 * result + (skipEmulators ? 1 : 0);
        result = 31 * result + (keepData ? 1 : 0);
        result = 31 * result + (quiet ? 1 : 0);
        result = 31 * result + (debug ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Arg{" +
                "filterString='" + filterString + '\'' +
                ", adbPath='" + adbPath + '\'' +
                ", device='" + device + '\'' +
                ", dryRun=" + dryRun +
                ", skipEmulators=" + skipEmulators +
                ", keepData=" + keepData +
                ", quiet=" + quiet +
                ", debug=" + debug +
                '}';
    }
}
