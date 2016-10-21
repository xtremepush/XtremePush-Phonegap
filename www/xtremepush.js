var exec = require('cordova/exec');

function XtremePush() { }

/**
 * Calling register function of the XtremePush plugin
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Object} options for method. Different for android and iOS:
 *         For iOS: 
 *           badge - is register push notification with badge
 *           sound - is register to push notifications with sound
 *           alert - is register to push notification with alert
 *           showAlerts - set if to show alert when received push notifications
 *         For Android:
 *           if locationTimeout,locationDistance, enableLocations, turnOnDebugLogs, setServerURL,
 *           beaconLocationBackground will be set PushConnector.register function will be called 
 *           with this specific configuration 
 *         Generic:
 *           callbackFunction - name of the function which will be called when push notification will be received
 *           
 */
XtremePush.prototype.register = function(success, fail, options) {
   return exec(success, fail, 'XtremePush', 'register', [options]);
};

/**
 * Calling hit tag function
 * @param  {String}   tag     value which will be sent
 */             
XtremePush.prototype.hitTag = function(tag){
   return exec(null, null, 'XtremePush', 'hitTag', [tag]);
};

/**
 * Calling hit tag function
 * @param  {String}   tag     value which will be sent
 * @param  {String}   message     associated with tag
 */             
XtremePush.prototype.hitTag = function(tag, message){
   return exec(null, null, 'XtremePush', 'hitTag', [tag, message]);
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
 */             
XtremePush.prototype.hitEvent = function(title){
   return exec(null, null, 'XtremePush', 'hitEvent', [title]);
};

/**
 * Calling hit event function
 * @param  {String}   title   title of the event
 * @param  {String}   message message of the event
 */             
XtremePush.prototype.hitEvent = function(title, message){
   return exec(null, null, 'XtremePush', 'hitEvent', [title, message]);
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
 * @param  {String}   id      ID to sent to Xtremepush
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

module.exports = new XtremePush();
