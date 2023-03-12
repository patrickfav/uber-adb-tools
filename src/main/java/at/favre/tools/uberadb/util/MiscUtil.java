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

package at.favre.tools.uberadb.util;

import java.io.File;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MiscUtil {

    private MiscUtil() {
    }

    public static void zip(File targetZipFile, List<ZipFileDescriptor> filesToZip) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        URI zipUri = new URI("jar:" + targetZipFile.toURI().getScheme(), targetZipFile.toURI().getPath(), null);

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
            for (ZipFileDescriptor fileDesc : filesToZip) {
                Path externalTxtFile = Paths.get(fileDesc.file.toURI());
                Path pathInZipfile = zipfs.getPath(fileDesc.toZipPath());

                if (fileDesc.subFolder != null && Files.notExists(zipfs.getPath(fileDesc.subFolder))) {
                    Files.createDirectory(zipfs.getPath(fileDesc.subFolder));
                }

                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static String[] getPackagesWithoutDelayValue(String[] arguments) {
        if (getIntInLastElement(arguments, Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            return Arrays.copyOfRange(arguments, 0, arguments.length - 1);
        }
        return arguments;
    }

    public static int getIntInLastElement(String[] arguments, int defaultValue) {
        if (arguments == null || arguments.length == 0) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(arguments[arguments.length - 1]);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.interrupted();
            e.printStackTrace();
        }
    }

    public static class ZipFileDescriptor {
        public final File file;
        public final String subFolder;

        public ZipFileDescriptor(String subFolder, File file) {
            this.file = file;
            this.subFolder = subFolder;
        }

        String toZipPath() {
            if (subFolder != null) {
                return File.separator + subFolder + File.separator + file.getName();
            } else {
                return file.getName();
            }
        }
    }
}
