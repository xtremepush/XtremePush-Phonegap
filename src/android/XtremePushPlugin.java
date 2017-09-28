package com.xtreme.plugins;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import com.squareup.otto.Subscribe;
import ie.imobile.extremepush.*;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.network.ConnectionManager;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dmytro Malieiev on 6/8/14.
 */
public class XtremePushPlugin extends CordovaPlugin implements InboxBadgeListener {
    public static final String TAG = "PushPlugin";
    public static final String REGISTER = "register";
    public static final String HITTAG = "hitTag";
    public static final String HITIMPRESSION = "hitImpression";
    public static final String HITEVENT = "hitEvent";
    public static final String SENDTAGS = "sendTags";
    public static final String SENDIMPRESSIONS = "sendImpressions";
    public static final String SETEXTERNALID = "setExternalId";
    public static final String SETSUBSCRIPTION = "setSubscription";
    public static final String DEVICEINFO = "deviceInfo";
    public static final String REQUESTLOCATIONSPERMISSIONS = "requestLocationsPermissions";   
    public static final String OPENINBOX = "openInbox";
    public static final String GETINBOXBADGE = "getInboxBadge";

    private static String AppId = "Your application ID";
    private static String GoogleProjectID = "Your Google Project ID";

    private static CordovaWebView _webView;
    private static String callback_function;
    private static String badge_callback_function;
    private static CallbackContext _callbackContext;
    private static Bundle cachedExtras;
    private PushConnector pushConnector;
    private static boolean isRegistered = false;
    private static boolean isInitialized = false;

    private static boolean inForeground = false;
    private static boolean notNewIntent = false;
    private static BroadcastReceiver mReceiver;
    private static String lastNotificationID = "";
    private static String lastForegroundID = "";
    private static String lastBackgroundID = "";

    /*
     * Returns application context
     */
    private Context getApplicationContext(){
        return this.getApplicationActivity().getApplicationContext();
    }

    /*
    * Returns application context
    */
    private Activity getApplicationActivity(){
        return this.cordova.getActivity();
    }

    /*
     * Returns FragmentManager Context
     */
    private FragmentManager getApplicationFragmentManager(){
        return this.cordova.getActivity().getFragmentManager();
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException
    {
        boolean result = false;

        LogEventsUtils.sendLogTextMessage(TAG, "execute: action = " + action);

        if (REGISTER.equals(action)) {
            register(data, callbackContext);
        } else if (HITTAG.equals(action)) {
            hitTag(data);
        } else if (HITIMPRESSION.equals(action)) {
            hitImpression(data);
        } else if (HITEVENT.equals(action)) {
            hitEvent(data);
        } else if (SENDTAGS.equals(action)) {
            sendTags();
        } else if (SENDIMPRESSIONS.equals(action)) {
            sendImpressions();
        } else if (SETEXTERNALID.equals(action)) {
            setExternalId(data);
        } else if(SETSUBSCRIPTION.equals(action)) {
            setSubscription(data);
        } else if (DEVICEINFO.equals(action)) {
            getDeviceInfo(callbackContext);
        } else if (REQUESTLOCATIONSPERMISSIONS.equals(action)) {
            requestLocationsPermissions();
        } else if (OPENINBOX.equals(action)) {
            openInbox();
        } else if (GETINBOXBADGE.equals(action)) {
            getInboxBadge();
        }

        if ( cachedExtras != null) {
            LogEventsUtils.sendLogTextMessage(TAG, "sending cached extras");
            sendExtras(cachedExtras);
            cachedExtras = null;
        }
        return true;
    }

    private void register(JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (pushConnector == null) {
            this._webView = this.webView;
            JSONObject jo = data.getJSONObject(0);

            if (jo.isNull("pushOpenCallback")){
                Log.e(TAG, "register: Please provide callback function");
                callbackContext.error("Please provide callback function");
                return;
            }

            String appKey;
            if (!jo.isNull("appKey")){
                appKey = jo.getString("appKey");
            } else {
                Log.e(TAG, "register: Please provide a valid xtremepush app key");
                callbackContext.error("Please provide a valid xtremepush app key");
                return;
            }

            String gcmProjectNumber = null;
            JSONObject joAndroid = null;
            if (!jo.isNull("android")) {
                joAndroid = jo.getJSONObject("android");
                if (!joAndroid.isNull("gcmProjectNumber")){
                    gcmProjectNumber = joAndroid.getString("gcmProjectNumber");
                }
            }

            PushConnector.Builder b = new PushConnector.Builder(appKey, gcmProjectNumber);

            if (!jo.isNull("serverUrl")){
                String serverUrl = jo.getString("serverUrl");
                b.setServerUrl(serverUrl);
            }

            if (!jo.isNull("attributionsEnabled")){
                Boolean attributions = jo.getBoolean("attributionsEnabled");
                b.setAttributionsEnabled(attributions);
            }

            if (!jo.isNull("inappMessagingEnabled")){
                Boolean inapp = jo.getBoolean("inappMessagingEnabled");
                b.setEnableStartSession(inapp);
            }

            if (!jo.isNull("inboxEnabled")){
                Boolean inbox = jo.getBoolean("inboxEnabled");
                b.setInboxEnabled(inbox);
            }

            if (!jo.isNull("debugLogsEnabled")){
                Boolean debugLogsEnabled = jo.getBoolean("debugLogsEnabled");
                b.turnOnDebugLogs(debugLogsEnabled);
            }

            if (!jo.isNull("tagsBatchingEnabled")){
                Boolean tagsBatchingEnabled = jo.getBoolean("tagsBatchingEnabled");
                b.setTagsBatchingEnabled(tagsBatchingEnabled);
            }

            if (!jo.isNull("impressionsBatchingEnabled")){
                Boolean impressionsBatchingEnabled = jo.getBoolean("impressionsBatchingEnabled");
                b.setImpressionsBatchingEnabled(impressionsBatchingEnabled);
            }

            if (!jo.isNull("tagsStoreLimit")){
                Integer tagsStoreLimit = jo.getInt("tagsStoreLimit");
                b.setTagsStoreLimit(tagsStoreLimit);
            }

            if (!jo.isNull("impressionsStoreLimit")){
                Integer impressionsStoreLimit = jo.getInt("impressionsStoreLimit");
                b.setImpressionsStoreLimit(impressionsStoreLimit);
            }

            if (!jo.isNull("sessionsStoreLimit")){
                Integer sessionsStoreLimit = jo.getInt("sessionsStoreLimit");
                b.setSessionsStoreLimit(sessionsStoreLimit);
            }


            if(joAndroid != null) {
                // Android only options
                if (!joAndroid.isNull("geoEnabled")){
                    Boolean geoEnabled = joAndroid.getBoolean("geoEnabled");
                    b.setEnableGeo(geoEnabled);
                }

                if (!joAndroid.isNull("locationsPermissionsRequest")){
                    Boolean locationsPermissionsRequest = joAndroid.getBoolean("locationsPermissionsRequest");
                    b.setRequestPermissions(locationsPermissionsRequest);
                }

                if (!joAndroid.isNull("beaconsEnabled")){
                    Boolean beaconsEnabled = joAndroid.getBoolean("beaconsEnabled");
                    b.setEnableBeacons(beaconsEnabled);
                }

                if (!joAndroid.isNull("setIcon")){
                    // Might need rewriting so that it takes image from www folder
                    String icon = joAndroid.getString("setIcon");
                    b.setIcon(icon);
                }
            }

            pushConnector = b.create(getApplicationActivity());

            callback_function = (String) jo.getString("pushOpenCallback");
            badge_callback_function = (String) jo.optString("inboxBadgeCallback", null);
            initNotificationMessageReceivers();
            isRegistered = true;
            initializePushConnector();
        }

        callbackContext.success("Successfully registered!");
    }

    private void hitTag(JSONArray data) throws JSONException {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "hitTag: Please call register function first");
            return;
        }

        if (data.isNull(0)){
            LogEventsUtils.sendLogTextMessage(TAG, "hitTag: Please provide tag title");
            return;
        }

        String tag =  data.getString(0);

        if (!data.isNull(1)){
            pushConnector.hitTag(tag, data.getString(1));
        } 
        else {
            pushConnector.hitTag(tag);
        }
    }

    private void hitEvent(JSONArray data) throws JSONException {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "hitEvent: Please call register function first");
            return;
        }

        if (data.isNull(0)){
            LogEventsUtils.sendLogTextMessage(TAG, "hitEvent: Please provide event title");
            return;
        }

        String title = data.getString(0);
        String message = null;
        if (!data.isNull(1)) {
            message = data.getString(1);
        }

        pushConnector.hitEvent(getApplicationContext(), title, message);
    }

    private void hitImpression(JSONArray data) throws JSONException {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "hitImpression: Please call register function first");
            return;
        }

        if (data.isNull(0)){
            LogEventsUtils.sendLogTextMessage(TAG, "hitImpression: Please provide impression title");
            return;
        }

        String impression =  data.getString(0);

        pushConnector.hitImpression(impression);
    }

    private void sendTags()
    {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "sendTags: Please call register function first");
            return;
        }
        pushConnector.sendTags();
    }

    private void sendImpressions()
    {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "sendImpressions: Please call register function first");
            return;
        }
        pushConnector.sendImpressions();
    }

    private void setExternalId(JSONArray data) throws JSONException {
        if (!isRegistered) {
            LogEventsUtils.sendLogTextMessage(TAG, "setExternalId: Please call register function first");
            return;
        }

        if (data.isNull(0)) {
            LogEventsUtils.sendLogTextMessage(TAG, "setExternalId: Please provide ID");
            return;
        }

        String id =  data.getString(0);

        pushConnector.hitTag("user.external_id", id);
    }

    private void setSubscription(JSONArray data) throws JSONException {
        if (data.isNull(0)) {
            LogEventsUtils.sendLogTextMessage(TAG, "setSubscription: Please provide true/false value");
            return;
        }

        Boolean subBoolean = data.getBoolean(0);
        String subStatus = subBoolean ? "1" : "0";
        SharedPrefUtils.setSubscriptionStatus(subStatus, getApplicationContext());
        ConnectionManager.getInstance().updateDevice(getApplicationContext());
    }

    private void openInbox()
    {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "openInbox: Please call register function first");
            return;
        }
        cordova.setActivityResultCallback(this);
        pushConnector.openInbox(getApplicationActivity());
    }

    private void getInboxBadge()
    {
        if (!isRegistered){
            LogEventsUtils.sendLogTextMessage(TAG, "openInbox: Please call register function first");
            return;
        }
        inboxBadgeUpdated(pushConnector.getInboxBadge());
    }

    @Override
    public void inboxBadgeUpdated(int badge) {
        if (badge_callback_function != null && _webView != null) {
            String _d = "javascript:" + badge_callback_function + "(" + badge + ")";
            LogEventsUtils.sendLogTextMessage(TAG, "inboxBadgeUpdated: " + _d);
            _webView.sendJavascript(_d);
        } else {
            LogEventsUtils.sendLogTextMessage(TAG, "inboxBadgeUpdated: callback or webview is null");
        }
    }

    private void getDeviceInfo(CallbackContext callbackContext) {
        if (!isRegistered) {
            LogEventsUtils.sendLogTextMessage(TAG, "getDeviceInfo: Please call register function first");
            callbackContext.error("Please call register function first");
        }

        HashMap<String, String> deviceInfo = pushConnector.getDeviceInfo(getApplicationContext());

        JSONObject devInfo = new JSONObject(deviceInfo);

        callbackContext.success(devInfo);
    }

    private void requestLocationsPermissions() {
        pushConnector.requestLocationsPermissions(getApplicationActivity());
    }

    /*
     * Start intent listening receiving push notifications
     */
    private void initNotificationMessageReceivers(){
        IntentFilter intentFilter = new IntentFilter(
                "ie.imobile.extremepush.action_message");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                if (extras != null)
                {
                    if (inForeground) {
                        PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
                        if(pushMessage != null){
                            if(!(pushMessage.pushActionId.equals(lastForegroundID))){
                                lastForegroundID = pushMessage.pushActionId;
                                extras.putBoolean("foreground", true);
                                sendExtras(extras);
                            }
                        }
                    }
                }
            }
        };

        this.getApplicationContext().registerReceiver(mReceiver, intentFilter);
    }

   /*
    * Sends the pushbundle extras to the client application.
    * If the client application isn't currently active, it is cached for later processing.
    */
    public static void sendExtras(Bundle extras)
    {
        if (extras != null) {
            if (callback_function != null && _webView != null) {
                sendJavascript(convertBundleToJson(extras));
            } else {
                LogEventsUtils.sendLogTextMessage(TAG, "sendExtras: caching extras to send at a later time.");
                cachedExtras = extras;
            }
        }
    }

   /*
    * Sends a json object to the client as parameter to a method which is defined in gECB.
    */
    public static void sendJavascript(JSONObject _json) {

        String _d = "javascript:" + callback_function + "(" + _json.toString() + ")";
        LogEventsUtils.sendLogTextMessage(TAG, "sendJavascript: " + _d);

        if (callback_function != null && _webView != null) {
            _webView.sendJavascript(_d);
        }
    }

    /*
    * serializes a bundle to JSON.
    */
    private static JSONObject convertBundleToJson(Bundle extras)
    {
        try
        {
            JSONObject json;
            json = new JSONObject().put("event", "message");
            JSONObject jsondata = new JSONObject();
            Iterator<String> it = extras.keySet().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                Object value = extras.get(key);

                // System data from Android
                if (key.equals("from") || key.equals("collapse_key"))
                {
                    json.put(key, value);
                }
                else if (key.equals("foreground"))
                {
                    json.put(key, extras.getBoolean("foreground"));
                }
                else if (key.equals("coldstart"))
                {
                    json.put(key, extras.getBoolean("coldstart"));
                }
                else
                {
                    // Maintain backwards compatibility
                    if (key.equals("message") || key.equals("msgcnt") || key.equals("soundname"))
                    {
                        json.put(key, value);
                    }

                    if ( value instanceof String ) {
                        // Try to figure out if the value is another JSON object

                        String strValue = (String)value;
                        if (strValue.startsWith("{")) {
                            try {
                                JSONObject json2 = new JSONObject(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e) {
                                jsondata.put(key, value);
                            }
                            // Try to figure out if the value is another JSON array
                        }
                        else if (strValue.startsWith("["))
                        {
                            try
                            {
                                JSONArray json2 = new JSONArray(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e)
                            {
                                jsondata.put(key, value);
                            }
                        }
                        else
                        {
                            jsondata.put(key, value);
                        }
                    }

                    if (value instanceof PushMessage)
                    {
                        if(((PushMessage)value).pushActionId != null)
                            json.put("pushActionId", ((PushMessage)value).pushActionId);
                        if(((PushMessage)value).alert != null)
                            json.put("alert", ((PushMessage)value).alert);
                        if(((PushMessage)value).badge != null)
                            json.put("badge", ((PushMessage)value).badge);
                        if(((PushMessage)value).sound != null)
                            json.put("sound", ((PushMessage)value).sound);
                        if(((PushMessage)value).url != null)
                            json.put("url", ((PushMessage)value).url);
                        if(((PushMessage)value).um != null)
                            json.put("um", ((PushMessage)value).um);
                        if(((PushMessage)value).source != null)
                            json.put("source", ((PushMessage)value).source);
                        Iterator iterator = ((PushMessage) value).payLoadMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> pair = (Map.Entry<String, String>)iterator.next();
                            jsondata.put(pair.getKey(), pair.getValue());
                        }
                    }
                }
            } // while
            json.put("payload", jsondata);

            LogEventsUtils.sendLogTextMessage(TAG, "extrasToJSON: " + json.toString());

            return json;
        }
        catch( JSONException e)
        {
            LogEventsUtils.sendLogTextMessage(TAG, "extrasToJSON: JSON exception");
        }
        return null;
    }

    private void initializePushConnector(){
        pushConnector.onStart(getApplicationActivity());
        pushConnector.onResume(getApplicationActivity());
        isInitialized = true;
    }

    /*
     * Initialization of plugin
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        inForeground = true;
    }

    /*
     * When application goes to onPause state
     */
    @Override
    public void onPause(boolean multitasking) {
        if(isInitialized && isRegistered && (pushConnector != null)){
            pushConnector.onPause(getApplicationActivity());
        }
        super.onPause(multitasking);
        inForeground = false;
        notNewIntent = true;
    }

    /*
     * When application will go to onResume state
     */
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        inForeground = true;
        if (isInitialized && isRegistered && (pushConnector != null)) {
            initializePushConnector();
        }
        if (getApplicationActivity().getIntent().hasExtra(GCMIntentService.EXTRAS_PUSH_MESSAGE)) {
            Bundle extras = getApplicationActivity().getIntent().getExtras();
            PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
            if(pushMessage != null){
                if(!(pushMessage.pushActionId.equals(lastNotificationID)) && !(pushMessage.pushActionId.equals(lastBackgroundID))){
                    extras.putBoolean("foreground", false);
                    sendExtras(extras);
                    if(notNewIntent){
                        lastNotificationID = pushMessage.pushActionId;
                        // ConnectionManager.getInstance().hitAction(getApplicationContext(), lastNotificationID, 1);
                        // SharedPrefUtils.setLastPushId(getApplicationContext(), lastNotificationID);
                    }
                    else{
                        lastBackgroundID = pushMessage.pushActionId;
                        // SharedPrefUtils.setLastNotificationPushId(getApplicationContext(), lastBackgroundID);
                    }
                }
            }
        }
    }

    /**
     * Called when the activity receives a new intent.
     */
    @Override
    public void onNewIntent(Intent intent) {
        if(isInitialized && isRegistered && (pushConnector != null)){
            pushConnector.onNewIntent(intent);
        }
        super.onNewIntent(intent);
        getApplicationActivity().setIntent(intent);
        notNewIntent = false;
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode   The request code originally supplied to startActivityForResult(),
     *                      allowing you to identify who this result came from.
     * @param resultCode    The integer result code returned by the child activity through its setResult().
     * @param intent        An Intent, which can return result data to the caller (various data can be
     *                      attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(isInitialized && isRegistered && (pushConnector != null))
            pushConnector.onActivityResult(requestCode, resultCode, intent);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /*
     * When application goes to onStop state
     */
    @Override
    public void onStop() {
        if(isInitialized && isRegistered && (pushConnector != null)){
            pushConnector.onStop(getApplicationActivity());
        }
        super.onStop();
    }

    /*
     * when application goes to onDestroy state
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isInitialized && isRegistered && (pushConnector != null)){
            pushConnector.onDestroy(getApplicationActivity());
        }
        isInitialized = false;
        inForeground = false;
        callback_function = null;
        webView = null;
    }

}
