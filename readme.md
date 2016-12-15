# PhoneGap: Getting Started with Xtremepush
With the xtremepush module for Cordova you can add xtremepush to both iOS and Android apps built with Cordova or PhoneGap or Ionic.

## Platform Specific Information
Xtremepush supports push notifications for iOS devices via Apples's Push Notification Service (APNs for short). And push notifications for Android devices via Google’s GCM/FCM (Google/Firebase Cloud Messaging for Android) service. This page contains PhoneGap specific instructions but you will need to connect your iOS and Android app to the platform separately. 

**N.B.** To integrate successfully you will need to have your xtremepush App Key, APNS certs for iOS and, Google project number and GCM/FCM API key for Android. These are used to connect your app to the platform and the Apple and Google push services. You will find links to documentation on connecting to the platform below:

* Connecting your app to the platform
    * [iOS](https://support.xtremepush.com/hc/en-us/articles/205194411-Integrate-your-iOS-App-with-the-Platform-Objective-C-)
    * [Android](https://support.xtremepush.com/hc/en-us/articles/205144162-Integrate-your-Android-App-with-the-Platform)   
* Getting push certs or API keys  
	* [Setting up APNs Certificates for iOS](https://support.xtremepush.com/hc/en-us/articles/205115882-APNs-Certificates)
	* [GCM setup for Android ](https://support.xtremepush.com/hc/en-us/articles/205144182-GCM-API-Keys)

Full Cordova integration guides are given for iOS and Android below. 

## Installing the Xtremepush Plugin in your Project

1. In a terminal navigate to your project directory
2. From a terminal navigate to your application directory, and add the plugin with the following command: 
   * `cordova plugin add https://github.com/xtremepush/XtremePush-Phonegap`
3. Now edit your index.js file to register with the xtremepush server on a deviceready event, taking care to switch "*APP_KEY*" and "*GCM_PROJECT_NUMBER*" to actual values.

```js
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
    }
};

function registerXtremePush(){
    XtremePush.register({    
        appKey: "APP_KEY",
        debugLogsEnabled: true,
        pushOpenCallback: "onPushOpened",
        inappMessagingEnabled: true,
        ios: {
            pushPermissionsRequest: true,
            locationsEnabled: true,
            locationsPermissionsRequest: true
        },
        android: {
            gcmProjectNumber: "GCM_PROJECT_NUMBER",
            geoEnabled: true,
            beaconsEnabled: true
        }
    });
}

app.initialize()
```
 
## Tagging and events

Tagging and events can then be implemented in your app as follows:

* Sample button showing how to hitEvent:
   * `<button onclick="XtremePush.hitEvent('test-event')"> Hit Event </button>`
* Sample button showing how to hitTag: 
   * `<button onclick="XtremePush.hitTag('test-tag')"> Hit Tag </button>`
* Sample button showing how to call hitTagWithValue: 
   * `<button onclick="XtremePush.hitTag('test-tag-with-value', 'value')"> Hit Tag With Value </button>` 

If you have any difficulty in configuring the implementation, a sample app can be built using the following two files:

* [index.html](https://support.xtremepush.com/hc/en-us/article_attachments/209443849/index.html)
* [sample_index.js](https://support.xtremepush.com/hc/en-us/article_attachments/209443829/index.js)

A full list of our JavaScript functions and their parameters can be found in the [xtremepush.js](https://github.com/xtremepush/XtremePush-Phonegap/blob/master/www/xtremepush.js) plugin file.

## Advanced Options

### Attributions

If you would like xtremepush to collect IDFA/Ad ID and attribution data in your app, we have a git branch of the plugin that copies the required frameworks into your app. This can be installed using the following command:

cordova plugin add https://github.com/xtremepush/XtremePush-Phonegap#master+attributions

To enable the collection functionality, the attributionsEnabled parameter in the XtremePush.register() function must also be set with a value of true.


### Remove Location Services

If you would like to remove the geo-location and beacon frameworks and services from your app, please use the following command when installing the plugin:

cordova plugin add  https://github.com/xtremepush/XtremePush-Phonegap#master-geo-beacon

### Inbox 

The inbox feature can be turned on in your app by adding the following to your XtremePush.register() function:

* `inboxEnabled: true`

* Here is a sample button showing how to open inbox
    * `<button onclick="XtremePush.openInbox()">Open Inbox</button>`

If you want to use the inbox badge functionality please use the following in your XtremePush.register() function:

* `inboxBadgeCallback: "onInboxBadgeUpdate"`

* Here is a sample button showing how to retrieve the latest badge number:
    * `<button onclick="XtremePush.getInboxBadge()">Get Inbox Badge</button>`

 
### Other

For any further customisation of the plugin, please feel free to fork the GitHub repository and make any changes you like in your forked version, before adding the plugin to you app.
