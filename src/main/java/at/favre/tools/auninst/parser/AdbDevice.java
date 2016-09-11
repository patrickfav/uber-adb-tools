package at.favre.tools.auninst.parser;

public class AdbDevice {
    public enum Status {OK, OFFLINE, UNAUTHORIZED, UNKOWN}

    public final String name;
    public final Status status;
    public final String model;
    public final String product;
    public final boolean isEmulator;


    public AdbDevice(String name, Status status, String model, String product, boolean isEmulator) {
        this.name = name;
        this.status = status;
        this.model = model;
        this.product = product;
        this.isEmulator = isEmulator;
    }

    @Override
    public String toString() {
        return "AdbDevice{" +
                "name='" + name + '\'' +
                ", status=" + status +
                ", model='" + model + '\'' +
                ", product='" + product + '\'' +
                ", isEmulator=" + isEmulator +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdbDevice adbDevice = (AdbDevice) o;

        if (isEmulator != adbDevice.isEmulator) return false;
        if (name != null ? !name.equals(adbDevice.name) : adbDevice.name != null) return false;
        if (status != adbDevice.status) return false;
        if (model != null ? !model.equals(adbDevice.model) : adbDevice.model != null) return false;
        return product != null ? product.equals(adbDevice.product) : adbDevice.product == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (isEmulator ? 1 : 0);
        return result;
    }
}
