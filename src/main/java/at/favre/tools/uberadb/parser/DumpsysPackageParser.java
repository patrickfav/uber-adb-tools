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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DumpsysPackageParser {

    public PackageInfo parseSingleDumpsysPackage(String packageName, String dumpsysOut) {
        try {
            String versionName, codePath, installTime, updateTime, pkgHash;
            int versionCode;

            versionCode = Integer.valueOf(find("versionCode=(\\d+?)\\s", dumpsysOut));
            versionName = find("versionName=(.+?)\\s", dumpsysOut);
            codePath = find("codePath=(.+?)\\s", dumpsysOut);
            installTime = find("firstInstallTime=(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s", dumpsysOut);
            updateTime = find("lastUpdateTime=(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s", dumpsysOut);
            pkgHash = find("\\s+Package \\[" + Pattern.quote(packageName) + "\\] \\((.+?)\\):\\s", dumpsysOut);
            return new PackageInfo(packageName, versionCode, versionName.trim(), codePath, installTime, updateTime, pkgHash);
        } catch (Exception e) {
            return null;
        }
    }

    private String find(String pattern, String haystack) {
        Matcher matcherVersionCode = Pattern.compile(pattern).matcher(haystack);
        matcherVersionCode.find();
        return matcherVersionCode.group(1);
    }

    public static class PackageInfo {
        public final String packageName;
        public final int versionCode;
        public final String versionName;
        public final String codePath;
        public final String firstInstallTime;
        public final String updateTime;
        public final String pkgHash;

        public PackageInfo(String packageName, int versionCode, String versionName, String codePath, String firstInstallTime, String updateTime, String pkgHash) {
            this.packageName = packageName;
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.codePath = codePath;
            this.firstInstallTime = firstInstallTime;
            this.updateTime = updateTime;
            this.pkgHash = pkgHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PackageInfo that = (PackageInfo) o;

            if (versionCode != that.versionCode) return false;
            if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) return false;
            if (versionName != null ? !versionName.equals(that.versionName) : that.versionName != null) return false;
            if (codePath != null ? !codePath.equals(that.codePath) : that.codePath != null) return false;
            if (firstInstallTime != null ? !firstInstallTime.equals(that.firstInstallTime) : that.firstInstallTime != null)
                return false;
            if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null) return false;
            return pkgHash != null ? pkgHash.equals(that.pkgHash) : that.pkgHash == null;

        }

        @Override
        public int hashCode() {
            int result = packageName != null ? packageName.hashCode() : 0;
            result = 31 * result + versionCode;
            result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
            result = 31 * result + (codePath != null ? codePath.hashCode() : 0);
            result = 31 * result + (firstInstallTime != null ? firstInstallTime.hashCode() : 0);
            result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
            result = 31 * result + (pkgHash != null ? pkgHash.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "PackageInfo{" +
                    "packageName='" + packageName + '\'' +
                    ", versionCode=" + versionCode +
                    ", versionName='" + versionName + '\'' +
                    ", codePath='" + codePath + '\'' +
                    ", firstInstallTime='" + firstInstallTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", pkgHash='" + pkgHash + '\'' +
                    '}';
        }
    }

}
