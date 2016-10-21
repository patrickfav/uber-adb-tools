package at.favre.tools.uberadb.ui;

import org.apache.commons.cli.*;

public class CLIParser {

    static final String ARG_INSTALL = "i";
    static final String ARG_UNINSTALL = "u";
    static final String ARG_BUGREPORT = "b";
    static final String ARG_DEVICE_SERIAL = "s";

    public static Arg parse(String[] args) {
        Options options = setupOptions();
        CommandLineParser parser = new DefaultParser();
        Arg argument = new Arg();

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("h") || commandLine.hasOption("help")) {
                printHelp(options);
                return null;
            }

            if (commandLine.hasOption("v") || commandLine.hasOption("version")) {
                System.out.println("Version: " + CLIParser.class.getPackage().getImplementationVersion());
                return null;
            }

            int mainArgCount = 0;
            if (commandLine.hasOption(ARG_UNINSTALL)) {
                argument.mainArgument = commandLine.getOptionValue(ARG_UNINSTALL);
                argument.mode = Arg.Mode.UNINSTALL;
                mainArgCount++;
            }
            if (commandLine.hasOption(ARG_INSTALL)) {
                argument.mainArgument = commandLine.getOptionValue(ARG_INSTALL);
                argument.mode = Arg.Mode.INSTALL;
                mainArgCount++;
            }
            if (commandLine.hasOption(ARG_BUGREPORT)) {
                argument.mainArgument = commandLine.getOptionValue(ARG_BUGREPORT);
                argument.mode = Arg.Mode.BUGREPORT;
                mainArgCount++;
            }

            if (commandLine.hasOption("reportDebugIntent")) {
                String[] reportArgs = commandLine.getOptionValues("reportDebugIntent");

                if (reportArgs.length < 2) {
                    throw new IllegalArgumentException("must provide filter and intent argument eg. 'com.google.* \"-a intent.at\"");
                }
                argument.reportFilterIntent = reportArgs;
            }

            if (mainArgCount != 1) {
                throw new IllegalArgumentException("Must either provide " + ARG_INSTALL + " or " + ARG_UNINSTALL + " argument");
            }

            if (commandLine.hasOption("adbPath")) {
                argument.adbPath = commandLine.getOptionValue("adbPath");
            }

            if (commandLine.hasOption(ARG_DEVICE_SERIAL)) {
                argument.device = commandLine.getOptionValue(ARG_DEVICE_SERIAL);
            }

            argument.dryRun = commandLine.hasOption("dryRun");
            argument.skipEmulators = commandLine.hasOption("skipEmulators");
            argument.keepData = commandLine.hasOption("keepData") || commandLine.hasOption("upgrade");
            argument.quiet = commandLine.hasOption("quiet");
            argument.debug = commandLine.hasOption("debug");
            argument.force = commandLine.hasOption("force");

        } catch (Exception e) {
            System.err.println(e.getMessage());

            CLIParser.printHelp(options);

            argument = null;
        }

        return argument;
    }

    private static Options setupOptions() {
        Options options = new Options();

        Option mainInstall = Option.builder(ARG_INSTALL).longOpt("install").argName("apk file/folder").desc("Provide path to an apk file or folder containing apk files and the tool tries to install all of them to all connected devices (if not a specfic device is selected).").hasArg().build();
        Option mainUninstall = Option.builder(ARG_UNINSTALL).longOpt("uninstall").argName("package filter").hasArg(true).desc("Filter string that has to be a package name or part of it containing wildcards '*' for uninstalling. Can be multiple filter Strings comma separated. Example: 'com.android.*' or 'com.android.*,com.google.*'.").build();
        Option mainBugReport = Option.builder(ARG_BUGREPORT).longOpt("bugreport").argName("out folder").hasArg().optionalArg(true).desc("Creates a generic bug report (including eg. logcat and screenshot) from all connected devices and zips it to the folder given as arg. If no folder is given trys to zips it in the location of the .jar.").build();

        Option adbPathOpt = Option.builder().longOpt("adbPath").argName("path").hasArg(true).desc("Full path to adb executable. If this is omitted the tool tries to find adb in PATH env variable.").build();
        Option deviceOpt = Option.builder(ARG_DEVICE_SERIAL).longOpt("serial").argName("device serial").hasArg(true).desc("If this is set, will only use given device. Default is all connected devices. Device id is the same that is given by 'adb devices'").build();
        Option reportFilter = Option.builder().longOpt("reportDebugIntent").argName("package> <intent").hasArgs().valueSeparator(' ').desc("Only for Bugreport: This is useful to start a e.g. activity that e.g. logs additional info before reading the logcat. " +
                "First param is a package filter (see --uninstall argument) followed by a series of params appended to a 'adb shell am' type command to start an activity or service (See https://goo.gl/MGK7ck). This will be executed for each app/package that is matched by the first parameter. " +
                "You can use the placeholder '${package}' and will substitute the package name. Example: 'com.google* start -n ${package}/com.myapp.LogActivity --ez LOG true' See https://goo.gl/luuPfz for the correct intent start syntax.").build();

        Option dryRunOpt = Option.builder().longOpt("dryRun").hasArg(false).desc("Use this to see what would be installed/uninstalled on what devices with the given params. Will not install/uninstall anything.").build();
        Option skipEmuOpt = Option.builder().longOpt("skipEmulators").hasArg(false).desc("Skips device emulators for install/uninstall.").build();
        Option keepDataOpt = Option.builder().longOpt("keepData").hasArg(false).desc("Only for uninstall: Uses the '-k' param on 'adb uninstall' to keep data and caches of the app.").build();
        Option upgradeOpt = Option.builder().longOpt("upgrade").hasArg(false).desc("Only for install: Uses the '-r' param on 'adb install' for trying to reinstall the app and keeping its data.").build();
        Option quietOpt = Option.builder().longOpt("quiet").hasArg(false).desc("Prints less output.").build();
        Option debugOpt = Option.builder().longOpt("debug").hasArg(false).desc("Prints additional info for debugging.").build();
        Option forceOpt = Option.builder().longOpt("force").hasArg(false).desc("If this flag is set all matched apps will be installed/uninstalled without any further warning. Otherwise a user input is necessary.").build();

        Option help = Option.builder("h").longOpt("help").desc("Prints docs").build();
        Option version = Option.builder("v").longOpt("version").desc("Prints current version.").build();

        OptionGroup mainArgs = new OptionGroup();
        mainArgs.addOption(mainUninstall).addOption(mainInstall).addOption(mainBugReport).addOption(help).addOption(version);
        mainArgs.setRequired(true);

        options.addOptionGroup(mainArgs);

        options.addOption(adbPathOpt).addOption(deviceOpt).addOption(dryRunOpt).addOption(skipEmuOpt).addOption(keepDataOpt)
                .addOption(quietOpt).addOption(debugOpt).addOption(forceOpt).addOption(upgradeOpt).addOption(reportFilter);

        return options;
    }

    private static void printHelp(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.setWidth(120);
        help.setLeftPadding(3);
        help.setDescPadding(3);
        help.printHelp("-" + ARG_INSTALL + " <apk file/folder> | -" + ARG_UNINSTALL + " <package filter> | -" + ARG_BUGREPORT + " <out folder> | --help", "Version: " + CLIParser.class.getPackage().getImplementationVersion(), options, "", false);
    }
}
