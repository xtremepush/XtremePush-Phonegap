<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Phonegap Plugin
==================

[About](#about)

[Phonegap - iOS integration guide](#ios_guide)

[Phonegap - Android integration guide](#android_guide)

##	 About <a name="about"></a>

The XtremePush phonegap plugin has two main folders:

* *Example_Apps/* : This folder contains a sample app for each OS supported. The apps are in a folder named after the OS they are related to, for example the iOS example app is in the *ios/* folder. 
* *Plugin/*: This folder contains the javascript part of the plugin in a folder called *JS/*  and the OS specific portion of the plugin in a folder named after that OS i.e. *iOS/* or *Android/*.

Full integration guides are given for iOS and Android below.




<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Phonegap iOS integration guide  <a name="ios_guide"></a> 
==================

[About](#ios_about)

[Integrating](#ios_integrating)

1.  [Download Library](#ios_download)
2.  [Add Xtreme Push to your Project](#ios_add_xtreme)
3.  [Download phonegap plugin](#ios_download_plugin)
4.  [Add phonegap plugin](#ios_add_plugin)
5.  [Connect your App to the XtremePush Platform] (#ios_connect_xtreme)
6.  [Sending your first Push](#fios_irst_push)
7.  [Tagging your app to enable deeper audience analysis and segmentation](#ios_tagging)
8.  [Custom Push Notification Sounds](#ios_custom_sounds)
9.  [Switching Off Location](#ios_location_off)

[Phonegap Example App](#ios_example_app)

[Appendix A: iOS Push Notification Certificates](#ios_certs)

[Appendix B: Method Summary for XtremePush Phonegap Plugin](#ios_method_summary)
	


##	 About <a name="ios_about"></a>
This section should provide all the info you need to integrate your iOS app with the XtremePush platform and send your first push via the platform.

## Integrating <a name="ios_integrating"></a>

### 1. Download Library <a name="ios_download"></a>

1. Download the latest library version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder XtremePush_iOS-master/ with the following files:
   * libXPush.a: The required library
   * XPush.h: The required header file containing methods declarations for using XPush library
   * Readme.md: The file containing instructions how to use XPush library

### 2. Add Xtreme Push to your Project  <a name="ios_add_xtreme"></a>

1. In Finder drag the XtremePush Folder XtremePush_iOS-master/ into your  project's folder

2. Open your project in Xcode

3. Add the folder with library files to your project: Files -> Add files to "ProjectName"

4. libXPush.a should now appear in the Linked Frameworks and Libraries section of your projects general target settings

5. Next ensure the following dependencies are added in Linked Frameworks and Libraries:
	* CoreLocation
	* SystemConfiguration
	* MobileCoreServices
	* CoreTelephony
	* CFNetwork
	* libz.dylib

6. libXPush.a and dependencies now appear in Linked Frameworks and Libraries in your projects general target settings. If unsure of these steps click below for a youtube walkthrough.

[![Video of dependencies being added](http://img.youtube.com/vi/teZ-uG5S-jE/0.jpg)](http://youtu.be/teZ-uG5S-jE)

### 3. Download phonegap plugin <a name="ios_download_plugin"></a>

1. Download the latest plugin version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder *XtremePush_phonegap/Plugin* with the following two folders relevant to the iOS phonegap integration:
    * *iOS/*: Contains the required  .h and .m files of the plugin. 
    * *JS/*: Contains the .js file of the plugin that goes in the www folder.

   Note: folders with the names of other OSes can be ignored, there is also an example iOS app in *Example_Apps/android*
 
### 4. Add phonegap plugin <a name="ios_add_plugin"></a>
1. Add AppDelegate+notification.h, AppDelegate+notification.m, XTremePushPlugin.h, XTremePushPlugin.m  to the plugins/ folder of your project in Xcode (found in the iOS/ folder).

2. In the *JS/* folder of the plugin you downloaded you will find the folder *xtremepush/* containing "xtremepush.js" the javascript file that allows you to use xtremepush methods in your phonegap project. Copy the folder *xtremepush/* to the *www/plugins/* folder of your project.

3. To the file cordova_plugins.js add: 

 ```javascript  
  module.exports = [
        {
                      "file": "plugins/xtremepush/xtremepush.js",
                      "id": "XTremePush",
                      "clobbers": [
                                   "XTremePush"
                                  ]
        }
    ]
 ```
  
  These steps are highlighted in the image below:
   ![Adding phonegap plugin ios](http://cl.ly/image/2q0O3e3p1X3g/add_phonegap_plugin_ios.png)

4.  Finally in config.xml add the path to your plugin file:

```xml
  <feature name="XTremePush">
      <param name="ios-package" value="XTremePushPlugin" />
  </feature>
```
  

### 5. Connect your App to the XtremePush Platform <a name="ios_connect_xtreme"></a>

1. Add your App on the platform by clicking "Integrate Push Features" on your XtremePush Dashboard at xtremepush.com
   
   ![Adding your app on the platform click integrate push features](http://cl.ly/image/050s3O0F2N2N/integrate_app.png)

2. Enter the App Name, upload the App icon, and give a short description of the app. An App key and token have been automatically generated. The App key is used in your iOS project to connect the app and platform. The app token will only be used if you use the external API.  Save your settings and copy  the app key. Your saved settings should be similar to the following.
   
   ![A saved apps settings on the platform](http://cl.ly/image/3I1K2V1t161l/app_saved.png) 
   

3. Return to your project in Xcode. In your Info.plist file add "XtremePushApplicationKey" in key field and your application key in value field. **You can find the "XtremePushApplicationKey" in the Settings (General Settings) page of your app under 'App Key' as seen above**.
   
   ![Where your App Key goes in your project](http://cl.ly/image/0U1r3V2h3P1p/added_key.png)

4. In your Info.plist file add "XtremePushSandboxMode" in key field and set it to YES if you want to use APNS in "Sandbox" gateway to quickly test with a DEBUG build of an app.

   ![Sandbox mode turned on below the App Key](http://cl.ly/image/1V2q251r0n1P/sandboxmode_yes.png)

   **NOTE: Sandbox Mode is only used for DEBUG builds compiled with a development mobile provisioning profile! For Ad Hoc and App Store builds, please, make sure to turn Sandbox Mode off by setting the value to NO or removing the key.**


5. Next return to your App Home on xtremepush.com and in Settings > Application Keys upload your app's iOS push notification certificates for development and production and save them. **If you need help creating these certs read our documentation on iOS Push Notification certificates [here](#certs)**
   
   ![Adding iOS push notification certs](http://cl.ly/image/3s0U0n3P1x3p/adding_certs.png)

6. To register for push notification call the following in your index.js or equivalent javascript code after the deviceReady event:

```javascript
XTremePush.register(successCallback, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived"});
```

where callbackFunction is a javascript function which will be called when push notification is received. Here is an example of how this might look in your index.js:

![Registering for push in javascript](http://cl.ly/image/2a2m2V0q033M/register_phonegap_ios.png)


7. In your Application Delegate, import XPush:

```objc
#import "XPush.h"
```

  And inside `applicationDidFinishLaunching:withOptions:` add the method call `[XPush applicationDidFinishLaunchingWithOptions:launchOptions]` :

```objc
 //This is main kick off after the app inits, the views and Settings are setup here. (preferred - iOS4 and up)
 
 - (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    //Let XPush Know app has finished launching
    [XPush applicationDidFinishLaunchingWithOptions:launchOptions];
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    
```

 finally add the iOS remote notifications handling methods in Application delegate, and place the corresponding XtremePush method call in each of these as shown. **You are now ready to send your first push.**
   
```objc
-(void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    BOOL showAlert = YES;// if yes shows shows stock alert for this push on app open.
    [XPush applicationDidReceiveRemoteNotification:userInfo showAlert:showAlert];
}

- (void) application:(UIApplication *)application
   didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void) application:(UIApplication *)application
    didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
    
}
```


### 6. Sending your first Push <a name="ios_first_push"></a>

1. To send a basic push go to your app home and select create campaign. The first step is to name your camapaign and add some content for the push. In this section you can also link to app pages, urls, or a custom html page for a richer push but for now we will just add text for a simple push.

   ![ Adding Content ](http://cl.ly/image/2S1f1R1K1J47/create_campaign_content.png)

2. Click next and you will be taken to the segments section. For your first push select broadcast to all users and refresh the number of addressable devices if you are using one development device this value should be one.
   
   ![ Selecting a segment ](http://cl.ly/image/0Q260e123V44/add_segment.png)

3. Click next and you will be taken to location, for your first push you will not be tying it to a location so click next and you are taken to schedule. In schedule the default selection is Send Now and to test your first push you will want to keep it that way.

   ![Schedule your push](http://cl.ly/image/0r2913470O2g/add_schedule.png) 

4. Click next and you will be taken to platform. Select iOS as your platform and add a badge count number if you want. If you are using a debug build with a development provision set environment to sandbox. If you are using a distribution build with a production provision set environment to production.  

   ![Configure your platform](http://cl.ly/image/1L0l0Z403H1k/push_add_platform.png)

5. You are almost ready to send your first push. Click preview, review your text and then hit send push. Your iphone will receive the push: 

   ![ Your First Push ](http://cl.ly/image/0d2H0t1v3I0A/first_push.png)


### 7. Tagging your app to enable deeper audience analysis and segmentation <a name="ios_tagging"></a>

XtremePush has two methods for tagging activity in your app one for tagging page impressions and one for tagging any other activity.

To tag page impression add the following method where your page loads:

```javascript
// Send impression to server to track page impressions
XTremePush.hitImpression(successCallback, failCallback, "your_impression_name");
```

To tag any other events send a tag to server using the following method:

```javascript 
// You might for example want to tag a button press
XTremePush.hitTag(successCallback, failCallback, "your_tag_name");
```

### 8. Custom Push Notification Sounds<a name="ios_custom_sounds"></a>

To use custom push sounds you must add the sound files you wish to play to your project as a non-localized resource of the application bundle as shown below. For more on bundle structure see Apple's [Bundle Programming Guide](https://developer.apple.com/library/mac/documentation/corefoundation/conceptual/cfbundles/BundleTypes/BundleTypes.html)

![Custom Sounds Xcode](http://cl.ly/image/3s2u2f360w2d/custo_sound_xcode%20.png)

You can then use these sounds in your app home when creating a campaign. You simply enter the file name under iOS[Custom Sound] as shown below The audio data can be packaged in an aiff, wav, or caf file and for more on preparing custom alert sounds see Apple's [Local and Push Notification Programming Guide](https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/IPhoneOSClientImp.html).

![Using a Custom Sound xtremepush.com](http://cl.ly/image/2T3A1U3S3m1g/add_custom_sound.png)

### 9. Switching Off Location<a name="ios_location_off"></a>


In the default configuration described above location is switched on to allow you to geofence regions related to your app in your app home on xtremepush.com. This allows you to analyse your audience and trigger pushes when they enter your locations. If you do not want these features location can be switched off using the following code in the application delegate.

```objc
#import "XPAppDelegate.h"
#import "XPush.h"

@implementation XPAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customisation after application launch.
    
    // You must call the method to turn off location before `applicationDidFinishLaunchingWithOptions`
    [XPush setLocationEnabled:NO];

    [XPush applicationDidFinishLaunchingWithOptions:launchOptions];
    
    
    return YES;
}

```

and register application from your javascript side after deviceReady event:

```objc
XTremePush.register(successCallback, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived"});
```

## Phonegap Example App <a name="ios_example_app"></a>

1. When you download the latest plugin version ([here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip)) it comes with example apps. 

2. Extract the archive, it contains the folder *XtremePush_phonegap/* and the iOS example app can be found in *Example_Apps/ios/*

3. To experiment  with this sample app open it in Xcode. It is a simple app with all the XtremePush library methods mapped to a series of buttons:

![Phonegap example app](http://cl.ly/image/133e063D0z41/cordova_test_app.png)

4. You can see how the plugin has been configured in the test app in key locations such as: the plugin folders, app delegate, cordova_plugins.js, config.xml, index.js and index.html (see image below).

![Test app in Xcode](http://cl.ly/image/3V3c2t2t0F2J/test_app_xcode%20_phonegap_ios.png)


### Appendix A: iOS Push Notification Certificates<a name="ios_certs"></a>


To integrate XtremePush with an app you need to upload some certificates to your app dashboard on xtremepush.com.
This is because to send push notifications to iOS devices, you need to set up an iOS Push Notifications certificate.
- Go to: https://developer.apple.com/membercenter/
- Log in
- Select Certificates, Identifiers and Profiles

![Apple Developer Member Centre](http://cl.ly/image/2U3N3U2l123W/apple_dev_member_centre.png) 

Select "Identifiers" under iOS Apps column.

![iOS App Identifiers](http://cl.ly/image/1O2G2i203w0S/apple_dev_ios_identifiers.png)

Select the app you are integrating with XtremePush and choose "Edit".

![Edit App ID settings](http://cl.ly/image/3p2K002K3T0N/apple_dev_appid_settings.png)

In iOS App ID Settings select the Push Notifications Check Box and then Click "Create Certificate" in "Development SSL Certificate".

![Create Certificate](http://cl.ly/image/0d1f1N210b17/apple_dev_create_cert.png)

Follow Apple's step by step instructions to generate the certificate. Download the certificate when prompted to do so.

![Cert is Ready](http://cl.ly/image/2W441O0W3w40/apple_dev_cert_ready.png)

Repeat's this procedure for the "Production SSL Certificate". Push should now be enabled for development and distribution.

![Push has been enabled](http://cl.ly/image/040w1Z3r3v0K/apple_dev_push_enabled.png)

Now that both certs have been created both certificates must be uploaded to xtremepush.com to link your app to the XtremePush platform.  Download the certificates if you have not already done so and then open them.The certificates will open in Keychain Access. For each certificate in turn, right click select 'Export'.

![Exporting keychain to link app to XtremePush](http://cl.ly/image/2k0L373I0V3b/exportin_keychain_for_upload.png)

Choose the default export format (.p12) and export the certificates.
Log in to your XtremePush dashboard on xtremepush.com go to your app home and navigate to Settings > Application Keys and upload the two exported certificates.

![Adding iOS push notification certs](http://cl.ly/image/3s0U0n3P1x3p/adding_certs.png)



## Appendix B: Method Summary for XtremePush Phonegap Plugin <a name="ios_method_summary"></a>


### XtremePush Information Methods


**`XTremePush.version(successCallback, failCallback);`**

Returns the version of the XtremePush library. Used to determine the version of the XtremePush library you are using.

--------------------------


**`XTremePush.deviceInfo(successCallback, failCallback);`**

Returns the XtremePush device identifier.  

--------------------------


**`XTremePush.shouldWipeBadgeNumber(successCallback, failCallback);`**

Returns `YES` if badge number is wiped on app loading and `NO` otherwise.

--------------------------


**`XTremePush.isSandboxModeOn(successCallback, failCallback);`**

Returns `YES` if library using sandbox environment for push notifications and `NO` otherwise




### XtremePush Library Setup Methods



 **`XTremePush.setLocationEnabled(successCallback, failCallback, true);`**

Set `false` if location features of XtremePush are to be switched off. Must be called in didFinishLaunchingWithOptions method of your app delegate before you register the XtremePush library. Set `true` or do not call if you want to use location features. 

--------------------------

  
**`XTremePush.setShouldWipeBadgeNumber(successCallback, failCallback, true);`**

Set `true` if application badge number is to be wiped on app loading, otherwise set `false` or do not call. Must be called in didFinishLaunchingWithOptions method of your app delegate before you register the XtremePush library. 

--------------------------


**`XTremePush.unregister(successCallback, failCallback);`**

Called in your application if you want to unregister for remote notifications from the XtremePush platform.


### XtremePush Tagging methods


**`XTremePush.hitImpression(successCallback, failCallback, 'hitImpression');`**

Called where pages load to send impression to XtremePush server to track page impressions.

--------------------------


**`XTremePush.hitTag(successCallback, failCallback, 'hitTag');`**

Called where events occur to send tag to XtremePush server to track events.





<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Phonegap Android integration guide  <a name="android_guide"></a> 
==================

[About](#android_about)

[Integrating](#android_integrating)

1.  [Download Library](#android_download)
2.  [Add XtremePush to your Project](#android_add_xtreme)  
3.  [Download phonegap plugin](#android_download_plugin)
4.  [Add phonegap plugin](#android_add_plugin)
5.  [Connect your App to the XtremePush Platform](#android_connect_xtreme)
6.  [Sending your first Push](#android_first_push)
7.  [Tagging your app to enable deeper audience analysis and segmentation](#android_tagging)

[Phonegap Example App](#android_example_app)

[Appendix A: Getting an API Key for Google Cloud Messaging](#android_keys)

<!--- 6.  [Custom Push Notification Sounds](#custom_sounds) -->
<!--- 7.  [Switching Off Location or adjusting Location frequency](#location_off) -->

<!--- [Appendix B: Method Summary for XtremePush Android Library](#method_summary) -->


## About <a name="android_about"></a> 
This section should provide all the info you need to integrate your Android app with the XtremePush platform and send your first push via the platform.

## Integrating <a name="android_integrating"></a> 

### 1. Download Library <a name="android_download"></a> 


  1. Download the latest library version [here](https://github.com/xtremepush/XtremePush_Android/archive/master.zip). 

  2. Extract the archive, it contains the folder XtremePush_Android-master/ with the following folders and files:

  ![Android library files](http://cl.ly/image/221m1w3y3V11/android_folder_files.png)

### 2. Add XtremePush to your Project<a name="android_add_xtreme"></a> 
1. Import XtremePush_Android-master/ into your IDE. Add it as a library to your project as per your IDE for example on eclipse adding the lbrary looks like this:

   ![Adding library on eclipse](http://cl.ly/image/0Z3G3R2y1M2S/eclipse_add_library.png)

2. Next add the following permissions to your Android Manifest

```xml
    <!-- GCM requires a Google account. -->
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />

    <!-- Keeps the processor from sleeping when a message is received. -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <!-- Other -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

   You must also add a custom permission to your app so only this app can receive messages. **NOTE: the permission must be called YOUR_PACKAGE.permission.C2D_MESSAGE, where YOUR_PACKAGE is the application's package name.**
    
```xml
<!--Creates a custom permission so only this app can receive its messages.-->
<permission
 android:name="YOUR_PACAKAGE.permission.C2D_MESSAGE"
 android:protectionLevel="signature" />

<uses-permission android:name="YOUR_PACAKAGE.permission.C2D_MESSAGE" />

<!-- This app has permission to register and receive data message. -->
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
```

3.  Inside the application element <application></application> in you android manifest add the following:


```xml
<receiver
            android:name="ie.imobile.extremepush.GCMReceiver"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE"/>
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="YOUR_PACKAGE" />
            </intent-filter>
        </receiver>

        <service android:name="ie.imobile.extremepush.GCMIntentService" />
        <receiver android:name="ie.imobile.extremepush.location.ProxymityAlertReceiver" />
        <activity
            android:name="ie.imobile.extremepush.ui.WebViewActivity"
            android:exported="false" />
        <activity
            android:name="ie.imobile.extremepush.ui.XPushLogActivity"
            android:exported="false" />

```
   
**Note: in <category android:name="YOUR_PACKAGE" make sure you replace YOUR_PACKAGE with the application's package name**

### 3. Download phonegap plugin <a name="android_download_plugin"></a>

1. Download the latest plugin version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder *XtremePush_phonegap/Plugin* with the following two folders relevant to the android phonegap integration:
    * *Android/*: Contains the required  .java file of the plugin - *XTremePushPlugin.java*. 
    * *JS/*: Contains the .js file of the plugin that goes in the www folder.

   Note: folders with the names of other OSes can be ignored, there is also an example Android app in the folder Example_Apps/android
 
### 4. Add phonegap plugin <a name="android_add_plugin"></a>
1. Add *XTremePushPlugin.java* to your project in *src/YOUR_PACKAGE*

2. Look for your projects www folder in assets/www and your config file in *res/xml/config.xml*. If these are not visible, to show *assets/www* or *res/xml/config.xml*, go to: Project -> Properties -> Resource -> Resource Filters And delete the exclusion filter.

3. In the *JS/* folder of the plugin you downloaded you will find the folder *xtremepush/* containing "xtremepush.js" the javascript file that allows you to use xtremepush methods in your phonegap project. Copy the folder *xtremepush/* to the folder assets/www/plugins.

3. To the file cordova_plugins.js add: 

 ```javascript  
  module.exports = [
        {
                      "file": "plugins/xtremepush/xtremepush.js",
                      "id": "XTremePush",
                      "clobbers": [
                                   "XTremePush"
                                  ]
        }
    ]
 ```
  
  These steps are highlighted in the image below:
   ![Adding phonegap plugin ios](http://cl.ly/image/2q0O3e3p1X3g/add_phonegap_plugin_ios.png)

4. Finally In *res/xml/config.xml* add the path to your plugin file: 

```xml
	<feature name="XTremePush">
        <param name="android-package" value="YOUR_PACKAGE.XTremePushPlugin" />
    </feature>
```

### 5. Connect your App to the XtremePush Platform <a name="android_connect_xtreme"></a> 

1. Add your App on the platform by clicking "Integrate Push Features" on your XtremePush Dashboard at xtremepush.com
   
   ![Adding your app on the platform click integrate push features](http://cl.ly/image/050s3O0F2N2N/integrate_app.png)

2. Enter the App Name, upload the App icon, and give a short description of the app. An App key and token have been automatically generated. The App key is used in your Android project to connect the app and platform. The app token will only be used if you use the external API.  Save your settings and *copy the app key*. Your saved settings should be similar to the following.
   
   ![A saved apps settings on the platform](http://cl.ly/image/3I1K2V1t161l/app_saved.png) 
   
3. Still in your App Home on xtremepush.com go to Settings > Application Keys 
and copy your API Key for Google Cloud Messaging into *Android Application Key* and click *save*. If you don't now where to get this key please read our documentation on *Getting an API Key for Google Cloud Messaging* [here](#keys) 

   ![Adding the API key](http://cl.ly/image/1U1N022r1B32/adding_API_key.png)


4. Next you will use your app key from the *Settings > General Settings* section of your app home on xtremepush.com and your project number from the google developer console to connect your app to the platform. If you don't know where to get the project number please refer to our documentation on *Getting an API Key for Google Cloud Messaging* [here](#keys) 

5. Return to your project in your IDE. In XTremePushPlugin.java, you must edit two properties `AppId` and `GoogleProjectID`. These should be set with your  XtremePush App Key and your Google project number:

```java
private static String AppId = "Your XtremePush App Key";
private static String GoogleProjectID = "Your google project number";
```

6. To register for push notifications call the following in your index.js or equivalent javascript code after the deviceReady event:

```javascript
XTremePush.register(successCallback, failCallback, {callbackFunction:"onPushReceived"})
```

where callbackFunction is a javascript function which will be called when a push notification is received. 

<!--- 
THESE ARE THE WRONG WAY ROUND
`locationDistance` and `locationTimeout` are optional parameters used to adjust the location  features. Please refer to [Adjusting Location settings](#location_off)  for more information. --->

7. Return to your  Main Activity and import the XtremePush library:

```java
import ie.imobile.extremepush.*;
``` 

Next you must add a line above the onCreate in your main activity like this:

```java
public class XPushPhonegapTestApp extends CordovaActivity 
{

	private PushConnector pushConnector;
    
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
```

    
Finally further down in the main activity add the following two methods: 

```java
       @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            pushConnector.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            pushConnector.onNewIntent(intent);
        }
```

**You are now ready to send your first push.**

### 6. Sending your first Push <a name="android_first_push"></a> 

1. To send a basic push go to your app home and select create campaign. The first step is to name your campaign and add some content for the push. In this section you can also link to app pages, urls, or a custom html page for a richer push but for now we will just add text for a simple push.

   ![ Adding Content ](http://cl.ly/image/2S1f1R1K1J47/create_campaign_content.png)

2. Click next and you will be taken to the segments section. For your first push select broadcast to all users and refresh the number of addressable devices if you are using one development device this value should be one.
   
   ![ Selecting a segment ](http://cl.ly/image/0Q260e123V44/add_segment.png)

3. Click next and you will be taken to location, for your first push you will not be tying it to a location so click next and you are taken to schedule. In schedule the default selection is Send Now and to test your first push you will want to keep it that way.

   ![Schedule your push](http://cl.ly/image/0r2913470O2g/add_schedule.png) 

4. Click next and you will be taken to platform. Select Android as your platform. 

   ![Configure your platform](http://cl.ly/image/2q2P2U141M1o/push_add_android.png)

5. You are almost ready to send your first push. Click preview, review your text and then hit send push. Your Android device will receive the push: 

   ![ Your First Push ](http://cl.ly/image/3i1g2L3L321B/first_push_android.png)

### 7. Tagging your app to enable deeper audience analysis and segmentation <a name="android_tagging"></a> 

XtremePush has two methods for tagging activity in your app one for tagging page impressions and one for tagging any other activity.

To tag page impression call the `pushConnector.hitImpression(String tag)` method  where your page loads after deviceReady event:
		
```javascript
XTremePush.hitImpression(successCallback, failCallback, "your_impression_name");
```

To tag any other events call the `pushConnector.hitTag(String tag)` method where that event occurs:
```javascript    
        XTremePush.hitTag(successCallback, failCallback, "your_tag_name");
  ```      

<!--- ### 6. Custom Push Notification Sounds <a name="custom_sounds"></a> -->

<!--- ### 6. Switching Off Location or adjusting Location frequency <a name="location_off"></a> 

In the default configuration described above location is switched on to allow you to geofence regions related to your app in your app home on xtremepush.com. This allows you to analyse your audience and trigger pushes when they enter your locations. If you do not want these features location can be switched off using the following code ???????

-->

<!---
### 6. Adjusting Location settings <a name="location_off"></a>
If you want to keep the location features but want to manage the trade off between the sensitivity of location detection and the impact on battery life then you have the option to  set the frequency of location updates. This can be done by setting an additional two values when initialising XtremePush in the onCreate method of your main activity. These are *locationCheckTimeout* and *locationDistance* :
		
`init(FragmentManager fm, String appKey, String projectNumber, int locationCheckTimeout, float locationDistance)`.
	
Set *locationCheckTimeout* to your desired location update frequency in minutes and *locationDistance* to desired updates frequency in meters. If you set *locationCheckTimeout* to *30* and  *locationDistance* to *500* then location will be updated every 30 minutes or if the device moves 500 meters depending on which occurs first. You would initialise this setup as follows:

```java
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
	
		pushConnector = PushConnector.init(getSupportFragmentManager(), "XPUSH_APP_KEY", "GOOGLE_PROJECT_NUMBER", 30, 50 );
```

 ### 8. Adding a push notification inbox <a name="inbox"></a> 

##How to start Inbox activity		


if you want to start Inbox activity, you should use the following code snippet(activity must be registered in manifest file)
 
 		startActivity(this, XPushLogActivity.class);
 --->

## Phonegap Example App <a name="android_example_app"></a>

1. When you download the latest plugin version ([here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip)) it comes with example apps. 

2. Extract the archive, it contains the folder *XtremePush_phonegap/* and the Android example app can be found in *Example_Apps/android/*

3. To experiment  with this sample app open it in your IDE. It is a simple app with all the XtremePush library methods mapped to a series of buttons:

![Phonegap example app](http://cl.ly/image/133e063D0z41/cordova_test_app.png)

4. You can see how the plugin has been configured in the test app in key locations such as: the *src/* folder, *assets/www/* folder or *res/xml/config.xml*.



## Appendix A: Getting an API Key for Google Cloud Messaging <a name="android_keys"></a> 
To integrate XtremePush with an Android app you need to upload your GCM API key to your app dashboard on xtremepush.com. This is because to send push notifications to Android devices, you need to set up a Google API Project, enable the GCM service and obtain an API key for it.
In this section we will summarise the main steps involved. You can also find Google's own guide to setting up a Google API Project and obtaining an API key for the GCM service [here](http://developer.android.com/google/gcm/gs.html).

The first step is to:
- Go to: https://console.developers.google.com/
- Log in
- Create a project for your app if you don't already have one

![Google Developer Console API Project](http://cl.ly/image/1a3x3L0M0U2A/google_dev_console.png)

Next click on your project and you will be taken to your project home. Your **project number** is displayed on top of this page you will need that later to integrate your app with XtremePush but first you must select *APIs & auth* to enable GCM.

![Google Developer Console Project Home](http://cl.ly/image/1M1b3M2O1324/google_dev_console_home.png)

In *APIs & auth > API* scroll down until you find Google Cloud Messaging for Android and switch it on:

![Google Developer Console GCM ON](http://cl.ly/image/2H2F1N180q1K/google_dev_gcm_on.png)

In the sidebar on the left, select *APIs & auth > Credentials*. On the right under  *Public API access* , click *Create New Key* and select *Server Key*. Do not specify any ip address and click *Create*.

![Generate API Key](http://cl.ly/image/1Q2G372k0w2w/google_dev_new_key.png)

Copy the new *API Key* you are given under *Public API Access > Key for server applications* in.  Log in to your XtremePush dashboard on xtremepush.com. Go to your app home and navigate to *Settings > Application Keys* and select Android App. Paste the key into *Android Application Key* and click *save*.

![Adding the API key](http://cl.ly/image/1U1N022r1B32/adding_API_key.png)

<!--- ## Appendix B: Method Summary for XtremePush Android Library <a name="method_summary"></a> 

### XtremePush Information Methods

### XtremePush Library Setup Methods

### XtremePush System methods

### XtremePush Tagging methods

##How to get GCM token and device ID in your app

PushConnector.getGCMToken() - to retrieve GCM token.
 
PushConnector.getDeviceID() - to retrieve device ID from Xtremepush.
-->