# PhoneGap: Getting Started with XtremePush
With the XtremePush module for PhoneGap you can add XtremePush to both iOS and Android apps built with PhoneGap.

## Platform Specific Information
XtremePush supports push notifications for iOS devices via Apples's Push Notification Service (APNs for short). 
And push notifications for Android devices via Google’s GCM (Google Cloud Messaging for Android) service. This page contains PhoneGap specific instructions but you will need to connect your iOS and Android app to the platform separately. 

**N.B.** To integrate successfully you will need to have your XtremePush App Key, APNS certs for iOS and, Google project number and GCM API key for Android. These are used to connect your app to the platform and the Apple and Google push services. You will find links to documentation on connecting to the platform below:

* Connecting your app to the platform
    * [iOS](https://xtremepush.com/docs/libs/ios_start/#connect-your-app-to-the-xtremepush-platform)
    * [Android](https://xtremepush.com/docs/libs/android_start/#connect-your-app-to-the-xtremepush-platform)   
 * Getting push certs or API keys  
	* [Setting up APNs Certificates for iOS](https://xtremepush.com/docs/libs/ios_start/#setting-up-apns-certificates)
	*  [GCM setup for Android ](https://xtremepush.com/docs/libs/android_start/#gcm-setup)

Full Cordova Phonegap integration guides are given for iOS and Android below.


### Integrate Xtremepush with your PhoneGap iOS Project

1. In a terminal navigate to your PhoneGap project directory

2. From a terminal navigate to your application directory, and type the command:
    
        cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap

3. Follow the native iOS instructions to connect you iOS app to the platform - [here](https://xtremepush.com/docs/libs/ios_start/#connect-your-app-to-the-xtremepush-platform)

    * After this step is complete you should have added your *XtremePushApplicationKey*  to your info.plist (*platforms/ios/YOURAPP/Resources/YOURAPP-info.plist*) and also*NSLocationAlwaysUsageDescription* if you are using location. 

4. To enable XtremePush Notifications you must modify your application delegate. Follow these three steps to complete the integration process:

    * In your Application Delegate (*platforms/ios/YOURAPP/Classes/AppDelegate.m*), import XPush:
     
            #import "XPush.h"



     * **For iOS 8** inside `applicationDidFinishLaunching:withOptions:`  add the following code to set up XtremePush:


            - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
		    {
    		 // Override point for customisation after application launch.

               // Setup XtremePUSH    
	        NSInteger types;

                if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.f) {
    		types = UIUserNotificationTypeBadge | UIUserNotificationTypeAlert | UIUserNotificationTypeSound;
               } else {
    		types = UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeBadge;
    		}
		
              [XPush registerForRemoteNotificationTypes:types];

              [XPush applicationDidFinishLaunchingWithOptions:launchOptions];


         **For iOS 7 and  before** `applicationDidFinishLaunching:withOptions:`  add these two method calls to set up XtremePush:


		    - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
		    {
    		// Override point for customisation after application launch.
    
		    // Two Calls to setup XtremePUSH
		    [XPush registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeBadge];

		    [XPush applicationDidFinishLaunchingWithOptions:launchOptions];

    * Finally add the the three iOS remote notifications handling methods in Application delegate, and place the corresponding XtremePush method call in each of these as shown. 

		    - (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
		    [XPush applicationDidRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
		    }

		    - (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
		    BOOL showAlert = YES;//should or not library shows alert for this push. 
		    [XPush applicationDidReceiveRemoteNotification:userInfo showAlert:showAlert];
		    }

		    - (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
		    [XPush applicationDidFailToRegisterForRemoteNotificationsWithError:error];
		    }

        
5. If you are building for Android now complete the Android specific portion of the integration. And then you can complete the integration using the Javascript API. 


    
### Integrate Xtremepush with your PhoneGap Android Project

1. In a terminal navigate to your PhoneGap project directory


2. From a terminal navigate to your application directory, and type the command below (Note: You may have already completed this step if you have completed the iOS instructions above):

        cordova plugin add https://github.com/xtremepush/XtremePush_Phonegap
    
3. Follow the native Android instructions to connect your Android app to the platform - [here](https://xtremepush.com/docs/libs/android_start/#connect-your-app-to-the-xtremepush-platform)

	* After this step is complete you should have your *XtremePush APP Key* and your *Google Project Number* ready to use in the next step.

4. To enable XtremePush Notifications you must modify your XTremePushPlugin.java file which is located in PROJECT_DIRECTORY/platforms/android/src/com/xtreme/plugins/

    * Replace *Your application ID* in the line `private static String AppId = "Your application ID";` with your XtremePush App Key

    * Replace *Your Google Project ID* in the line `private static String GoogleProjectID = "Your Google Project ID";` with your Google developer project number

You are now ready to integrate the JavaScript API into your Cordova www directory

## Integrating the JavaScript API in your application
In order to use the JavaScript API in your Cordova application, you need to add the API calls in your apps main .js file. 

At a minimum to complete a basic integration you should register XtremePush in the *onDeviceReady* function of your main .js file. An example of how to do this is given in a sample index.js and index.html below. In order to use the JavaScript API from your cordova application, the `XTremePush.register()` function of the plugin *must* be called when a `deviceready` event is triggered.

In the example below the *register* API method is made available via the function *registerXtremePush* and this is called in *onDeviceReady*. *Success*, *fail*, and *onPushReceived* callbacks are also added. After you have configured registration in your main .js file you are ready to test sending push notifications. How to send a push is covered in the next section.

In the example given below the deviceInfo, hitTag and hitImpression methods are also used. These methods and their usage are described in more detail in the following sections. 



### index.js

```javascript
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        registerXtremePush();
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

function registerXtremePush(){
    XTremePush.register(successRegister, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived"});
}

function deviceInfo(){
    XTremePush.deviceInfo(successCallback, failCallback);
}

function sampleButton(){
    //Button Functionality
    hitTag("sample");
}

function hitTag(tag){
    XTremePush.hitTag(successCallback, failCallback, tag);
}

function hitImpression(impression){
    XTremePush.hitImpression(successCallback, failCallback, impression);
}

// Simple call back function that displays success result
function successRegister(result){
    app.receivedEvent('registered');
}

// Simple call back function that displays success result
function successCallback(result){
    alert('Success callback : ' + JSON.stringify(result));
}

// Simple call back function that displays failure result
function failCallback(result){
    alert('Fail callback : ' + JSON.stringify(result));
}

// Simple call back that displays data when a push is received
function onPushReceived(data){
    alert('Push received : ' + JSON.stringify(data));
}

app.initialize();
```

### index.html

```html
<!DOCTYPE html>
<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->
<html>
    <head>
        <!--
        Customize this policy to fit your own app's needs. For more guidance, see:
            https://github.com/apache/cordova-plugin-whitelist/blob/master/README.md#content-security-policy
        Some notes:
            * XtremePush requires some open permissions here so that tagging communication with server works
        -->
        <meta http-equiv="Content-Security-Policy" content="default-src *; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline' 'unsafe-eval'">
        <meta name="format-detection" content="telephone=no">
        <meta name="msapplication-tap-highlight" content="no">
        <meta name="viewport" content="user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width">
        <link rel="stylesheet" type="text/css" href="css/index.css">
        <title>Hello World</title>
    </head>
    <body>
        <div class="app">
            <h1>Apache Cordova</h1>
            <div id="registered" class="blink">
                <p class="event listening">Connecting to XtremePush</p>
                <p class="event received">Registered with XtremePush</p> 
            </div>
            <button onclick="sampleButton()">Sample</button>
        </div>
        <script type="text/javascript" src="cordova.js"></script>
        <script type="text/javascript" src="js/index.js"></script>
    </body>
</html>

```

### Building your application

Currently, the plugin uses Apache Ant for compilation of the Android project. If you have upgraded your Cordova Android system to version 4.0+ this can be manually enabled by setting the following environment variable: `ANDROID_BUILD=ant` Once that variable has been configured, your project can be run normally, i.e. `cordova run android`

You are now ready to send your first push message.

<br>

##  Sending a Push
When you have the basic integration complete you should attempt to send a push. To quickly send a push notifications to an app's users click *+ CREATE CAMPAIGN* on that app's summary screen

After clicking *+ CREATE CAMPAIGN* you will be taken to *App Home > Create Campaign Home*

<img alt="App Home > Create Campaign Home" src="http://cl.ly/image/0U2j0P0T3K1p/Create_Campaign_Selection.png" width="650">

In just a few short steps you can send push notifications from here:

1. On the Create Campaign Home page click on *Push Message Campaign* and you will be taken to the campaign creation work flow.

2. In the opening section of create campaign you can give your campaign a title and add content. The first step is to name your campaign and add the text  of the push notification. You can also link to urls, a custom html page, or use payloads to ad custom behaviour such as linking to a specific app page for a richer push but for now just add text for a simple push.


3. Click next and you will be taken to the segments section. In this example we select broadcast to all users and refresh the number of addressable devices if you are working with one development device this value will be one. When your app is live this number will be larger. If you are interested in targeting a subsection of  your audience see the documentation on [segmentation](../platform/segmentation.md)
   
	<img alt="Broadcast to all campaign selected" src="http://cl.ly/image/3F11372C0x08/Segment_Broadcast_to_all.png" width="650">  	   

4. Click next and and you are taken to schedule. In schedule the default selection is Send Now and for this example we will keep it that way. 

	<img alt="Send Now campaign selected " src="http://cl.ly/image/333d2j2T3o1W/Schedule%20_Send_Now.png" width="650">  

5. Click next and you will be taken to platform. Here you can choose to send to iOS or Android devices, or both if you have integrated your app for both and want to send the same message to both.

	<img alt="Configure your platform" src="http://cl.ly/image/3J3b463w2F3B/Campaign_platform.png" width="650"> 

6. You now ready to send a push. It will be sent to all devices on the selected platforms immediately. Click preview, review your text and then hit send push. Your devices will receive the push: 

<img alt="Your First Push" src="http://cl.ly/image/0d2H0t1v3I0A/first_push.png" width="250"> 

Test Push on an iPhone lock screen  


## JavaScript API

The JavaScript API follows an asynchronous callback approach with hooks for events such as Push registration and incoming messages. 

<br>

 -  **success** *Function* 
 
       Callback function which will be called in case of success of the function
       
 -  **fail**  *Function*
 
       Callback function which will be called in case of failure
       
  - **options** *Object*  
       Options for this method. Common options and iOS or android specific options.
       
       iOS:
        
      * **badge** - register for push notification with badge
      * **sound** - register for push notifications with sound
      * **alert** -  register for push notification with alert
      * **showAlerts** - set to show alert when  push notifications are received when the app is opened

      Android:
      
      * **enableLocations** - set true to enable location services on Android

            	 
       Common:
       
       * **callbackFunction** - name of the function which will be called when push notification will be received


Registers device for push notifications.


#### unregister(success, fail)

 -  **success** *Function* 
 
       Callback function which will be called in case of success of the function
       
 -  **fail**  *Function*
 
       Callback function which will be called in case of failure 

  
 De-registers device for push notifications

<br>



#### deviceInfo(success, fail)

 -  **success** *Function* 
 
       Callback function which will be called in case of success of the function
       
 -  **fail**  *Function*
 
       Callback function which will be called in case of failure 
       
Returns device information. Used to retrieve your XtremePush ID. If you successfully retrieve the ID it can be used to identify your device on the platform and to send a push notification to just that device.

<br>


 
#### hitTag(success, fail, tag)
 
 -  **success** *Function* 
 
       Callback function which will be called in case of success of the function
       
 -  **fail**  *Function*
 
       Callback function which will be called in case of failure 

 - **tag** *String*    Descriptive value sent to server to mark the occurrence of an in app event.

Used to tag in app behaviour in your Phonegap App. 

<br>

#### hitImpression(success, fail, impression)
 
 -  **success** *Function* 
 
       Callback function which will be called in case of success of the function
       
 -  **fail**  *Function*
 
       Callback function which will be called in case of failure 

 - **impression** *String*    Unique value sent to server to mark the occurrence of a page impression

Used to mark a page impression on a page in your Phonegap App.    

## Using the Javascript API

To make the javascript API calls available in your HTML pages you can for example add them to your index.js or similar as shown in the example below:

```javascript
function deviceInfo(){
    XTremePush.deviceInfo(successCallback, failCallback);
}

function hitTag(tag){
    XTremePush.hitTag(successCallback, failCallback, tag);
}

function hitImpression(Impression){
    XTremePush.hitImpression(successCallback, failCallback, impression);
}


// Simple call back function that displays success result
function successCallback(result){
    alert('Success callback : ' + JSON.stringify(data));
}

// Simple call back function that displays failure result
function failCallback(result){
    alert('Fail callback : ' + JSON.stringify(data));
} 
```
 
### Event Tagging for Event-based Analytics and Push

XtremePush has two methods for tagging activity in your app one for tagging page impression and one for tagging any other events for analytics.

To tag events for analytics and have the ability to segment campaigns based on tag behaviour call the `hitTag` method where that event occurs for example:

        <script type="text/javascript">
            function homeBtnFn(){
				hitTag("home_btn");
				//Button Functionality
            }
			
        </script>

	<button onclick="homeBtnFn()">Home</button> 

<br>

### Special Tag Syntax: ON/OFF Flag

You can use a special syntax on your tag if you wish to track setting a Flag ON/OFF. A good example of when you might want to do this is if you have a push notification preferences page as in the example shown below.


<img alt="Flag setting example" src="http://cl.ly/image/021Y1f2D1t1p/2015-02-11%2010.13.41.png" width="250">

In the example shown you can set a flag on when the user selects Notify about full time scores and off if the user deselects the option. You would set/unset it using the hitTag method call with the following @on: / @off: syntax.


    function setPreference(){
				
			    var chkBox = document.getElementById('myonoffswitch');
			    if (chkBox.checked)
			    {
			        // Set full_time_score flag ON
			        hitTag("@on:full_time_score");
			    }
				else {
				       // Set full_time_score flag OFF 
					hitTag("@off:full_time_score");
				} 
				
            }

Any flags being set in your app using this approach can be targeted by creating a segment. From the example above to send messages to the users who have subscribed for full time score updates you would create: 

* A segment called  Full Time Score Subscribers
* With the condition *"Flag full_time_score Is on"*
* Add this segment to your full time score notifications


Below you can see an example of this segment being created in the segment manager on the platform. More information on segmentation can be found in the [segmentation docs](../platform/segmentation.md)
 
<img alt="Using a Custom Sound xtremepush.com]" src="http://cl.ly/image/0j3G112C1L06/Screen%20Shot%202015-02-11%20at%2012.32.09.png" width="650">



<br>
<br>








