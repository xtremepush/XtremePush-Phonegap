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
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        console.log('Received Event: ' + id);
    }
};


function register(){
    XTremePush.register(successCallback, failCallback, { alert:true, badge:true, sound:true, callbackFunction:"onPushReceived", locationDistance:30, locationTimeout:50});
}

function unregister(){
    XTremePush.unregister(successCallback, failCallback);
}

function isSandboxModeOn(){
    XTremePush.isSandboxModeOn(successCallback, failCallback);
}

function version(){
    XTremePush.version(successCallback, failCallback);
}

function shouldWipeBadgeNumber(){
    XTremePush.shouldWipeBadgeNumber(successCallback, failCallback);
}

function deviceInfo(){
    XTremePush.deviceInfo(successCallback, failCallback);
}


function setShouldWipeBadgeNumber(){
    XTremePush.setShouldWipeBadgeNumber(successCallback, failCallback, true);
}

function setLocationEnabled(){
    XTremePush.setLocationEnabled(successCallback, failCallback, true);
}

function setAsksForLocationPermissions(){
    XTremePush.setAsksForLocationPermissions(successCallback, failCallback, true);
}

function hitTag(){
    XTremePush.hitTag(successCallback, failCallback, 'hitTag');
}

function hitImpression(){
    XTremePush.hitImpression(successCallback, failCallback, 'hitImpression');
}

function showPushListController(success, fail, impression){
    XTremePush.showPushListController(successCallback, failCallback);
};

function getPushNotificationsOffset(success, fail){
    XTremePush.getPushNotificationsOffset(successCallback, failCallback, 0);
};

function successCallback(result){
    alert('Success callback : ' + JSON.stringify(data));
}

function failCallback(result){
    alert('Fail callback : ' + JSON.stringify(data));
}

function onPushReceived(data){
    alert('Push received : ' + JSON.stringify(data));
}

function successCallback(status){
    
    alert('Success: ' + JSON.stringify(status));
}

function failCallback(status){
    alert('failed: ' + status);
    
}

/*

 
 XTremePush.prototype.hitTag = function(success, fail, tag){
 return exec(success, fail, 'XTremePush', 'hitTag', [tag]);
 };
 
 XTremePush.prototype.hitImpression = function(success, fail, impression){
 return exec(success, fail, 'XTremePush', 'hitImpression', [impression]);
 };
 
 XTremePush.prototype.showPushListController = function(success, fail){
 return exec(success, fail, 'XTremePush', 'showPushListController');
 };
 
 XTremePush.prototype.getPushNotificationsOffset = function(success, fail, offset, limit){
 return exec(success, fail, 'XTremePush', 'getPushNotificationsOffset', [offset, limit]);
 };

 */


