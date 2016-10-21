# Uber Adb Tools for Android
A simple tool that makes it more convenient to **install, uninstall and creating bug reports for multiple apps on multiple devices** with one command. Additionally uninstalling allows to use **wildcards as package name**. This is basically a front-end for the [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb.html) which is required to run.

Main features:

* Wildcard support for package names when uninstalling at the end or middle of the filter string: `com.android.*` or `com.android.*e`
* Possible to provide multiple packages to uninstall: `com.android.*,com.google.*,org.wiki*`
* Installing multiple apks with one command
* Installing/Uninstalling on all connected devices
* Creating a bug report in zip format including screenshot and logcats
* Starting custom activities to log additional information with bug report

Basic usage:

    java -jar uber-adb-tools.jar --install /folder/apks/
    java -jar uber-adb-tools.jar --uninstall com.your.packa*
    java -jar uber-adb-tools.jar --bugreport ~/adb-logs/

This should run on any Windows, Mac or Linux machine where Java7+ is installed. Adb must be installed (comes with [Android SDK](https://developer.android.com/studio/index.html)) and should
be either set in `PATH` or `ANDROID_HOME` should be set.

## Download

**[Grab jar from latest Release](https://github.com/patrickfav/uber--uninstaller-android/releases/latest)**

## Demo

[![asciicast](https://asciinema.org/a/86433.png)](https://asciinema.org/a/86433)

## Why do I need this?

If you or your company develops many apps or flavors, if you make heavy use of buildTypes and/or if you share testing devices with peers this is a convenient tool to either wipe all test apps from your device (or multiple devices simultaneously) or install all buildTypes for testing in one go. This is even more important where different apps have sideffects if more than one flavor is installed.

## Command Line Interface

The documentation of all possible parameters

       --adbPath <path>                         Full path to adb executable. If this is omitted the tool tries to find
                                                adb in PATH env variable.
    -b,--bugreport <out folder>                 Creates a generic bug report (including eg. logcat and screenshot) from
                                                all connected devices and zips it to the folder given as arg. If no
                                                folder is given trys to zips it in the location of the .jar.
       --debug                                  Prints additional info for debugging.
       --dryRun                                 Use this to see what would be installed/uninstalled on what devices with
                                                the given params. Will not install/uninstall anything.
       --force                                  If this flag is set all matched apps will be installed/uninstalled
                                                without any further warning. Otherwise a user input is necessary.
    -h,--help                                   Prints docs
    -i,--install <apk file/folder>              Provide path to an apk file or folder containing apk files and the tool
                                                tries to install all of them to all connected devices (if not a specfic
                                                device is selected).
       --keepData                               Only for uninstall: Uses the '-k' param on 'adb uninstall' to keep data
                                                and caches of the app.
       --quiet                                  Prints less output.
       --reportDebugIntent <package> <intent>   Only for Bugreport: This is useful to start a e.g. activity that e.g.
                                                logs additional info before reading the logcat. First param is a package
                                                filter (see --uninstall argument) followed by a series of params
                                                appended to a 'adb shell am' type command to start an activity or
                                                service (See https://goo.gl/MGK7ck). This will be executed for each
                                                app/package that is matched by the first parameter. You can use the
                                                placeholder '${package}' and will substitute the package name. Example:
                                                'com.google* start -n ${package}/com.myapp.LogActivity --ez LOG true'
                                                See https://goo.gl/luuPfz for the correct intent start syntax.
    -s,--serial <device serial>                 If this is set, will only use given device. Default is all connected
                                                devices. Device id is the same that is given by 'adb devices'
       --skipEmulators                          Skips device emulators for install/uninstall.
    -u,--uninstall <package filter>             Filter string that has to be a package name or part of it containing
                                                wildcards '*' for uninstalling. Can be multiple filter Strings comma
                                                separated. Example: 'com.android.*' or 'com.android.*,com.google.*'.
       --upgrade                                Only for install: Uses the '-r' param on 'adb install' for trying to
                                                reinstall the app and keeping its data.
    -v,--version                                Prints current version.
   
### General

Test what would happen with dryrun:

    java -jar uber-adb-tools.jar --install /myfolder -dryRun


Install/Uninstall only on a certain device by providing the device's serial (check `adb devices`):

    java -jar uber-adb-tools.jar --uninstall com.your.packa* -s IUG65621532

Skip user prompt:

    java -jar uber-adb-tools.jar --uninstall com.your.packa* --force

Provide your own adb executables:

    java -jar uber-adb-tools.jar --bugreport --adbPath "C:\pathToAdb\adb.exe"

### Install

If the apk is already installed upgrade to new version while keeping the app's data:

    java -jar uber-adb-tools.jar --install /myfolder/my-apk.apk --upgrade

Only install a certain apk file (as opposed to installing all from a folder):

    java -jar uber-adb-tools.jar --install /myfolder/my-apk.apk

### Uninstall

Provide more than one package filter:

    java -jar uber-adb-tools.jar --uninstall com.your.packa*,com.their.packa*,com.third.*

#### Wildcard Support for Uninstall

It is possible to just use the full package name like using `adb uninstall com.mypackage.app`. 
To take advantage of the enhance features wildcards are supported:

    com.android.*
 
Will match e.g. `com.android.app`, `com.android.app.maps`, `com.android.something `
Will NOT match `com.android`, `org.com.android`

    com.android.*e
    
Will match e.g. `com.android.app.service`, `com.android.elle`
Will NOT match `com.android`, `com.android.app`

    com.android.*.debug
    
Will match e.g. `com.android.app.service.debug`, `com.android.maps.debug`
Will NOT match `com.android.debug`, `com.android.app`

Note: Wildcard is not supported at the beginning of the package filter

### Bugreport

Omit the location param to use the same folder as the executable

    java -jar uber-adb-tools.jar --bugreport --skipEmulators

Provide a activity intent to start before logcat will be pulled for request apps (packages) while using package placeholder:

    java -jar uber-adb-tools.jar --bugreport --reportDebugIntent your.package.* start -n ${package}/com.company.app.DebugLogActivity --ez HEADLESS true
    
#### Starting custom intents

When using the `--reportDebugIntent` argument you first have to provide an package filter string (see uninstall) and then a series of arguments describing the activity/service/etc. to start. These arguments are intenally append to a `adb shell am ...` command, therefore use the same syntax, eg. start to `start` an activity with intent params and `startservice` to start an service. For details on the intent syntax see https://developer.android.com/studio/command-line/shell.html#IntentSpec.
    
An example on how to use this:

1. Create an activity that logs some custom code when a specific flag is set

        public class DebugLogActivity extends AWalletActivity {
        ...
        private static final String KEY_HEADLESS = "HEADLESS"; //used with external programs

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    
            if (getIntent().getBooleanExtra(KEY_HEADLESS, false)) {
                //TODO add custom log here
                finish();
            } else {
                setContentView(R.layout.activity_layout);
                //create here your activity as you normally would if you need ui
            }
        }
        ...
        }

In your AndroidManifest declare the activity as exported:

        <activity
            android:name=".DebugLogActivity"
            android:label="@string/app_name"
            android:exported="true">
        </activity>

You should now be able to access this feature with adb:

    adb shell am start -n <your_application_id>/<your_internal_package>.AppInfoActivity --ez HEADLESS true

Note: `<your_application_id>` is what you set in gradle as applicationId and `<your_internal_package>` is your actual java package (they might be the same)

Now we might have the problem when using multiple flavours, that you want to use this command on multiple apps - you can use a placeholder for this: `${package}` so the final bugreport call will look like:

    java -jar uber-adb-tools.jar --bugreport --reportDebugIntent your.package.* start -n ${package}/com.company.app.DebugLogActivity --ez HEADLESS true

### ADB Executable Location Strategy

If you provide a custom location to adb, the tool will try to use it. Otherwise it will try to use, which requires adb to be set in `PATH` (See http://stackoverflow.com/questions/20564514).As a fallback, if the tool does not find the adb in `PATH` it tries to check some default locations for the Android SDK. One of these default location checks involves checking if `$ANDROID_HOME`/`%ANDROID_HOME%` is set, so if you don't want to set adb in PATH, use `ANDROID_HOME` environment variable.

## Used ADB commands

This tool uses the following adb commands:

`adb devices -l`
Gathers the attached devices. May use the `-s` param with a device's serial.

`adb shell "pm list packages -f"`
List all installed packages. May use the `-s` param with a device's serial.

`adb shell pm uninstall <package>`
Uninstalls an app.

`adb install <apk-file>`
Installs an app.

## Build

Use maven (3.1+) to create a jar including all dependencies

    mvn clean package

## Tech Stack

* Java 7
* Maven

# License

Copyright 2016 Patrick Favre-Bulle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
