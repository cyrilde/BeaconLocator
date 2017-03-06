@echo off

FOR /F "tokens=1,2 delims==" %%G IN (env.properties) DO (set %%G=%%H)

set ANT_OPTS=-Xmx200m -XX:MaxPermSize=128M
set ANT_HOME=%ant_home%
set PATH=%ANT_HOME%\bin;%PATH%
set CLASSPATH=
echo Setting ant home to: %ANT_HOME%
echo.
echo To create the build.xml file for the project run the following:
echo android update project -p . -n BeaconDemo
echo.
echo.
echo.
echo To build the application release with ant run the following:
echo ant release
echo.
echo.
echo.
echo Please make sure the android environment is set before build application. Also, the keystore and alias have to be defined in local.properties
echo.
echo.
echo For additional details, check docs/readme.doc as well as the following:
echo http://developer.android.com/tools/building/building-cmdline.html
echo http://developer.android.com/tools/publishing/app-signing.html#releasemode
echo.
pause
