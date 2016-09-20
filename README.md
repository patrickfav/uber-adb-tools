# Uber Uninstaller for Android
A simple tool that makes it more convenient to uninstall multiple apps by e.g. providing wildcards and
multiple devices. This is basically a front-end for the [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb.html) which is required to run.

Main features:

* Wildcard support for package names at the end or middle of the filter string: `com.android.*` or `com.android.*e`
* Possible to provide multiple packages to uninstall: `com.android.*,com.google.*,org.wiki*`
* Uninstalling on all connected devices

Basic usage:

    java -jar uber-uninstaller-android.jar -p com.your.packa*

[![asciicast](https://asciinema.org/a/86358.png)](https://asciinema.org/a/86358)

This should run on any Windows, Mac or Linux machine where Java7+ is installed. Adb must be installed (comes with [Android SDK](https://developer.android.com/studio/index.html)) and should
be either set in `PATH` or `ANDROID_HOME` should be set.

## Why do I need this?

If you or your company develops many apps or flavors, if you make heavily use of buildTypes and/or if you share test devices with peers this is a convient tool
to wipe all test apps from your device (or multiple devices simultaneously). This is even more important where different apps have sideffects if more than one flavor is installed.

## Command Line Interface

Provide more than one package filter:

    java -jar uber-uninstaller-android.jar -p com.your.packa*,com.their.packa*,com.third.* -dryRun

Test which apps would be uninstalled with a dryrun:

    java -jar uber-uninstaller-android.jar -p com.your.packa* -dryRun

Uninstall only on a certain device by providing the device's serial (check `adb devices`):

    java -jar uber-uninstaller-android.jar -p com.your.packa* -s IUG65621532

Provide your own adb executables:

    java -jar uber-uninstaller-android.jar -p com.your.packa* -adbPath "C:\pathToAdb\adb.exe"

The documentation of all possible parameters

    -adbPath <path>      Full path to adb executable. If this is omitted the tool tries to find adb in PATH
                         env variable.
    -dryRun              Use this to see what would be uninstalled on what devices with the given params. Will
                         not uninstall anything.
    -h,--help            Prints docs
    -keepData            Uses the '-k' param on 'adb uninstall' to keep data and caches of the app.
    -p <package name>    Filter string that has to be a package name or part of it containing wildcards '*'.
                         Can be multiple filter Strings comma separated. Example: 'com.android.*' or
                         'com.android.*,com.google.*'
    -quiet               Prints less output.
    -s <device serial>   If this is set, will only uninstall on given device. Default is all connected
                         devices. Device id is the same that is given by 'adb devices'
    -skipEmulators       Skips device emulators.
    -v,--version         Prints current version.

### Wildcard Support

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

### Adb Executable Location Strategy

If you provide a custom location to adb, the tool will try to use it. Otherwise
it will try to use which requires adb to be set in `PATH` (See http://stackoverflow.com/questions/20564514).
As a fallback, if the tool does not find the adb in `PATH` it tries to check some default locations for the Android SDK.
One of these default location check involves checking if `$ANDROID_HOME`/`%ANDROID_HOME%` is set, so if you dont want to set ADB in PATH,
use `ANDROID_HOME` environment variable.

## Used ADB commands

This tool uses the following adb commands:

`adb devices -l`
Gathers the attached devices. May use the `-s` param with a device's serial.

`adb shell "pm list packages -f"`
List all installed packages. May use the `-s` param with a device's serial.

`adb uninstall <package>`
Uninstalls an app.

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