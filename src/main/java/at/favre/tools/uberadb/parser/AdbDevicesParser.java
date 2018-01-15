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

package at.favre.tools.uberadb.parser;

import java.util.ArrayList;
import java.util.List;

public class AdbDevicesParser {
    private static final String STATUS_OFFLINE = "offline";
    private static final String STATUS_OK = "device";
    private static final String STATUS_UNAUTHORIZED = "unauthorized";
    private static final String STATUS_BOOTLOADER = "bootloader";

    private static final String PROP_MODEL = "model";
    private static final String PROP_DEVICE = "device";
    private static final String PROP_PRODUCT = "product";

    private static final java.lang.String EMULATOR = "emulator-";

    public List<AdbDevice> parse(String adbOutput) {
        List<AdbDevice> devices = new ArrayList<>();

        String[] lines = adbOutput.split("\\n");

        if (lines.length > 1) {
            for (int i = 1; i < lines.length; i++) {
                AdbDevice d = parseDeviceLine(lines[i]);
                if (d != null) {
                    devices.add(d);
                }
            }
        }
        return devices;
    }

    static AdbDevice parseDeviceLine(String line) {
        char[] chars = line.replace("\t", " ").toCharArray();
        StringBuilder deviceName = new StringBuilder();
        String additionalInfo = null;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ' ') {
                deviceName.append(chars[i]);
            } else {
                additionalInfo = line.substring(i, line.length()).trim();
                break;
            }
        }
        String status, product = null, model = null;

        if (additionalInfo != null) {
            String[] addProperties = additionalInfo.split(" ");
            if (addProperties.length == 1) {
                status = addProperties[0];
            } else if (addProperties.length > 1) {
                status = addProperties[0];
                for (int i = 1; i < addProperties.length; i++) {
                    String[] keyValue = addProperties[i].split(":");
                    if (keyValue.length == 2) {
                        if (keyValue[0].equals(PROP_MODEL)) {
                            model = keyValue[1];
                        }
                        if (keyValue[0].equals(PROP_PRODUCT)) {
                            product = keyValue[1];
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("could not parse additional info from device: " + additionalInfo);
            }

            boolean isEmulator = false;
            if (deviceName.toString().startsWith(EMULATOR)) {
                isEmulator = true;
            }

            return new AdbDevice(deviceName.toString(), translate(status), model, product, isEmulator);
        }

        return null;
    }

    private static AdbDevice.Status translate(String status) {
        switch (status) {
            case STATUS_OFFLINE:
                return AdbDevice.Status.OFFLINE;
            case STATUS_OK:
                return AdbDevice.Status.OK;
            case STATUS_UNAUTHORIZED:
                return AdbDevice.Status.UNAUTHORIZED;
            case STATUS_BOOTLOADER:
                return AdbDevice.Status.BOOTLOADER;
            default:
                return AdbDevice.Status.UNKNOWN;
        }
    }
}
