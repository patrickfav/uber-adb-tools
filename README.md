# Uber Adb Tools for Android
A simple tool that makes it more convenient to **install and uninstall multiple apps on multiple devices** with one command.  Additionally uninstalling allows to use **wildcards as package name**. This is basically a front-end for the [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb.html) which is required to run.

Main features:

* Wildcard support for package names when uninstalling at the end or middle of the filter string: `com.android.*` or `com.android.*e`
* Possible to provide multiple packages to uninstall: `com.android.*,com.google.*,org.wiki*`
* Installing multiple apks with one command
* Installing/Uninstalling on all connected devices

Basic usage:

    java -jar uber-adb-tools.jar -install /folder/apks/
    java -jar uber-adb-tools.jar -uninstall com.your.packa*


[![asciicast](https://asciinema.org/a/86433.png)](https://asciinema.org/a/86433)

This should run on any Windows, Mac or Linux machine where Java7+ is installed. Adb must be installed (comes with [Android SDK](https://developer.android.com/studio/index.html)) and should
be either set in `PATH` or `ANDROID_HOME` should be set.

## Download

**[Grab jar from latest Release](https://github.com/patrickfav/uber-uninstaller-android/releases/latest)**

## Why do I need this?

If you or your company develops many apps or flavors, if you make heavy use of buildTypes and/or if you share testing devices with peers this is a convenient tool to either wipe all test apps from your device (or multiple devices simultaneously) or install all buildTypes for testing in one go. This is even more important where different apps have sideffects if more than one flavor is installed.

## Command Line Interface

Provide more than one package filter:

    java -jar uber-adb-tools.jar -uninstall com.your.packa*,com.their.packa*,com.third.*

Test what would happen with dryrun:

    java -jar uber-adb-tools.jar -uninstall com.your.packa* -dryRun
    java -jar uber-adb-tools.jar -install /myfolder -dryRun

Install/Uninstall only on a certain device by providing the device's serial (check `adb devices`):

    java -jar uber-adb-tools.jar -uninstall com.your.packa* -s IUG65621532
    java -jar uber-adb-tools.jar -install /myfolder -s IUG65621532

Provide your own adb executables:

    java -jar uber-adb-tools.jar -uninstall com.your.packa* -adbPath "C:\pathToAdb\adb.exe"

If the apk is already installed upgrade to new version while keeping the app's data:

    java -jar uber-adb-tools.jar -install /myfolder/my-apk.apk -upgrade

Only install a certain apk file (as opposed to installing all from a folder):

    java -jar uber-adb-tools.jar -install /myfolder/my-apk.apk

The documentation of all possible parameters

    -adbPath <path>              Full path to adb executable. If this is omitted the tool tries to find adb in
                                 PATH env variable.
    -debug                       Prints additional info for debugging.
    -dryRun                      Use this to see what would be installed/uninstalled on what devices with the
                                 given params. Will not install/uninstall anything.
    -force                       If this flag is set all matched apps will be installed/uninstalled without
                                 any further warning. Otherwise a user input is necessary.
    -h,--help                    Prints docs
    -install <apk file/folder>   Provide path to an apk file or folder containing apk files and the tool tries
                                 to install all of them to all connected devices (if not a specfic device is
                                 selected). Either this or 'uninstall' must be passed as argument.
    -keepData                    Only for uninstall: Uses the '-k' param on 'adb uninstall' to keep data and
                                 caches of the app.
    -quiet                       Prints less output.
    -s <device serial>           If this is set, will only use given device. Default is all connected devices.
                                 Device id is the same that is given by 'adb devices'
    -skipEmulators               Skips device emulators for install/uninstall.
    -uninstall <package name>    Filter string that has to be a package name or part of it containing
                                 wildcards '*'. Can be multiple filter Strings comma separated. Example:
                                 'com.android.*' or 'com.android.*,com.google.*'. Either this or 'install'
                                 must be passed as argument.
    -upgrade                     Only for install: Uses the '-r' param on 'adb install' for trying to
                                 reinstall the app and keeping its data.
    -v,--version                 Prints current version.

### Wildcard Support for Uninstall

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

### ADB Executable Location Strategy

If you provide a custom location to adb, the tool will try to use it. Otherwise
it will try to use, which requires adb to be set in `PATH` (See http://stackoverflow.com/questions/20564514).
As a fallback, if the tool does not find the adb in `PATH` it tries to check some default locations for the Android SDK.
One of these default location checks involves checking if `$ANDROID_HOME`/`%ANDROID_HOME%` is set, so if you don't want to set adb in PATH,
use `ANDROID_HOME` environment variable.

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
