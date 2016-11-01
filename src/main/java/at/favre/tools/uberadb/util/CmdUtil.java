package at.favre.tools.uberadb.util;

import at.favre.tools.uberadb.CmdProvider;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class CmdUtil {

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static File checkAndGetFromPATHEnvVar(CmdProvider provider, final String matchesExecutable) {
        String[] pathParts = System.getenv("PATH").split(File.pathSeparator);
        for (String pathPart : pathParts) {
            File pathFile = new File(pathPart);

            if (pathFile.isFile() && pathFile.getName().toLowerCase().contains(matchesExecutable)) {
                return pathFile;
            } else if (pathFile.isDirectory()) {
                File[] matchedFiles = pathFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return FileUtil.getFileNameWithoutExtension(pathname).toLowerCase().equals(matchesExecutable);
                    }
                });

                if (matchedFiles != null) {
                    for (File matchedFile : matchedFiles) {
                        if (provider.canRunCmd(new String[]{matchedFile.getAbsolutePath()})) {
                            return matchedFile;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static OS getOsType() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return OS.WIN;
        }
        if (osName.contains("mac")) {
            return OS.MAC;
        }

        return OS._NIX;
    }

    public enum OS {
        WIN, MAC, _NIX;
    }

    public static String toPlainString(String[] array) {
        StringBuilder sb = new StringBuilder();

        for (String s : array) {
            sb.append(s).append(" ");
        }
        return sb.toString().trim();
    }

    public static String jarVersion() {
        return CmdUtil.class.getPackage().getImplementationVersion();
    }
}
