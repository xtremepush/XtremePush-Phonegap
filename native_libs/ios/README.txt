Welcome to the XtremePush client library for iOS.

Add the library to your project in Xcode
----------------------------------------------------

This folder contains the following additional files: 
* libXPush.a: The required library  
* XPush.h: The required header file containing methods declarations for using XPush library

1. In Finder drag the XtremePush library Folder into your project's folder

2. Open your project in Xcode

3. Add the folder with library files to your project: Files -> Add files to "ProjectName"

4. libXPush.a should now appear in the Linked Frameworks and Libraries section of your projects general target settings

5. Next add the following dependencies in Linked Frameworks and Libraries:

Security
Security.framework
CFNetwork
MobileCoreServices
CoreTelephony
SystemConfiguration
libz.dylib
CoreLocation
CoreBluetooth

6. libXPush.a and all dependencies should now appear in Linked Frameworks and Libraries in your projects general target settings.

7. IMPORTANT! When using the library in Xcode 6 and using Location Services, make sure to include a line NSLocationAlwaysUsageDescription in your Info.plist file.


Documentation
-------------

For instructions on integrating this library with your iOS project, see:

https://xtremepush.com/docs/libs/ios_start/

