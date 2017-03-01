package at.favre.tools.uberadb;

import at.favre.tools.uberadb.parser.AdbDevice;
import at.favre.tools.uberadb.util.CmdUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockAdbCmdProvider implements CmdProvider {
    private List<AdbDevice> devices = Collections.emptyList();
    private List<String> installedPackages = Collections.emptyList();
    private boolean returnsSuccess = true;
    private boolean canRunCmd = true;
    private List<Result> history = new ArrayList<>();

    public MockAdbCmdProvider(List<AdbDevice> devices, List<String> installedPackages, boolean returnsSuccess) {
        this.devices = devices;
        this.installedPackages = installedPackages;
        this.returnsSuccess = returnsSuccess;
    }

    public MockAdbCmdProvider(boolean canRunCmd) {
        this.canRunCmd = canRunCmd;
    }

    @Override
    public Result runCmd(String[] args) {
        String flatCmd = CmdUtil.concat(args, " ");
        StringBuilder out = new StringBuilder();

        if (flatCmd.contains("devices -l")) {
            out.append("List of devices attached\n");
            for (AdbDevice device : devices) {
                out.append(device.serial).append("\tdevice product:").append(device.product).append(" model:").append(device.model).append(" device:").append(device.product).append("\n");
            }
        } else if (flatCmd.contains("pm list packages")) {
            for (String installedPackage : installedPackages) {
                out.append("package:/data/app/").append(installedPackage).append("/base.apk=").append(installedPackage).append("\n");
            }
        } else if (flatCmd.contains("uninstall")) {
            if (returnsSuccess) {
                out.append("Success");
            } else {
                out.append("Failure [MOCK-ERROR-UNINSTALL]");
            }
        } else if (flatCmd.contains("install")) {
            if (returnsSuccess) {
                out.append("Success");
            } else {
                out.append("Failure [MOCK-ERROR-INSTALL]");
            }
        }
        Result r = new Result(out.toString(), null, args, 0);
        history.add(r);
        return r;
    }

    @Override
    public boolean canRunCmd(String[] cmd) {
        return canRunCmd;
    }

    @Override
    public List<Result> getHistory() {
        return history;
    }

    public int deviceCount() {
        return devices.size();
    }

    public int installedCount() {
        return installedPackages.size();
    }
}
