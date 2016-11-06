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