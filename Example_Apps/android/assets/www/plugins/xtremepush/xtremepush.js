cordova.define("XTremePush", function(require, exports, module) {
               
  var exec = require('cordova/exec');
  
  function XTremePush() { }
  
  XTremePush.prototype.register = function(success, fail, options) {
     return exec(success, fail, 'XTremePush', 'register', [options]);
  };
               
  XTremePush.prototype.unregister = function(success, fail){
     return exec(success, fail, 'XTremePush', 'unregister',[]);
  };
               
  XTremePush.prototype.isSandboxModeOn = function(success, fail){
     return exec(success, fail, 'XTremePush', 'isSandboxModeOn',[]);
  };
               
  XTremePush.prototype.version = function(success, fail){
     return exec(success, fail, 'XTremePush', 'version',[]);
  };
               
  XTremePush.prototype.shouldWipeBadgeNumber = function(success, fail){
     return exec(success, fail, 'XTremePush', 'shouldWipeBadgeNumber',[]);
  };
               
  XTremePush.prototype.deviceInfo = function(success, fail){
     return exec(success, fail, 'XTremePush', 'deviceInfo',[]);
  };
               
  XTremePush.prototype.setShouldWipeBadgeNumber = function(success, fail, value){
     return exec(success, fail, 'XTremePush', 'setShouldWipeBadgeNumber', [value]);
  };
               
  XTremePush.prototype.setLocationEnabled = function(success, fail, value){
     return exec(success, fail, 'XTremePush', 'setLocationEnabled', [value]);
  };

  XTremePush.prototype.setAsksForLocationPermissions = function(success, fail, value){
     return exec(success, fail, 'XTremePush', 'setAsksForLocationPermissions', [value]);
  };           
               
  XTremePush.prototype.hitTag = function(success, fail, tag){
     return exec(success, fail, 'XTremePush', 'hitTag', [tag]);
  };
               
  XTremePush.prototype.hitImpression = function(success, fail, impression){
     return exec(success, fail, 'XTremePush', 'hitImpression', [impression]);
  };
               
  XTremePush.prototype.showPushListController = function(success, fail){
     return exec(success, fail, 'XTremePush', 'showPushListController',[]);
  };
               
  XTremePush.prototype.getPushNotificationsOffset = function(success, fail, offset, limit){
     return exec(success, fail, 'XTremePush', 'getPushNotificationsOffset', [offset, limit]);
  };
    
  module.exports = new XTremePush();
   
});