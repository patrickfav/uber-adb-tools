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


import at.favre.tools.uberadb.util.FileUtil;

import java.io.File;
import java.util.*;

/**
 * Parses and checks the file input argument
 */
public class FileArgParser {

    public List<File> parseAndSortUniqueFilesNonRecursive(String[] files, String extensionFilter) {
        if (files == null) {
            throw new IllegalArgumentException("input files must not be null");
        }

        if (files.length == 0) {
            return Collections.emptyList();
        }

        Set<File> fileSet = new HashSet<>();

        for (String file : files) {
            File apkFile = new File(file);

            if (apkFile.exists() && apkFile.isDirectory()) {
                for (File dirFile : apkFile.listFiles()) {
                    if (isCorrectFile(dirFile, extensionFilter)) {
                        fileSet.add(dirFile);
                    }
                }
            } else if (isCorrectFile(apkFile, extensionFilter)) {
                fileSet.add(apkFile);
            } else {
                throw new IllegalArgumentException("provided apk path or file '" + file + "' does not exist");
            }
        }

        List<File> resultList = new ArrayList<>(fileSet);
        Collections.sort(resultList);
        return resultList;
    }

    private static boolean isCorrectFile(File f, String extensionFilter) {
        if (f != null && f.exists() && f.isFile()) {
            return FileUtil.getFileExtension(f).equalsIgnoreCase(extensionFilter);
        }
        return false;
    }

}