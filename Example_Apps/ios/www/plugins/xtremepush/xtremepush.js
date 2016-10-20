cordova.define("XtremePush", function(require, exports, module) {
               
  var exec = require('cordova/exec');
  
  function XtremePush() { }
  
  XtremePush.prototype.register = function(options) {
     return exec(null, null, 'XtremePush', 'register', [options]);
  };
               
  XtremePush.prototype.deviceInfo = function(success){
     return exec(success, null, 'XtremePush', 'deviceInfo');
  };
                              
  XtremePush.prototype.hitTag = function(tag, value) {
     if (value) {
        return exec(null, null, 'XtremePush', 'hitTag', [tag, value]);
     } else {
        return exec(null, null, 'XtremePush', 'hitTag', [tag]);
     }
  };
               
  XtremePush.prototype.hitImpression = function(impression){
     return exec(null, null, 'XtremePush', 'hitImpression', [impression]);
  };
               
               XtremePush.prototype.requestLocationsPermissions = function() {
               return exec(null, null, 'XtremePush', 'requestLocationsPermissions', []);
               };
               
               XtremePush.prototype.requestPushPermissions = function() {
               return exec(null, null, 'XtremePush', 'requestPushPermissions', []);
               };
               
  module.exports = new XtremePush();
   
});
