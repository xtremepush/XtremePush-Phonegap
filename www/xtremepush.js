var exec = require('cordova/exec');

function XtremePush() { }

/**
 * Calling register function of the XtremePush plugin
 * @param  {Object} options   JSON object containing the options for which bit of Xtremepush functionality
 *                            to turn on/off
 *
 * Sample format:
 *    {
 *        appKey: "asdjfaSDFASfjkfasd",
 *        pushOpenCallback: "onPushOpened",
 *        inappMessagingEnabled: true,
 *        ios: {
 *            locationsEnabled: true,
 *        },
 *        android: {
 *            gcmProjectNumber: "012345678910",
 *            geoEnabled: true,
 *            beaconsEnabled: true
 *        }
 *    }
 *
 * Description:
 *    appKey - String (*required*) Key for Xtremepush project
 *    debugLogsEnabled - Boolean (optional) to turn on debug logs
 *    impressionsBatchingEnabled - Boolean (optional) to turn on caching impressions and batch sending when app closing
 *    impressionsStoreLimit - Integer (optional) size for impression cache
 *    inappMessagingEnabled - Boolean (optional) to turn on sending an event on app start
 *    inboxBadgeCallback - String (optional) JavaScript function to be executed when the badge is updated
 *    inboxEnabled - Boolean (optional) to turn on inbox functionality in the app
 *    pushOpenCallback - String (*required*) JavaScript function to be executed when a message is opened
 *    serverUrl - String (optional) to send all data to different Xtremepush API endpoint
 *    tagsBatchingEnabled - Boolean (optional) to turn on caching tags and batch sending when app closing
 *    tagsStoreLimit - Integer (optional) size for tag cache
 *    sessionsStoreLimit - Integer (optional) size for session event cache
 *    attributionsEnabled - Boolean (optional) *(requires plugin from master+attributions branch)* to turn on collection of IDFA, Ad ID and attribution information
 *    ios: 
 *        locationsEnabled - Boolean (optional) Can be used to turn on Xtremepush location and iBeacon scanning functionality
 *        locationsPermissionsRequest - Boolean (optional) Can be used to prevent automatically showing the location permission request dialog
 *        nameCollectingEnabled - Boolean (optional) Can be used to turn off collection of the device name, e.g. "John's iPhone"
 *        pushPermissionsRequest - Boolean (optional) Can be used to prevent automatically showing the notification permission request dialog
 *        shouldWipeBadgeNumber - Boolean (optional) Badge of application gets cleared on app open
 *    android:
 *        beaconsEnabled - Boolean (optional) Can be used to turn on Xtremepush iBeacon scanning functionality
 *        gcmProjectNumber - String (optional) If push is used in your application, set the GCM/FCM project number here
 *        geoEnabled - Boolean (optional) Can be used to turn on Xtremepush geo-location functionality
 *        locationsPermissionsRequest - Boolean (optional) Can be used to prevent automatically showing the location permission request dialog
 */
XtremePush.prototype.register = function(options) {
   return exec(null, null, 'XtremePush', 'register', [options]);
};

/**
 * Calling hit tag function
 * @param  {String}   tag     value which will be sent
 * @param  {String}   value   associated with tag (optional)
 */             
XtremePush.prototype.hitTag = function(tag, value) {
   if (value) {
      return exec(null, null, 'XtremePush', 'hitTag', [tag, value]);
   } else {
      return exec(null, null, 'XtremePush', 'hitTag', [tag]);
   }
};

XtremePush.prototype.registerWithToken = function(token) {
   return exec(null, null, 'XtremePush', 'registerWithToken', [token]);
};

/**
 * Calling hit impression function
 * @param  {String}   impression value which will be sent
 */             
XtremePush.prototype.hitImpression = function(impression){
   return exec(null, null, 'XtremePush', 'hitImpression', [impression]);
};

/**
 * Calling hit event function
 * @param  {String}   title   title of the event
 * @param  {String}   value   value of the event (optional)
 */             
XtremePush.prototype.hitEvent = function(title, value){
   if (value) {
      return exec(null, null, 'XtremePush', 'hitEvent', [title, value]);
   } else {
      return exec(null, null, 'XtremePush', 'hitEvent', [title]);
   }
};

XtremePush.prototype.hitEventWithValue = function(id){
   return exec(null, null, 'XtremePush', 'hitEventWithValue', [id]);
};


XtremePush.prototype.setUser = function(id){
   return exec(null, null, 'XtremePush', 'setUser', [id]);
};

XtremePush.prototype.setTempUser = function(id){
   return exec(null, null, 'XtremePush', 'setTempUser', [id]);
};

/**
 * Calling send tags function
 */             
XtremePush.prototype.sendTags = function(){
   return exec(null, null, 'XtremePush', 'sendTags',[]);
};

/**
 * Calling send impressions function
 */             
XtremePush.prototype.sendImpressions = function(){
   return exec(null, null, 'XtremePush', 'sendImpressions', []);
};

/**
 * Setting an external ID for the app user
 * @param  {String}   id      ID to send to Xtremepush
 */ 
XtremePush.prototype.setExternalId = function(id){
   return exec(null, null, 'XtremePush', 'setExternalId', [id]);
};

/**
 * Setting the subscription status of the app
 * @param  {bool}     subscription   true to enable subscription
 */ 
XtremePush.prototype.setSubscription = function(subscription){
   return exec(null, null, 'XtremePush', 'setSubscription', [subscription]);
};

/**
 * Calling function to open the app inbox
 */             
XtremePush.prototype.openInbox = function(){
   return exec(null, null, 'XtremePush', 'openInbox', []);
};

/**
 * Retrieve the inbox badge number, will be returned to the function set above for inboxBadgeCallback
 */             
XtremePush.prototype.getInboxBadge = function(){
   return exec(null, null, 'XtremePush', 'getInboxBadge', []);
};

/**
 * Returns device information
 * @param  {Function} success callback function which will be called in case of success of the function
 */              
XtremePush.prototype.deviceInfo = function(success){
   return exec(success, null, 'XtremePush', 'deviceInfo',[]);
};

/**
 * Calling function to trigger the system permissions dialog for location services 
 */ 
XtremePush.prototype.requestLocationsPermissions = function(){
   return exec(null, null, 'XtremePush', 'requestLocationsPermissions', []);
};

/**
 * Calling function to trigger the system permissions dialog for notifications (iOS only) 
 */ 
XtremePush.prototype.requestPushPermissions = function(){
   return exec(null, null, 'XtremePush', 'requestPushPermissions', []);
};

/**
*  Calling function to show notification
*/
XtremePush.prototype.showNotification = function(id){
   return exec(null, null, 'XtremePush', 'showNotification', [id]);
};

/**
 * Calling function to open click in message
 */             
XtremePush.prototype.clickMessage = function(id, action){
   return exec(null, null, 'XtremePush', 'clickMessage', [id, action]);
};

/**
 * Calling function to report message clicked
 */
XtremePush.prototype.reportMessageClicked = function(id, action){
   return exec(null, null, 'XtremePush', 'reportMessageClicked', [id, action]);
};

/**
 * Calling function to report message dismissed
 */
XtremePush.prototype.reportMessageDismissed = function(id){
   return exec(null, null, 'XtremePush', 'reportMessageDismissed', [id]);
};

XtremePush.prototype.onConfigChanged = function(){
   return exec(null, null, 'XtremePush', 'onConfigChanged', []);
};

XtremePush.prototype.unregisterForRemoteNotifications = function(){
   return exec(null, null, 'XtremePush', 'unregisterForRemoteNotifications', []);
};

module.exports = new XtremePush();
