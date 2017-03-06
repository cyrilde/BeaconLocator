# Beacon Locator


## Application Description

Beacon Locator is the Android™ application to demonstrate how an android device may detect and react to iBeacon devices.

iBeacon  was introduced by Apple and according to the official press release, iBeacon is a new class of low-powered, low-cost transmitters that can notify nearby iOS 7 devices of their presence, provides apps a whole new level of location awareness, such as trail markers in a park, exhibits in a museum, or product displays in stores. iBeacon works on Bluetooth Low Energy (BLE or Bluetooth Smart) and could be supported by devices with enabled Bluetooth 4.0. iBeacon could send notifications of items nearby that are on sale or items customers may be looking for.

The application allows to register a number of iBeacon devices to react to. Each registered device in the application is represented by three unique identifiers: the device proximity UUID (a 128-bit value that uniquely identifies one or more beacons as a certain type or from a certain organization), major ID (a 16-bit unsigned integer represented as a string that can be used to group related beacons that have the same proximity * UUID), and minor ID (a 16-bit unsigned integer that differentiates beacons with the same proximity UUID and major value). For each registered iBeacon device, a number of beacon actions can be defined.

The defined beacon action determines content to run when the registered iBeacon device is detected. In addition to the specified content, a distance can be defined. The specified distance value is optional and  indicates the distance in meters when the action for the detected device should be performed. If the distance value is not defined, the action will be performed when the device is detected, and there is no other action with defined distance that fits the found device.

The action content can be either image, video, web or application url. The action content will be shown only when the application is in the background. If the action content for the found device is either image or video, the content will be shown in a full screen application fragment. If the content is either web or application url, either browser or specific application will be opened. 

## Application Usage

## How-To's

### How to prepare the development environment
To build and install the application, your system has to have the android sdk and ant. If you already have the configured android sdk and ant, you do no need to complete the steps below and you are ready to build the application; otherwise, please do the following: 

1. In the project root copy the env.source.properties file to env.properties. Add the file to ignore if you are using git
2. Download and unzip the last released ant installation; 
3. Download and unzip the android sdk from http://developer.android.com/sdk/index.html?hl=sk. Minimum required sdk version is 18; 
4. Navigate to the project run folder and open the env.properties file. In the file, please point the android_home and ant_home variables to the proper locations. Save changes; 
5. Run the setantenv.bat batch file. It will set the ant home path; 
6. Run the setandroidsdkenv.bat batch file. It will set the android sdk home path.

### How to build the application from the command line
For this action, the android sdk and ant have to be installed, and properly configured in your system. How to prepare the required environment is described in the "How to prepare the development environment" section 

1. If the project was previously imported in ADT, please open ADT, disable the Build Automatically option ( click on Project --> Build Automatically ) and then clean up the project by clicking the Project --> Clean... menu (please make sure the Clean modal window Start a build immediately checkbox is deselected); 
2. Open the command line and navigate to the project root location; 
3. This step is conditional. If you do not have a properly configured ant environment, please run the setantenv.bat batch file (located in the project root folder). It will set the ant environment properly;
4. This step is conditional. If you do not have a properly configured android environment, please run the setandroidsdkenv.bat. It will set the android environment properly;
5. To create the build.xml file for the project, run android update project -p . -n BeaconDemo . Now you are ready to build the project; 
6. To build the installable application, you need to run a signed and aligned build. To do so, please specify your keystore and alias. To specify your keystore and alias, open the project local.properties file (found in the root of the project directory) and add entries for key.store and key.alias. If you have neither keystore nor alias, please read http://developer.android.com/tools/publishing/app-signing.html .  When the keystore and alias properties are defined, please run the ant release. It will create the Android application .apk file inside the project bin/ directory, named BeaconDemo-release.apk. This .apk file has been signed with the private key, specified in ant.properties and aligned with zipalign. It's ready for installation and distribution. 

For additional details, please check the following links

http://developer.android.com/tools/building/building-cmdline.html

http://developer.android.com/tools/publishing/app-signing.html#releasemode 

### How to install the application from the command line
You may run the signed application either on a physical or virtual device. 
To install the application on the physical device, run the following: 

1. Enable the debugging mode on the device; 
2. Connect the device via USB; 
3. If the application was previously installed on the device, please remove it; otherwise, you will get the "Failure [INSTALL_FAILED_ALREADY_EXISTS]" exception; 
4. Run adb -d install bin/BeaconDemo-release.apk. The application should be installed on the device successfully. 

How to install the application on an emulator device please check the official documentation page http://developer.android.com/tools/building/building-cmdline.html 

### How to import the project to ADT 

1. Open ADT; 
2. Right click on the Package Explorer view and select the Import... menu item; 
3. In the opened modal window, select the General --> Existing Projects into Workspace tree path and click the Next button. In the next Import Projects modal window, the Select root directory radio button should be selected by default (if it doesn't, please select the radio button); 
4. In the Select root directory, click the Browse button and provide a path to the root directory of the unzipped project, and click the OK button. As a result of the action, in the Import Projects modal dialog, the Projects view has to have the BeaconDemo project selected. 
5. In the Import Projects modal dialog, click the Finish button. Now, the Package Explorer view should contain the BeaconDemo project and if the Build Automatically option is enabled, the project build will start immediately; otherwise, you should start the build manually by clicking on Build --> Build Project 

### How to run the project from ADT 

## Possible problems and solutions
The project files encoding is UTF-8. If your ADT (Android Development Tools (http://developer.android.com/tools/sdk/eclipse-adt.html) instance is started with a different default encoding, there is a chance that, during the project import, some files may be faultily imported. If you are not able to build the project after the import, please complete the following steps: 

1. Remove the project from Eclipse (there is no need to remove the project either from the workspace or any other location); 
2. Close ADT; 
3. Go to the ADT eclipse installation folder, open the eclipse.ini file, and add -Dfile.encoding=utf-8 at the end of the file; 
4. Save changes in the eclipse.ini file and start ADT; 
5. Import the project to ADT; 
6. If the Build Automatically option is enabled, the project build will start immediately after the import; otherwise, you should start the build manually by clicking on Build --> Build Project.


## Feedback

This is a personal project. Please report any bugs to cyril.deba@gmail.com.

## Copyright and License

This is released as open-source under the Apache License, Version 2.0.

Copyright 2014 Cyril Deba

Android is a trademark of Google Inc.

iBeacon is trademark of Apple Inc., registered in the U.S. and other countries.
