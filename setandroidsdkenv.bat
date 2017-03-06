@echo off
FOR /F "tokens=1,2 delims==" %%G IN (local.properties) DO (set %%G=%%H)

set ANDROID_HOME=%android_home%
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%

echo Setting android home to: %ANDROID_HOME%
echo.
echo How to use Android SDK Manager run the following:
echo android -h
echo.
echo.
echo How to use Android Debug Bridge run the following:
echo adb -h
echo.
echo.
echo Please do not forget to set ant environment by running setantenv.bat
echo.
echo.
echo To create the build.xml file for the project run the following:
echo android update project -p . -n BeaconDemo
echo.
echo.
echo To build the application release with ant run the following:
echo ant release
echo.
echo.
echo Please make sure the android environment is set before build application. Also, the keystore and alias have to be defined in local.properties
echo.
echo.
echo To install the application run the following:
echo adb -d install bin/BeaconDemo-release.apk
echo.
echo.
echo For additional details, check docs/readme.doc as well as the following:
echo http://developer.android.com/tools/building/building-cmdline.html
echo http://developer.android.com/tools/publishing/app-signing.html#releasemode
echo.
pause