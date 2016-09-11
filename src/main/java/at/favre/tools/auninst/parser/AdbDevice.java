package at.favre.tools.auninst.parser;

public class AdbDevice {
    public enum Status {OK, OFFLINE, UNAUTHORIZED, UNKOWN}

    public final String name;
    public final Status status;
    public final boolean isEmulator;

    public AdbDevice(String name, Status status, boolean isEmulator) {
        this.name = name;
        this.status = status;
        this.isEmulator = isEmulator;
    }

    @Override
    public String toString() {
        return "AdbDevice{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", isEmulator=" + isEmulator +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdbDevice device = (AdbDevice) o;

        if (isEmulator != device.isEmulator) return false;
        if (name != null ? !name.equals(device.name) : device.name != null) return false;
        return status == device.status;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isEmulator ? 1 : 0);
        return result;
    }
}
