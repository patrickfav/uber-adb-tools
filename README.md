# Uber Adb Tools for Android
A simple tool that makes it more convenient to **install, uninstall and creating bug reports and more for multiple apps on multiple devices** with one command. Additionally uninstalling allows to use **wildcards as package name**. This is basically a front-end for the [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb.html) which is required to run.

Main features:

* Process multiple apps with wildcard support for package matching (e.g. `com.android.*` or `com.android.*e`) for features like: uninstalling, stopping, starting, clearing and showing app info
* Installing multiple apks from different locations with one command
* All commands can be executed on all connected devices simultaneously
* Fast and easy bug report features with screenshot, logcats, customizable dumpsys logs, pm and more 
* Starting custom activities to log additional information with bug report

Basic usage:

    java -jar uber-adb-tools.jar --install /folder/apks/
    java -jar uber-adb-tools.jar --uninstall com.your.packa*
    java -jar uber-adb-tools.jar --bugreport
    
More features:
    
    java -jar uber-adb-tools.jar --force-stop com.your.packa*
    java -jar uber-adb-tools.jar --clear com.your.packa*
    java -jar uber-adb-tools.jar --appinfo com.your.packa*
    java -jar uber-adb-tools.jar --start com.your.packa*

This should run on any Windows, Mac or Linux machine,

### Requirements

* JDK 7
* [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb.html) set in `PATH` or `ANDROID_HOME` is set (some default locations work also, see below)

## Download

**[Grab jar from latest Release](https://github.com/patrickfav/uber-adb-tools/releases/latest)**

## Demo

[![asciicast](https://asciinema.org/a/91091.png)](https://asciinema.org/a/91091)

## Command Line Interface

The documentation of all possible parameters

       --adbPath <path>                         Full path to adb executable. If this is omitted the tool tries to find
                                                adb in PATH env variable.
       --appinfo <package filter>               Will show additional information for like version, install-time, etc of
                                                the apps matching the argument. Argument is the filter string that has
                                                to be a package name or part of it containing wildcards '*'. Can be
                                                multiple filter Strings space separated. Example: 'com.android.*' or
                                                'com.android.* com.google.*'.
    -b,--bugreport <out folder>                 Creates a generic bug report (including eg. logcat and screenshot) from
                                                all connected devices and zips it to the folder given as arg. If no
                                                folder is given tries to zips it in the location of the .jar.
       --clear <package filter>                 Will clear app data for given packages. Argument is the filter string
                                                that has to be a package name or part of it containing wildcards '*'.
                                                Can be multiple filter Strings space separated. Example: 'com.android.*'
                                                or 'com.android.* com.google.*'.
       --debug                                  Prints additional info for debugging.
       --dryRun                                 Use this to see what would be installed/uninstalled on what devices with
                                                the given params. Will not install/uninstall anything.
       --dumpsysServices <service-name>         Only for bugreport: include only theses dumpsys services. See all
                                                services with 'adb shell dumpsys list'
       --force                                  If this flag is set all matched apps will be installed/uninstalled
                                                without any further warning. Otherwise a user input is necessary.
       --force-stop <package filter>            Will stop the process of given packages. Argument is the filter string
                                                that has to be a package name or part of it containing wildcards '*'.
                                                Can be multiple filter Strings space separated. Example: 'com.android.*'
                                                or 'com.android.* com.google.*'.
       --grant                                  Only for install: will grant all permissions set in the apk
                                                automatically.
    -h,--help                                   Prints docs
    -i,--install <apk file/folder>              Provide path to an apk file or folder containing apk files and the tool
                                                tries to install all of them to all connected devices (if not a specfic
                                                device is selected). It is possible to pass multiple files/folders as
                                                arguments e.g. '/apks apk1.apk apk2.apk'
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
       --simpleBugreport                        Only for bugreport: report will only contain the most essential data
       --skipEmulators                          Skips device emulators for install/uninstall.
       --start <package filter> <[seconds]>     Will start the launcher activity of this app. Argument is the filter
                                                string that has to be a package name or part of it containing wildcards
                                                '*'. Can be multiple filter Strings space separated. Example:
                                                'com.android.*' or 'com.android.* com.google.*'. The last argument may
                                                be a int in seconds which represents the wait time between the apps eg.:
                                                'com.exmaple.* 10' will have a 10 sec delay between starts.
    -u,--uninstall <package filter>             Filter string that has to be a package name or part of it containing
                                                wildcards '*' for uninstalling. Can be multiple filter Strings space
                                                separated. Example: 'com.android.*' or 'com.android.* com.google.*'.
       --upgrade                                Only for install: Uses the '-r' param on 'adb install' for trying to
                                                reinstall the app and keeping its data.
    -v,--version                                Prints current version.
       --waitForDevice                          If set, will wait until a device is connected and debug mode is enabled.
   
### General

Test what would happen with dryrun:

    java -jar uber-adb-tools.jar --install /myfolder -dryRun

Install/Uninstall only on a certain device by providing the device's serial (check `adb devices`):

    java -jar uber-adb-tools.jar --uninstall com.your.packa* -s IUG65621532

Skip user prompt:

    java -jar uber-adb-tools.jar --uninstall com.your.packa* --force

Provide your own adb executables:

    java -jar uber-adb-tools.jar --bugreport --adbPath "C:\pathToAdb\adb.exe"
    
Wait until device is connected:

    java -jar uber-adb-tools.jar --uninstall com.your.packa* --waitForDevice 

### Install

If the apk is already installed upgrade to new version while keeping the app's data:

    java -jar uber-adb-tools.jar --install /myfolder/my-apk.apk --upgrade

Only install a certain apk file (as opposed to installing all from a folder):

    java -jar uber-adb-tools.jar --install /myfolder/my-apk.apk

Provide multiple files/folder

    java -jar uber-adb-tools.jar --install /myfolder/my-apk.apk /otherfolder /apk1.apk

### Uninstall

Provide more than one package filter:

    java -jar uber-adb-tools.jar --uninstall com.your.packa* com.their.packa* com.third.*

#### Wildcard Support for Package Filter

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

The idea behind this is to get a smaller faster version of the default `adb bugreport` that is easier to read and understand as well as customizable and more practical for the "every-day-bug".

#### Content

A full bugreport will contain the following data:

* a screenshot (downscaled if bigger than 2MB)
* logcats (normal, radio and event)
* some dumpsys services logs (either a default list is used or the ones provided with `--dumpsysServices`)
* info from packagemanger (`adb shell pm ...`)
* misc data like running processes

#### Examples

Provide your own dumpsys services 

    java -jar uber-adb-tools.jar --bugreport --dumpsysServices package nfc battery

Only log the most essential

    java -jar uber-adb-tools.jar --bugreport --simpleBugreport

Provide a activity intent to start before logcat will be pulled for request apps (packages) while using package placeholder:

    java -jar uber-adb-tools.jar --bugreport --reportDebugIntent your.package.* start -n ${package}/com.company.app.DebugLogActivity --ez HEADLESS true
    
#### Starting custom intents

When using the `--reportDebugIntent` argument you first have to provide a package filter string (see uninstall) and then a series of arguments describing the activity/service/etc. to start. These arguments are internally appended to a `adb shell am ...` command, therefore use the same syntax, eg. start to `start` an activity with intent params and `startservice` to start an service. For details on the intent syntax, see https://developer.android.com/studio/command-line/shell.html#IntentSpec.
    
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

### Additional Features

Clear app data & caches of all matching apps

    java -jar uber-adb-tools.jar --clear com.example.*

Force stop all matching apps

    java -jar uber-adb-tools.jar --force-stop com.example.*

Show app info (version, install time, etc.) of matched apps

    java -jar uber-adb-tools.jar --appinfo com.example.*

Start all matching apps (launcher activity) with start delay of 9 seconds:

    java -jar uber-adb-tools.jar --start com.your.packa* 9

### Process Return Value

This application will return `0` if every install/uninstall was successful, `1` if an error happens (e.g. wrong arguments) and `2` if at least one part of a install/uninstall process was not successful.

### ADB Executable Location Strategy

If you provide a custom location to adb, the tool will try to use it. Otherwise it will try to use the one provided by the system, which requires adb to be set in `PATH` (See http://stackoverflow.com/questions/20564514 ). As a fallback, if the tool does not find the adb in `PATH` it tries to check some default locations for the Android SDK. One of these default location checks involves checking if `ANDROID_HOME` is set, so if you don't want to set adb in `PATH`, use `ANDROID_HOME` environment variable.

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
