<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Phonegap Plugin
==================

[About](#about)

[Phonegap - iOS integration guide](#ios_guide)

[Phonegap - Android integration guide](#android_guide)

##   About <a name="about"></a>

The XtremePush phonegap plugin has two main folders:

* *Example_Apps/* : This folder contains a sample app for each OS supported. The apps are in a folder named after the OS they are related to, for example the iOS example app is in the *ios/* folder. 
* *Plugin/*: This folder contains the javascript part of the plugin in a folder called *JS/*  and the OS specific portion of the plugin in a folder named after that OS i.e. *iOS/* or *Android/*.

Full integration guides are given for iOS and Android below.




<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Phonegap iOS integration guide  <a name="ios_guide"></a> 
==================

[About](#ios_about)

[Integrating](#ios_integrating)

1.  [Adding Library to the Project](#ios_download)
2.  [Download phonegap plugin](#ios_download_plugin)
3.  [Add phonegap plugin](#ios_add_plugin)
4.  [Connect your App to the XtremePush Platform](#ios_connect_xtreme)
5.  [Tagging your app to enable deeper audience analysis and segmentation](#ios_tagging)

[Phonegap Example App](#ios_example_app)

[Appendix A: Method Summary for XtremePush Phonegap Plugin](#ios_method_summary)
  


##   About <a name="ios_about"></a>
This section should provide all the info you need to integrate your iOS app with the XtremePush platform and send your first push via the platform.

## Integrating <a name="ios_integrating"></a>

### 1. Adding Library to the project <a name="ios_download"></a>

Please follow [instructions](https://github.com/xtremepush/XtremePush_iOS)

### 2. Download phonegap plugin <a name="ios_download_plugin"></a>

1. Download the latest plugin version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder *XtremePush_phonegap/Plugin* with the following two folders relevant to the iOS phonegap integration:
    * *src/ios/*: Contains the required  .h and .m files of the plugin. 
    * *www/*: Contains the .js file of the plugin that goes in the www folder.

   Note: folders with the names of other OSes can be ignored, there is also an example iOS app in *Example_Apps/android*
 
### 3. Add phonegap plugin <a name="ios_add_plugin"></a>

Manually Installation:

1. Add AppDelegate+notification.h, AppDelegate+notification.m, XTremePushPlugin.h, XTremePushPlugin.m  to the plugins/ folder of your project in Xcode (found in the src/ios/ folder).

2. In the *www/* folder of the plugin you downloaded you will find the folder *xtremepush/* containing "xtremepush.js" the javascript file that allows you to use xtremepush methods in your phonegap project. Copy the folder *xtremepush/* to the *www/plugins/* folder of your project.

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
  

### 4. Connect your App to the XtremePush Platform <a name="ios_connect_xtreme"></a>

Please follow documentation [here](https://xtremepush.com/docs/libs/ios_start/).

Automatic adding of the Plugin

In the folder with your PhoneGap aplication please use command:

cordova plugins add %extracted_folder%/Plugin

Build your application using command:

cordova build ios

### 5. Tagging your app to enable deeper audience analysis and segmentation <a name="ios_tagging"></a>

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
```

## Phonegap Example App <a name="ios_example_app"></a>

1. When you download the latest plugin version ([here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip)) it comes with example apps. 

2. Extract the archive, it contains the folder *XtremePush_phonegap/* and the iOS example app can be found in *Example_Apps/ios/*

3. To experiment  with this sample app open it in Xcode. It is a simple app with all the XtremePush library methods mapped to a series of buttons:

![Phonegap example app](http://cl.ly/image/133e063D0z41/cordova_test_app.png)

4. You can see how the plugin has been configured in the test app in key locations such as: the plugin folders, app delegate, cordova_plugins.js, config.xml, index.js and index.html (see image below).

![Test app in Xcode](http://cl.ly/image/3V3c2t2t0F2J/test_app_xcode%20_phonegap_ios.png)

## Appendix A: Method Summary for XtremePush Phonegap Plugin <a name="ios_method_summary"></a>


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

1.  [Adding Library to your Project](#android_download)
2.  [Add XtremePush to your Project](#android_add_xtreme)  
3.  [Download phonegap plugin](#android_download_plugin)
4.  [Add phonegap plugin](#android_add_plugin)
5.  [Tagging your app to enable deeper audience analysis and segmentation](#android_tagging)

[Phonegap Example App](#android_example_app)

## About <a name="android_about"></a> 
This section should provide all the info you need to integrate your Android app with the XtremePush platform and send your first push via the platform.

## Integrating <a name="android_integrating"></a> 

### 1. Adding Library to your Project <a name="android_download"></a> 

Please follow [instructions](https://xtremepush.com/docs/libs/android_start/)

### 2. Download phonegap plugin <a name="android_download_plugin"></a>

1. Download the latest plugin version [here](https://github.com/xtremepush/XtremePush_iOS/archive/master.zip). 

2. Extract the archive, it contains the folder *XtremePush_phonegap/Plugin* with the following two folders relevant to the android phonegap integration:
    * *src/android/*: Contains the required  .java file of the plugin - *XTremePushPlugin.java*. 
    * *www/*: Contains the .js file of the plugin that goes in the www folder.

   Note: folders with the names of other OSes can be ignored, there is also an example Android app in the folder Example_Apps/android
 
### 3. Add phonegap plugin <a name="android_add_plugin"></a>

Manuall Installation:

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
  
4. Finally In *res/xml/config.xml* add the path to your plugin file: 

```xml
  <feature name="XTremePush">
        <param name="android-package" value="YOUR_PACKAGE.XTremePushPlugin" />
    </feature>
```

Automatic adding of the Plugin

In the folder with your PhoneGap aplication please use command:

cordova plugins add %extracted_folder%/Plugin

Build your application using command:

cordova build android


### 4. Connect your App to the XtremePush Platform <a name="android_connect_xtreme"></a> 

Please follow instruction [here](https://xtremepush.com/docs/libs/android_start/)

### 5. Tagging your app to enable deeper audience analysis and segmentation <a name="android_tagging"></a> 

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
