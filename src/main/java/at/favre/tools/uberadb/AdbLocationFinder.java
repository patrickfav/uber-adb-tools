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

/**
 * Determines the location of the adb tool
 */
public interface AdbLocationFinder {
    enum Location {
        PATH, CUSTOM, WIN_DEFAULT, MAC_DEFAULT, LINUX_DEFAULT, ANDROID_HOME
    }

    /**
     * Returns the location of the adb
     *
     * @param cmdProvider used to test if adb is executable
     * @param customPath  to check provided by user
     * @return the location
     */
    LocationResult find(CmdProvider cmdProvider, String customPath);

    class LocationResult {
        public final AdbLocationFinder.Location location;
        public final String[] args;

        public LocationResult(AdbLocationFinder.Location location, String[] args) {
            this.location = location;
            this.args = args;
        }

        public String arg() {
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            return sb.toString();
        }
    }
}
