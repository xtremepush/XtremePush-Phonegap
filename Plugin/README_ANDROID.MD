<span style="color: #0086cf;">Xtreme</span><span style="color: #787878;">Push</span> Android integration guide  
==================

[About](#about)

[Integrating](#integrating)

1.  [Download Library](#download)
2.  [Add Xtreme Push to your Project](#add_xtreme)
3.  [Connect your App to the XtremePush Platform](#connect_xtreme)
4.  [Sending your first Push](#first_push)
5.  [Tagging your app to enable deeper audience analysis and segmentation](#tagging)
6.  [Adjusting Location settings](#location_off)  

[Appendix A: Getting an API Key for Google Cloud Messaging](#keys)

<!--- 6.  [Custom Push Notification Sounds](#custom_sounds) -->
<!--- 7.  [Switching Off Location or adjusting Location frequency](#location_off) -->

<!--- [Appendix B: Method Summary for XtremePush Android Library](#method_summary) -->


## About
This document should provide all the info you need to integrate your Android app with the XtremePush plaform and send your first push via the platform.

## Integrating

### 1. Download Library <a name="download"></a> 


  1. Download the latest library version [here](https://github.com/xtremepush/XtremePush_Android/archive/master.zip). 

  2. Extract the archive, it contains the folder XtremePush_Android-master/ with the following folders and files:

  ![Android library files](http://cl.ly/image/221m1w3y3V11/android_folder_files.png)

### 2. Add Xtreme Push to your Project<a name="add_xtreme"></a> 
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

<uses-permission android:name="com.example.xtremepushtestapp.permission.C2D_MESSAGE" />

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

4. Add XTremePushPlugin.java to your project.
5. Copy folder xtremepush to www/plugins folder.
6. To the file cordova_plugins.js add: 
	
	module.exports = [
        {
                      "file": "plugins/xtremepush/xtremepush.js",
                      "id": "XTremePush",
                      "clobbers": [
                                   "XTremePush"
                                  ]
        }
    ]

7.  In res/xml/config.xml add path to your plugin file:
	<feature name="XTremePush">
        <param name="android-package" value="xtreme.plugins.push.XTremePushPlugin" />
    </feature>

### 3. Connect your App to the XtremePush Platform <a name="connect_xtreme"></a> 

1. Add your App on the platfrom by clicking "Integrate Push Features" on your XtremePush Dashboard at xtremepush.com
   
   ![Adding your app on the platform click integrate push features](http://cl.ly/image/050s3O0F2N2N/integrate_app.png)

2. Enter the App Name, upload the App icon, and give a short description of the app. An App key and token have been automatically generated. The App key is used in your Android project to connect the app and platform. The app token will only be used if you use the external API.  Save your settings and *copy the app key*. Your saved settings should be similar to the following.
   
   ![A saved apps settings on the platform](http://cl.ly/image/3I1K2V1t161l/app_saved.png) 
   
3. Still in your App Home on xtremepush.com go to Settings > Application Keys 
and copy your API Key for Google Cloud Messaging into *Android Application Key* and click *save*. If you don't now where to get this key please read our documentation on *Getting an API Key for Google Cloud Messaging* [here](#keys) 

   ![Adding the API key](http://cl.ly/image/1U1N022r1B32/adding_API_key.png)


4. Next you will use your app key from the *Settings > General Settings* section of your app home on xtremepush.com and your project number from the google developer console to connect your app to the platform. If you don't know where to get the project number please refer to our documentation on *Getting an API Key for Google Cloud Messaging* [here](#keys) 

5. Return to your project in your IDE. In XTremePushPlugin, please fill properties with Xtreme Application Id and Google project Id:

private static String AppId = "Your Xtreme Applciation Id";
private static String GoogleProjectID = "Your google project Id";

6. To register for push notification use in your JS code after deviceReady event:

XTremePush.register(successCallback, failCallback, {callbackFunction:"onPushReceived", locationDistance:30, locationTimeout:50})

callbackFunction property sets javascript method which will be called when push notification will be received.

locaionDistance and locationTimeout are optional parameters in case you want to use push notifications with location enabled features. Please refere to [Adjusting Location settings](#location_off)  for more information.

**You are now ready to send your first push.**

### 4. Sending your first Push <a name="first_push"></a> 

1. To send a basic push go to your app home and select create campaign. The first step is to name your camapaign and add some content for the push. In this section you can also link to app pages, urls, or a custom html page for a richer push but for now we will just add text for a simple push.

   ![ Adding Content ](http://cl.ly/image/2S1f1R1K1J47/create_campaign_content.png)

2. Click next and you will be taken to the segments section. For your first push select broadcast to all users and refresh the number of addressable devices if you are using one development device this value should be one.
   
   ![ Selecting a segment ](http://cl.ly/image/0Q260e123V44/add_segment.png)

3. Click next and you will be taken to location, for your first push you will not be tieing it to a location so click next and you are taken to schedule. In schedule the default selection is Send Now and to test your first push you will want to keep it that way.

   ![Schedule your push](http://cl.ly/image/0r2913470O2g/add_schedule.png) 

4. Click next and you wil be taken to platform. Select Android as your platform. 

   ![Configure your platform](http://cl.ly/image/2q2P2U141M1o/push_add_android.png)

5. You are almost ready to send your first push. Click preview, review your text and then hit send push. Your Android device will receive the push: 

   ![ Your First Push ](http://cl.ly/image/3i1g2L3L321B/first_push_android.png)

### 5. Tagging your app to enable deeper audience analysis and segmentation <a name="tagging"></a> 

XtremePush has two methods for tagging activity in your app one for tagging page impressions and one for tagging any other activity.

To tag page impression call the `pushConnector.hitImpression(String tag)` method  where your page loads after deviceReady event:
		
```javascript
XTremePush.hitImpression(successCallback, failCallback, "your_impression_name");
```

To tag any other events call the `pushConnector.hitTag(String tag)` method where that event occurs:
    
        XTremePush.hitTag(successCallback, failCallback, "your_tag_name");
        

<!--- ### 6. Custom Push Notification Sounds <a name="custom_sounds"></a> -->

<!--- ### 6. Switching Off Location or adjusting Location frequency <a name="location_off"></a> 

In the default configuration described above location is switched on to allow you to geofence regions related to your app in your app home on xtremepush.com. This allows you to analyse your audience and trigger pushes when they enter your locations. If you do not want these features location can be switched off using the following code ???????

-->


### 6. Adjusting Location settings <a name="location_off"></a>
If you want to keep the location features but want to manage the trade off between the sensitivity of location detection and the impact on battery life then you have the option to  set the frequency of location updates. This can be done by setting an additional two values when initialisng XtremePush in the onCreate method of your main activity. These are *locationCheckTimeout* and *locationDistance* :
		
`init(FragmentManager fm, String appKey, String projectNumber, int locationCheckTimeout, float locationDistance)`.
	
Set *locationCheckTimeout* to your desired location update frequency in minutes and *locationDistance* to desired updates frequency in meters. If you set *locationCheckTimeout* to *30* and  *locationDistance* to *500* then location will be updated every 30 minutes or if the device moves 500 meters depending on which occurs first. You would initialise this setup as follows:

```java
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
	
		pushConnector = PushConnector.init(getSupportFragmentManager(), "XPUSH_APP_KEY", "GOOGLE_PROJECT_NUMBER", 30, 50 );
```

<!--- ### 8. Adding a push notification inbox <a name="inbox"></a> 

##How to start Inbox activity		


if you want to start Inbox activity, you should use the following code snippet(activity must be registred in manifest file)
 
 		startActivity(this, XPushLogActivity.class);
 --->


## Appendix A: Getting an API Key for Google Cloud Messaging <a name="keys"></a> 
To integrate XtremePush with an Android app you need to upload your GCM API key to your app dashboard on xtremepush.com. This is because to send push notifications to Android devices, you need to set up a Google API Project, enable the GCM service and otain an API key for it.
In this section we will summarise the main steps involved. You can also find Google's own guide to setting up a Google API Project and otaining an API key for the GCM service [here](http://developer.android.com/google/gcm/gs.html).

The first step is to:
- Go to: https://console.developers.google.com/
- Log in
- Create a project for your app if you don't already have one

![Google Developer Console API Project](http://cl.ly/image/1a3x3L0M0U2A/google_dev_console.png)

Next click on your project and you will be taken to your project home. Your project Number is diplayed on top of this page you will need that later to integrate your app with XtremePush but first you must select *APIs & auth* to enable GCM.

![Google Developer Console Project Home](http://cl.ly/image/1M1b3M2O1324/google_dev_console_home.png)

In *APIs & auth > API* scroll down until you find Google Cloud Messaging for Android and switch it on:

![Google Developer Console GCM ON](http://cl.ly/image/2H2F1N180q1K/google_dev_gcm_on.png)

In the sidebar on the left, select *APIs & auth > Credentials*. On the right under  *Public API access* , click *Create New Key* and select *Server Key*. Do not specify any ip address and click *Create*.

![Generate API Key](http://cl.ly/image/1Q2G372k0w2w/google_dev_new_key.png)

Copy the new API Key you are given under *Public API Access > Key for server applications* in.  Log in to your XtremePush dashboard on xtremepush.com. Go to your app home and navigate to *Settings > Application Keys* and select Android App. Paste the key into *Android Application Key* and click *save*.

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