# PhoneGap: Getting Started with XtremePush
With the XtremePush module for PhoneGap you can add XtremePush to both iOS and Android apps built with PhoneGap.

## Platform Specific Information
XtremePush supports push notifications for iOS devices via Apples's Push Notification Service (APNs for short). 
And push notifications for Android devices via Google’s GCM (Google Cloud Messaging for Android) service. This page contains PhoneGap specific instructions but It is also  recommended you read the native documentation for these platforms. This includes the quick start guides: 

* [Getting started with iOS](https://xtremepush.com/docs/libs/ios_start/) 
* [Getting Started with Android](https://xtremepush.com/docs/libs/android_start/)

These docs deal with important topics like:

* Connecting your app to the platform
  * [iOS](https://xtremepush.com/docs/libs/ios_start/#connect-your-app-to-the-xtremepush-platform)
  * [Android](https://xtremepush.com/docs/libs/android_start/#connect-your-app-to-the-xtremepush-platform)   
* [Setting up APNs Certificates for iOS](https://xtremepush.com/docs/libs/ios_start/#setting-up-apns-certificates)
*  [GCM setup for Android ](https://xtremepush.com/docs/libs/android_start/#gcm-setup)
* Sending your first push notification
  * [iOS](https://xtremepush.com/docs/libs/ios_start/#sending-a-push)
  * [Android](https://xtremepush.com/docs/libs/android_start/#sending-a-push)

Both platforms also have documentation on setting up advanced features when integrating XtremePush into your Application:

* [Android: Advanced Feature Setup](android_advanced.md)
* [iOS: Advanced Feature Setup](ios_advanced.md)

Advanced features include: tagging of events, custom push notification handling and location services. 

## XtremePush PhoneGap SDK Setup

First, download the current XtremePush PhoneGap SDK [here](https://github.com/xtremepush/XtremePush_Phonegap/archive/master.zip). Inside of the ZIP you will find the following directories:

* *Example_Apps/* : This folder contains a sample app for each OS supported. The apps are in a folder named after the OS they are related to, for example the iOS example app is in the *ios/* folder. 
<<<<<<< HEAD
* *www/*: This folder contains the javascript part of the plugin. OS specific portion of the plugin in a folder named after that OS i.e. *src/iOS/* or *src/Android/*.
=======
* *www/*: This folder contains the javascript part of the plugin in a folder called.
* *src/* contains the OS specific portion of the plugin in a folder named after that OS i.e. *iOS/* or *Android/*.
>>>>>>> 957f47a58c47fd724564fab75e2f47fbfc3550b1

Full integration guides are given for iOS and Android below.

### Integrate Xtremepush to your PhoneGap iOS Project
1. Navigate to you iOS PhoneGap project in platforms/ios directory.
2. Download the [XtremePush iOS library here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip) and add it to your iOS project following [these instructions](https://xtremepush.com/docs/libs/ios_start/)
3. Navigate to your application directory, and type command:
<<<<<<< HEAD
    cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap
=======
    cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap.git
>>>>>>> 957f47a58c47fd724564fab75e2f47fbfc3550b1

### Tagging your app to enable deeper audience analysis and segmentation <a name="ios_tagging"></a>

XtremePush has two methods for tagging activity in your app one for tagging page impressions and one for tagging any other activity.

To tag page impression add the following method where your page loads:

```javascript
// Send impression to server to track page impressions
XTremePush.hitImpression(successCallback, failCallback, "your_impression_name");
```

To tag any other events send a tag to server using the following method:

```javascript 
// E.g. you might want to tag a button press
XTremePush.hitTag(successCallback, failCallback, "your_tag_name");
```

## Phonegap Example iOS App <a name="ios_example_app"></a>
1. When you have downloaded the latest plugin version ([here](https://github.com/xtremepush/XtremePush_Phonegap/archive/master.zip)) it comes with example apps. 

2. Extract the archive, it contains the folder *XtremePush_phonegap/* and the iOS example app can be found in *Example_Apps/ios/*

3. To experiment  with this sample app open it in Xcode. It is a simple app with all the XtremePush library methods mapped to a series of buttons:

![Phonegap example app](http://cl.ly/image/133e063D0z41/cordova_test_app.png)

4. You can see how the plugin has been configured in the test app in key locations such as: the plugin folders, app delegate, cordova_plugins.js, config.xml, index.js and index.html (see image below).

![Test app in Xcode](http://cl.ly/image/3V3c2t2t0F2J/test_app_xcode%20_phonegap_ios.png)

### Integrate Xtremepush to your PhoneGap Android Project
1. Add XtremePush Android Library to your Project. Please follow [instructions](https://xtremepush.com/docs/libs/android_start/).
2. Navigate to your application directory, and type command:
<<<<<<< HEAD
    cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap
=======
    cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap/tree/master/Plugin
>>>>>>> 957f47a58c47fd724564fab75e2f47fbfc3550b1

### PhoneGap Example Android App
1. When you have downloaded the latest plugin version ([here](https://github.com/xtremepush/XtremePush_Phonegap/archive/master.zip) it comes with example apps. 

2. Extract the archive, it contains the folder *XtremePush_phonegap/* and the Android example app can be found in *Example_Apps/android/*

3. To experiment  with this sample app open it in your IDE. It is a simple app with all the XtremePush library methods mapped to a series of buttons:

![Phonegap example app](http://cl.ly/image/133e063D0z41/cordova_test_app.png)

4. You can see how the plugin has been configured in the test app in key locations such as: the *src/* folder, *assets/www/* folder or *res/xml/config.xml*.



