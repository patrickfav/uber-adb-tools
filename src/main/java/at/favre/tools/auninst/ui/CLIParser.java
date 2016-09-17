package at.favre.tools.auninst.ui;

import org.apache.commons.cli.*;

public class CLIParser {

    static final String ARG_PACKAGE_FILTER = "p";
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

            argument.filterString = commandLine.getOptionValue(ARG_PACKAGE_FILTER);

            if (commandLine.hasOption("adbPath")) {
                argument.adbPath = commandLine.getOptionValue("adbPath");
            }

            if (commandLine.hasOption(ARG_DEVICE_SERIAL)) {
                argument.device = commandLine.getOptionValue(ARG_DEVICE_SERIAL);
            }

            argument.dryRun = commandLine.hasOption("dryRun");
            argument.skipEmulators = commandLine.hasOption("skipEmulators");
            argument.keepData = commandLine.hasOption("keepData");
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
        Option filterOpt = Option.builder(ARG_PACKAGE_FILTER).argName("package name").hasArg(true).desc("Filter string that has to be a package name or part of it containing wildcards '*'. Can be multiple filter Strings comma separated. Example: 'com.android.*' or 'com.android.*,com.google.*'").build();

        Option adbPathOpt = Option.builder("adbPath").argName("path").hasArg(true).desc("Full path to adb executable. If this is omitted the tool tries to find adb in PATH env variable.").build();
        Option deviceOpt = Option.builder(ARG_DEVICE_SERIAL).argName("device serial").hasArg(true).desc("If this is set, will only uninstall on given device. Default is all connected devices. Device id is the same that is given by 'adb devices'").build();

        Option dryRunOpt = Option.builder("dryRun").hasArg(false).desc("Use this to see what would be uninstalled on what devices with the given params. Will not uninstall anything.").build();
        Option skipEmuOpt = Option.builder("skipEmulators").hasArg(false).desc("Skips device emulators.").build();
        Option keepDataOpt = Option.builder("keepData").hasArg(false).desc("Uses the '-k' param on 'adb uninstall' to keep data and caches of the app.").build();
        Option quietOpt = Option.builder("quiet").hasArg(false).desc("Prints less output.").build();
        Option debugOpt = Option.builder("debug").hasArg(false).desc("Prints additional info for debugging.").build();
        Option forceOpt = Option.builder("force").hasArg(false).desc("If this flag is set all matched apps will be uninstalled without any further warning. Otherwise a user input is necessary.").build();

        Option help = Option.builder("h").longOpt("help").desc("Prints docs").build();
        Option version = Option.builder("v").longOpt("version").desc("Prints current version.").build();

        OptionGroup mainArgs = new OptionGroup();
        mainArgs.addOption(filterOpt).addOption(help).addOption(version);
        mainArgs.setRequired(true);

        options.addOptionGroup(mainArgs);
        options.addOption(adbPathOpt).addOption(deviceOpt).addOption(dryRunOpt).addOption(skipEmuOpt).addOption(keepDataOpt)
                .addOption(quietOpt).addOption(debugOpt).addOption(forceOpt);

        return options;
    }

    private static void printHelp(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.setWidth(110);
        help.setLeftPadding(4);
        help.printHelp("uber-uninstaller-android", "Version: " + CLIParser.class.getPackage().getImplementationVersion(), options, "", true);
    }
}
