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
 * Unregister from push notifications
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */
XtremePush.prototype.unregister = function(success, fail){
   return exec(success, fail, 'XtremePush', 'unregister',[]);
};

/**
 * Only for iOS. Returns if application is configured for development mode
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */           
XtremePush.prototype.isSandboxModeOn = function(success, fail){
   return exec(success, fail, 'XtremePush', 'isSandboxModeOn',[]);
};

/**
 * Only for iOS. Returnes version of the XtremePush library
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */        
XtremePush.prototype.version = function(success, fail){
   return exec(success, fail, 'XtremePush', 'version',[]);
};

/**
 * Only for iOS. Gets if library can wipe badge number
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */              
XtremePush.prototype.shouldWipeBadgeNumber = function(success, fail){
   return exec(success, fail, 'XtremePush', 'shouldWipeBadgeNumber',[]);
};

/**
 * Only for iOS. Sets if library can wipe badge number
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */                  
XtremePush.prototype.setShouldWipeBadgeNumber = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setShouldWipeBadgeNumber', [value]);
};

/**
 * Returns device information
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */              
XtremePush.prototype.deviceInfo = function(success, fail){
   return exec(success, fail, 'XtremePush', 'deviceInfo',[]);
};

/**
 * Only for iOS. Switches on/ooff use location manager in the app.
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XtremePush.prototype.setLocationEnabled = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setLocationEnabled', [value]);
};

/**
 * Only for iOS. if location disables then does nothing
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */
XtremePush.prototype.setAsksForLocationPermissions = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setAsksForLocationPermissions', [value]);
};   

/**
 * Only for Android. set if stadard window should be shown on push
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Boolean } value   set if dialog should be shown. nothing - if should not
 */
XtremePush.prototype.setShowDialog = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setShowDialog', [value]);
};  

/**
 * Only for Android. Use to enable tag batching
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Object} options for method:
 *           batching - (boolean) set batching enabled - required
 *           limit - (int) max number of tags to store - optional
 */ 
XtremePush.prototype.setTagsBatchingEnabled = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setTagsBatchingEnabled', [value]);
};  

/**
 * Only for Android. Use to enable impression batching
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Object} options for method:
 *           batching - (boolean) set batching enabled - required
 *           limit - (int) max number of impressions to store - optional
 */ 
XtremePush.prototype.setImpressionsBatchingEnabled = function(success, fail, value){
   return exec(success, fail, 'XtremePush', 'setImpressionsBatchingEnabled', [value]);
};       

/**
 * Calling hit tag function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   tag     value which will be sent
 */             
XtremePush.prototype.hitTag = function(success, fail, tag){
   return exec(success, fail, 'XtremePush', 'hitTag', [tag]);
};

/**
 * Calling hit tag function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   tag     value which will be sent
 * @param  {String}   message     associated with tag
 */             
XtremePush.prototype.hitTag = function(success, fail, tag, message){
   return exec(success, fail, 'XtremePush', 'hitTag', [tag, message]);
};

/**
 * Calling hit impression function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   impression value which will be sent
 */             
XtremePush.prototype.hitImpression = function(success, fail, impression){
   return exec(success, fail, 'XtremePush', 'hitImpression', [impression]);
};

/**
 * Calling hit impression function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   impression value which will be sent
 * @param  {String}   message associated with impression
 */             
XtremePush.prototype.hitImpression = function(success, fail, impression, message){
   return exec(success, fail, 'XtremePush', 'hitImpression', [impression, message]);
};

/**
 * Calling hit event function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   title   title of the event
 * @param  {String}   message message of the event
 */             
XtremePush.prototype.hitEvent = function(success, fail, title, message){
   return exec(success, fail, 'XtremePush', 'hitEvent', [title, message]);
};

/**
 * Calling send tags function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XtremePush.prototype.sendTags = function(success, fail){
   return exec(success, fail, 'XtremePush', 'sendTags',[]);
};

/**
 * Calling send impressions function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XtremePush.prototype.sendImpressions = function(success, fail){
   return exec(success, fail, 'XtremePush', 'sendImpressions', []);
};

/**
 * Shows push log view
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XtremePush.prototype.showPushListController = function(success, fail){
   return exec(success, fail, 'XtremePush', 'showPushListController',[]);
};

/**
 * Returnes list of received pushed
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Number}   offset value
 * @param  {Number}   limit value
 */             
XtremePush.prototype.getPushNotificationsOffset = function(success, fail, offset, limit){
   return exec(success, fail, 'XtremePush', 'getPushNotificationsOffset', [offset, limit]);
};

module.exports = new XtremePush;
