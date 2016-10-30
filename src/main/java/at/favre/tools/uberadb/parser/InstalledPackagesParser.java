package at.favre.tools.uberadb.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstalledPackagesParser {

    public List<String> parse(String shellOutput) {
        List<String> packages = new ArrayList<>();

        if (shellOutput != null && !shellOutput.isEmpty()) {
            for (String line : shellOutput.split("\\n")) {
                if (line != null) {
                    String parsedPackage = parsePackage(line);
                    if (parsedPackage != null) {
                        packages.add(parsePackage(line));
                    }
                }
            }
        }
        return packages;
    }

    static String parsePackage(String line) {
        if (line.contains("=")) {
            String packageName = line.trim().substring(line.lastIndexOf("=") + 1, line.length());

            int dotCount = packageName.length() - packageName.replace(".", "").length();
            if (dotCount >= 1) {
                return packageName;
            } else {
                //throw new IllegalArgumentException("unexpected package name: "+packageName+" in "+line+" expect to have one or more '.'");
                return null;
            }
        }
        throw new IllegalArgumentException("unexpected installed app syntax: " + line + " expect to have one '='");
    }

    public static boolean wasSuccessfulUninstalled(String cmdOut) {
        return cmdOut != null && cmdOut.toLowerCase().trim().startsWith("success");
    }

    public static boolean wasSuccessfulInstalled(String cmdOut) {
        return cmdOut != null && cmdOut.toLowerCase().trim().contains("success");
    }

    public static String parseShortenedInstallStatus(String cmdOut) {
        if(cmdOut == null) {
            return "";
        }

        Pattern errorPattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matchPattern = errorPattern.matcher(cmdOut);
        List<String> matches = new ArrayList<>();

        while (matchPattern.find()) {
            matches.add(matchPattern.group(1));
        }

        if (!matches.isEmpty()) {
            return "[" + matches.get(matches.size() - 1) + "]";
        }

        String truncated=cmdOut.trim();
        if(cmdOut.contains("\n")) {
            truncated = truncated.substring(truncated.lastIndexOf("\n")+1,truncated.length());
        }

        if (truncated.length() > 80) {
            truncated = "..." + truncated.substring(truncated.length() - 79, truncated.length());
        }

        return truncated;
    }
}
