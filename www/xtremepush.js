var exec = require('cordova/exec');

function XTremePush() { }

/**
 * Calling register function of the XTremePush plugin
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
XTremePush.prototype.register = function(success, fail, options) {
   return exec(success, fail, 'XTremePush', 'register', [options]);
};
             
/**
 * Unregister from push notifications
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */
XTremePush.prototype.unregister = function(success, fail){
   return exec(success, fail, 'XTremePush', 'unregister',[]);
};

/**
 * Only for iOS. Returns if application is configured for development mode
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */           
XTremePush.prototype.isSandboxModeOn = function(success, fail){
   return exec(success, fail, 'XTremePush', 'isSandboxModeOn',[]);
};

/**
 * Only for iOS. Returnes version of the XTremePush library
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */        
XTremePush.prototype.version = function(success, fail){
   return exec(success, fail, 'XTremePush', 'version',[]);
};

/**
 * Only for iOS. Gets if library can wipe badge number
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */              
XTremePush.prototype.shouldWipeBadgeNumber = function(success, fail){
   return exec(success, fail, 'XTremePush', 'shouldWipeBadgeNumber',[]);
};

/**
 * Only for iOS. Sets if library can wipe badge number
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */                  
XTremePush.prototype.setShouldWipeBadgeNumber = function(success, fail, value){
   return exec(success, fail, 'XTremePush', 'setShouldWipeBadgeNumber', [value]);
};

/**
 * Returns device information
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */              
XTremePush.prototype.deviceInfo = function(success, fail){
   return exec(success, fail, 'XTremePush', 'deviceInfo',[]);
};

/**
 * Only for iOS. Switches on/ooff use location manager in the app.
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XTremePush.prototype.setLocationEnabled = function(success, fail, value){
   return exec(success, fail, 'XTremePush', 'setLocationEnabled', [value]);
};

/**
 * Only for iOS. if location disables then does nothing
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */
XTremePush.prototype.setAsksForLocationPermissions = function(success, fail, value){
   return exec(success, fail, 'XTremePush', 'setAsksForLocationPermissions', [value]);
};   

/**
 * Only for Android. set if stadard window should be shown on push
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Boolean } value   set if dialog should be shown. nothing - if should not
 */
XTremePush.prototype.setShowDialog = function(success, fail, value){
   return exec(success, fail, 'XTremePush', 'setShowDialog', [value]);
};           

/**
 * Calling hit tag function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   tag     value which will be sent
 */             
XTremePush.prototype.hitTag = function(success, fail, tag){
   return exec(success, fail, 'XTremePush', 'hitTag', [tag]);
};

/**
 * Calling hit impression function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   impression value which will be sent
 */             
XTremePush.prototype.hitImpression = function(success, fail, impression){
   return exec(success, fail, 'XTremePush', 'hitImpression', [impression]);
};


/**
 * Calling hit event function
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {String}   title   title of the event
 * @param  {String}   message message of the event
 */             
XTremePush.prototype.hitEvent = function(success, fail, title, message){
   return exec(success, fail, 'XTremePush', 'hitEvent', [title, message]);
};


/**
 * Shows push log view
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 */             
XTremePush.prototype.showPushListController = function(success, fail){
   return exec(success, fail, 'XTremePush', 'showPushListController',[]);
};

/**
 * Returnes list of received pushed
 * @param  {Function} success callback function which will be called in case of success of the function
 * @param  {Function} fail    callback function which will be called in case of failure
 * @param  {Number}   offset value
 * @param  {Number}   limit value
 */             
XTremePush.prototype.getPushNotificationsOffset = function(success, fail, offset, limit){
   return exec(success, fail, 'XTremePush', 'getPushNotificationsOffset', [offset, limit]);
};

module.exports = new XTremePush;