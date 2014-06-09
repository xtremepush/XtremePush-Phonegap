<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> iOS integration guide  
==================

[About](#about)

[Integrating](#integrating)

1.  [Download Library](#download)
2.  [Add Xtreme Push to your Project](#add_xtreme)
3.  [Connect your App to the XtremePush Platform] (#connect_xtreme)
4.  [Sending your first Push](#first_push)
5.  [Tagging your app to enable deeper audience analysis and segmentation](#tagging)
6.  [Custom Push Notification Sounds](#custom_sounds)
7.  [Switching Off Location](#location_off)

[Appendix A: iOS Push Notification Certificates](#certs)

[Appendix B: Method Summary for XtremePush iOS Library](#method_summary)
	


##	 About <a name="about"></a>
This document should provide all the info you need to integrate your iOS app with the XtremePush plaform and send your first push via the platform.

## Integrating <a name="integrating"></a>

### 1. Download Library <a name="download"></a>

1. Download the latest library version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder XtremePush_iOS-master/ with the following files:
   * libXPush.a: The required library
   * XPush.h: The required header file containing methods declarations for using XPush library
   * Readme.md: The file containing instructions how to use XPush library

### 2. Add Xtreme Push to your Project  <a name="add_xtreme"></a>

1. In Finder drag the Xtreme Push Folder XtremePush_iOS-master/ into your  project's folder

2. Open your project in Xcode

3. Add the folder with library files to your project: Files -> Add files to "ProjectName"

4. libXPush.a should now appear in the Linked Frameworks and Libraries section of your projects general target settings

5. Next add the following dependencies in Linked Frameworks and Libraries:
	* CoreLocation
	* SystemConfiguration
	* MobileCoreServices
	* CoreTelephony
	* CFNetwork
	* libz.dylib

6. libXPush.a and dependencies now appear in Linked Frameworks and Libraries in your projects general target settings. If unsure of these steps click below for a youtube walkthrough.

7. Add AppDelegate+notification.h, AppDelegate+notification.m, XTremePushPlugin.h, XTremePushPlugin.m  to your project.
8. Copy folder xtremepush to www/plugins folder.
9. To the file cordova_plugins.js add: 
  
  module.exports = [
        {
                      "file": "plugins/xtremepush/xtremepush.js",
                      "id": "XTremePush",
                      "clobbers": [
                                   "XTremePush"
                                  ]
        }
    ]

10.  In config.xml add path to your plugin file:
    <feature name="XTremePush">
      <param name="ios-package" value="XTremePushPlugin" />
    </feature>



[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/teZ-uG5S-jE/0.jpg)](http://youtu.be/teZ-uG5S-jE)


### 3. Connect your App to the XtremePush Platform <a name="connect_xtreme"></a>

1. Add your App on the platfrom by clicking "Integrate Push Features" on your XtremePush Dashboard at xtremepush.com
   
   ![Adding your app on the platform click integrate push features](http://cl.ly/image/050s3O0F2N2N/integrate_app.png)

2. Enter the App Name, upload the App icon, and give a short description of the app. An App key and token have been automatically generated. The App key is used in your iOS project to connect the app and platform. The app token will only be used if you use the external API.  Save your settings and copy  the app key. Your saved settings should be similar to the following.
   
   ![A saved apps settings on the platform](http://cl.ly/image/3I1K2V1t161l/app_saved.png) 
   

3. Return to your project in Xcode. In your Info.plist file add "XtremePushApplicationKey" in key field and your application key in value field. **You can find the "XtremePushApplicationKey" in the Settings (General Settings) page of your app under 'App Key' as seen above**.
   
   ![Where your App Key goes in your project](http://cl.ly/image/0U1r3V2h3P1p/added_key.png)

4. In your Info.plist file add "XtremePushSandoxMode" in key field and set it to YES if you want to use APNS in "Sandbox" gateway to quickly test with a DEBUG build of an app.

   ![Sandbox mode turned on below the App Key](http://cl.ly/image/1V2q251r0n1P/sandboxmode_yes.png)

   **NOTE: Sandbox Mode is only used for DEBUG builds compiled with a development mobile provisioning profile! For Ad Hoc and App Store builds, please, make sure to turn Sandbox Mode off by setting the value to NO or removing the key.**


5. Next return to your App Home on xtremepush.com and in Settings > Application Keys upload your app's iOS push notification certificates for development and production and save them. **If you need help creating these certs read our documentation on iOS Push Notification certificates [here](#certs)**
   
   ![Adding iOS push notification certs](http://cl.ly/image/3s0U0n3P1x3p/adding_certs.png)

6. In your Application Delegate, import XPush:

```objc
#import "XPush.h"
```

  And inside `applicationDidFinishLaunching:withOptions:` add these two method calls to set up XtremePush:

```objc
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.

[XPush applicationDidFinishLaunchingWithOptions:launchOptions];
```

   finally add the the three iOS remote notifications handling methods in Application delegate, and place the corresponding XtremePush method call in each of these as shown. **You are now ready to send your first push.**

To register for push notification call in your JS code after deviceReady event:

XTremePush.register(successCallback, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived"});

where callbackFunction is a javascript function which will be called when push notification will be received.

### 4. Sending your first Push <a name="first_push"></a>

1. To send a basic push go to your app home and select create campaign. The first step is to name your camapaign and add some content for the push. In this section you can also link to app pages, urls, or a custom html page for a richer push but for now we will just add text for a simple push.

   ![ Adding Content ](http://cl.ly/image/2S1f1R1K1J47/create_campaign_content.png)

2. Click next and you will be taken to the segments section. For your first push select broadcast to all users and refresh the number of addressable devices if you are using one development device this value should be one.
   
   ![ Selecting a segment ](http://cl.ly/image/0Q260e123V44/add_segment.png)

3. Click next and you will be taken to location, for your first push you will not be tieing it to a location so click next and you are taken to schedule. In schedule the default selection is Send Now and to test your first push you will want to keep it that way.

   ![Schedule your push](http://cl.ly/image/0r2913470O2g/add_schedule.png) 

4. Click next and you wil be taken to platform. Select iOS as your platform and add a badge count number if you want. If you are using a debug build with a development provision set environment to sandbox. If you are using a distribution build with a production provision set environment to production.  

   ![Configure your platform](http://cl.ly/image/1L0l0Z403H1k/push_add_platform.png)

5. You are almost ready to send your first push. Click preview, review your text and then hit send push. Your iphone will receive the push: 

   ![ Your First Push ](http://cl.ly/image/0d2H0t1v3I0A/first_push.png)


### 5. Tagging your app to enable deeper audience analysis and segmentation <a name="tagging"></a>

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

### 6. Custom Push Notification Sounds<a name="custom_sounds"></a>

To use custom push sounds you must add the sound files you wish to play to your project as a nonlocalized resource of the application bundle as shown below. For more on bundle structure see Apple's [Bundle Programming Guide](https://developer.apple.com/library/mac/documentation/corefoundation/conceptual/cfbundles/BundleTypes/BundleTypes.html)

![Custom Sounds Xcode](http://cl.ly/image/3s2u2f360w2d/custo_sound_xcode%20.png)

You can then use these sounds in your app home when creating a campaign. You simply enter the file name under iOS[Custom Sound] as shown below The audio data can be packaged in an aiff, wav, or caf file and for more on preparing custom alert sounds see Apple's [Local and Push Notification Programming Guide](https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/IPhoneOSClientImp.html).

![Using a Custom Sound xtremepush.com](http://cl.ly/image/2T3A1U3S3m1g/add_custom_sound.png)

### 7. Switching Off Location<a name="location_off"></a>


In the default configuration described above location is switched on to allow you to geofence regions related to your app in your app home on xtremepush.com. This allows you to analyse your audience and trigger pushes when they enter your locations. If you do not want these features location can be switched off using the following code in the application delegate.

```objc
#import "XPAppDelegate.h"
#import "XPush.h"

@implementation XPAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    
    // You must call the method to turn off location before `applicationDidFinishLaunchingWithOptions`
    [XPush setLocationEnabled:NO];

    [XPush applicationDidFinishLaunchingWithOptions:launchOptions];
    
    
    return YES;
}

```

and register application from your javascript side after deviceReady event:

XTremePush.register(successCallback, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived"});



### Appendix A: iOS Push Notification Certificates<a name="certs"></a>


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



## Appendix B: Method Summary for XtremePush Phonegap Library <a name="method_summary"></a>


### XtremePush Information Methods


XTremePush.version(successCallback, failCallback);

Returns the version of the XtremePush library. Used to determine the version of the XtremePush library you are using for example if you want to quickly output the the version to the debug area in xcode you would use:

--------------------------


XTremePush.deviceInfo(successCallback, failCallback);

Returns the XtremePush device identifier.  If you want to quickly output the the decice id to the debug area in Xcode you would use:

--------------------------


XTremePush.shouldWipeBadgeNumber(successCallback, failCallback);

Returns `YES` if badge number is wiped on app loading and `NO` otherwise.

--------------------------


XTremePush.isSandboxModeOn(successCallback, failCallback);

Returns `YES` if library using sandbox environment for push notifications and `NO` otherwise




### XtremePush Library Setup Methods



 XTremePush.setLocationEnabled(successCallback, failCallback, true);

Set `false` if location features of XtremePush are to be switched off. Must be called in didFinishLaunchingWithOptions method of your app delegate before you register the XtremePush library. Set `true` or do not call if you want to use location features. Call as follows to turn off Location features:

--------------------------

  
XTremePush.setShouldWipeBadgeNumber(successCallback, failCallback, true);

Set `true` if application badge number is to be wiped on app loading, otherwise set `false` or do not call. Must be called in didFinishLaunchingWithOptions method of your app delegate before you register the XtremePush library. 

--------------------------


XTremePush.unregister(successCallback, failCallback);

Called in your application if you want to unregister for remote notifications from the XtremePush platform.


### XtremePush Tagging methods


XTremePush.hitImpression(successCallback, failCallback, 'hitImpression');

Called where pages load to send impression to XtremePush server to track page impressions.

--------------------------


XTremePush.hitTag(successCallback, failCallback, 'hitTag');

Called where events occur to send tag to XtremePush server to track events.



