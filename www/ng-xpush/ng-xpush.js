angular.module('XTreme.Plugins', [])
  .factory('$xpush',['$rootScope', '$timeout', function($rootScope, $timeout){
    return {
      
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
      register: function(success, fail, options){
        
        var callbackFunction = function(){
          $rootScope.$broadcast('$xpush:notificationReceived', notification);     
        }
        if (options !== undefined && options.callbackFunction === undefined) {
          
          options.callbackFunction = callbackFunction;
        }


        return XtremePush.register(success, fail, options);
      },

      /**
       * Unregister from push notifications
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */
      unregister: function(success, fail){
        return XtremePush.unregister(success, fail);
      },

      /**
       * Only for iOS. Returns if application is configured for development mode
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */         
      isSandboxModeOn: function(success, fail){
        return XtremePush.isSandboxModeOn(success, fail);
      },

      /**
       * Only for iOS. Returnes version of the XtremePush library
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */        
      version: function(success, fail){
        return XtremePush.version(success, fail);
      },

      /**
       * Only for iOS. Gets if library can wipe badge number
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */   
      shouldWipeBadgeNumber: function(success, fail){
        return XtremePush.shouldWipeBadgeNumber(success, fail);
      },


      /**
       * Only for iOS. Sets if library can wipe badge number
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */                  
      setShouldWipeBadgeNumber: function(success, fail, value){
        return XtremePush.setShouldWipeBadgeNumber(uccess, fail, value);
      },

      /**
       * Returns device information
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */     
      deviceInfo: function(success, fail){
        return XtremePush.deviceInfo(success, fail);
      },

      /**
       * Only for iOS. Switches on/ooff use location manager in the app.
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */    
      setLocationEnabled: function(success, fail, value){
        return XtremePush.setLocationEnabled(success, fail, value);
      },

      /**
       * Only for iOS. if location disables then does nothing
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */
      setAsksForLocationPermissions: function(success, fail, value){
        return XtremePush.setAsksForLocationPermissions(success, fail, value);
      },

      /**
       * Only for Android. set if stadard window should be shown on push
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       * @param  {Boolean } value   set if dialog should be shown. nothing - if should not
       */
      setShowDialog: function(success, fail, value){
        return XtremePush.setShowDialog(success, fail, value);
      },

      /**
       * Calling hit tag function
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       * @param  {String}   tag     value which will be sent
       */             
      hitTag: function(success, fail, tag){
        return XtremePush.hitTag(success, fail, tag);
      },

      /**
       * Calling hit impression function
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       * @param  {String}   impression value which will be sent
       */         
      hitImpression: function(success, fail, impression){
        return XtremePush.hitImpression(success, fail, impression)
      },

      /**
       * Calling hit event function
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       * @param  {String}   title   title of the event
       * @param  {String}   message message of the event
       */             
      hitEvent: function(success, fail, title, message){
         return XtremePush.hitEvent(success, fail, title, message);
      },

      /**
       * Shows push log view
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       */             
      showPushListController: function(success, fail){
         return XtremePush.showPushListController(success, fail)
      },

      /**
       * Returnes list of received pushed
       * @param  {Function} success callback function which will be called in case of success of the function
       * @param  {Function} fail    callback function which will be called in case of failure
       * @param  {Number}   offset value
       * @param  {Number}   limit value
       */             
      getPushNotificationsOffset: function(success, fail, offset, limit){
          return XtremePush.getPushNotificationsOffset(success, fail, offset, limit);
      }
    }
  }]);
