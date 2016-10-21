package xtreme.com.push;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import com.squareup.otto.Subscribe;
import ie.imobile.extremepush.api.model.EventsPushlistWrapper;
import ie.imobile.extremepush.ui.XPushLogActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import ie.imobile.extremepush.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dmytro Malieiev on 6/8/14.
 */
public class XTremePushPlugin extends CordovaPlugin {
    public static final String TAG = "PushPlugin";

    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unregister";
    public static final String ISSANDBOXMODE = "isSandboxModeOn";
    public static final String VERSION = "version";
    public static final String SHOULDWIPEBADGENUMBER = "shouldWipeBadgeNumber";
    public static final String DEVICEINFO = "deviceInfo";
    public static final String SETSHOULDWIPEBADGENUMBER = "setShouldWipeBadgeNumber";
    public static final String SETLOCATIONENABLED = "setLocationEnabled";
    public static final String SETASKSFORLOCATIONPERMISSION = "setAsksForLocationPermissions";
    public static final String HITTAG = "hitTag";
    public static final String HITIMPRESSION = "hitImpression";
    public static final String SHOWPUSHLISTCONTROLLER = "showPushListController";
    public static final String GETPUSHNOTIFICATIONOFFSET = "getPushNotificationsOffset";

    private static CordovaWebView _webView;
    private static String callback_function;
    private static CallbackContext _callbackContext;
    private static Bundle cachedExtras;
    private PushConnector pushConnector;
    private static boolean isRegistered = false;

    private static String AppId = "eb22f3b665dca2c68a";
    private static String GoogleProjectID = "33474064823";

    private static boolean inForeground = false;
    private static BroadcastReceiver mReceiver;

    /*
     * Returns application context
     */
    private Context getApplicationContext(){
        return this.cordova.getActivity().getApplicationContext();
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

        Log.v(TAG, "execute: action = " + action);

        if (REGISTER.equals(action)) {
            Register(data, callbackContext);
        } else if (UNREGISTER.equals(action)) {
            Unregister(callbackContext);

        } else if (ISSANDBOXMODE.equals(action)) {
            getIsSandboxModeOn(callbackContext);
        } else if (VERSION.equals(action)) {
            getVersion(callbackContext);
        } else if (SHOULDWIPEBADGENUMBER.equals(action)) {
            getShouldWipeBadgeNumber(callbackContext);
        } else if (DEVICEINFO.equals(action)) {
            getDeviceInfo(callbackContext);
        } else if (SETSHOULDWIPEBADGENUMBER.equals(action)) {
            setShouldWipeBadgeNumber(callbackContext);
        } else if (SETLOCATIONENABLED.equals(action)) {
            setLocationEnabled(callbackContext);
        } else if (SETASKSFORLOCATIONPERMISSION.equals(action)) {
            setAskForLocationPermission(callbackContext);
        } else if (HITTAG.equals(action)) {
            hitTag(data, callbackContext);
        } else if (HITIMPRESSION.equals(action)) {
            hitImpression(data,callbackContext);
        } else if (SHOWPUSHLISTCONTROLLER.equals(action)) {
            showPushListController(callbackContext);
        } else if (GETPUSHNOTIFICATIONOFFSET.equals(action)) {
            getPushNotificationOffset(data,callbackContext);

        }

        if ( cachedExtras != null) {
            Log.v(TAG, "sending cached extras");
            sendExtras(cachedExtras);
            cachedExtras = null;
        }
        return true;
    }

    private void Register(JSONArray data, CallbackContext callbackContext) throws JSONException {
        this._webView = this.webView;
        JSONObject jo = data.getJSONObject(0);

        if (jo.isNull("callbackFunction")){
            callbackContext.error("Please provide callback function");
            return;
        }

        if (!jo.isNull("locationTimeout") && !jo.isNull("locationDistance")){
            Integer locationTimeout = jo.getInt("locationTimeout");
            Integer locationDistance = jo.getInt("locationDistance");

            pushConnector = PushConnector.init(getApplicationFragmentManager(), this.AppId, this.GoogleProjectID, locationTimeout, locationDistance);
        } else {
            pushConnector = PushConnector.init(getApplicationFragmentManager(), this.AppId, this.GoogleProjectID);
        }

        callback_function = (String) jo.getString("callbackFunction");
        initNotificationMessageReceivers();
        isRegistered = true;

        callbackContext.success("Successfully registered!");
    }

    private void Unregister(CallbackContext callbackContext)
    {
        pushConnector = null;
        isRegistered = false;

    }

    private void getIsSandboxModeOn(CallbackContext callbackContext)
    {
        callbackContext.error("Not implemented in Android version");
    }

    private void getVersion(CallbackContext callbackContext)
    {
        callbackContext.error("Not implemented in Android version");
    }

    private void setShouldWipeBadgeNumber(CallbackContext callbackContext)
    {
        callbackContext.error("Not implemented in Android version");
    }

    private void getShouldWipeBadgeNumber(CallbackContext callbackContext)
    {
        callbackContext.error("Not implemented in Android version");
    }

    private void getDeviceInfo(CallbackContext callbackContext)
    {
        if (!isRegistered){
            callbackContext.error("Please call register function first");
        }

        HashMap<String, String> deviceInfo = pushConnector.getDeviceInfo();

        JSONObject devInfo = new JSONObject(deviceInfo);

        callbackContext.success(devInfo.toString());
    }

    private void setLocationEnabled(CallbackContext callbackContext)
    {
        callbackContext.error("Not implemented in Android version");
    }

    private void setAskForLocationPermission(CallbackContext callbackContext)
    {
       callbackContext.error("Not implemented in Android");
    }

    private void hitTag(JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (!isRegistered){
            callbackContext.error("Please call register function first");
        }

        if (data.getString(0) == null){
            callbackContext.error("Please provide tag");
            return;
        }

        String tag =  data.getString(0);

        pushConnector.hitTag(tag);

        callbackContext.success();
    }

    private void hitImpression(JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (!isRegistered){
            callbackContext.error("Please call register function first");
        }

        if (data.getString(0) == null){
            callbackContext.error("Please provide impression");
            return;
        }

        String impression =  data.getString(0);

        pushConnector.hitImpression(impression);

        callbackContext.success();
    }

    private void showPushListController(CallbackContext callbackContext)
    {
        if (!isRegistered){
            callbackContext.error("Please call register function first");
        }
        Intent intent = new Intent(this.getApplicationContext(), XPushLogActivity.class);
        this.cordova.getActivity().startActivity(intent);
        callbackContext.success();
    }

    private void getPushNotificationOffset(JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (!isRegistered){
            callbackContext.error("Please call register function first");
        }

        Integer offset = (!data.isNull(0)) ? data.getInt(0) : 0;
        Integer limit = (!data.isNull(1)) ? data.getInt(1) : 0;


        PushConnector.registerInEventBus(this);
        _callbackContext = callbackContext;

        pushConnector.getPushlist(offset,limit);
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
                        extras.putBoolean("foreground", true);
                        sendExtras(extras);
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
                Log.v(TAG, "sendExtras: caching extras to send at a later time.");
                cachedExtras = extras;
            }
        }
    }

   /*
    * Sends a json object to the client as parameter to a method which is defined in gECB.
    */
    public static void sendJavascript(JSONObject _json) {

        String _d = "javascript:" + callback_function + "(" + _json.toString() + ")";
        Log.v(TAG, "sendJavascript: " + _d);

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
                }
            } // while
            json.put("payload", jsondata);

            Log.v(TAG, "extrasToJSON: " + json.toString());

            return json;
        }
        catch( JSONException e)
        {
            Log.e(TAG, "extrasToJSON: JSON exception");
        }
        return null;
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
        super.onPause(multitasking);
        inForeground = false;
    }

    /*
     * When application will go to onResume state
     */
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        inForeground = true;
    }

    /*
     * when application goes to onDestroy state
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        inForeground = false;
        callback_function = null;
        webView = null;
    }

    /*
     * event will be called, after getPushMessagesList called
     */
    @Subscribe
    public void consumeEventList(EventsPushlistWrapper pushmessageListItems) {

        JSONArray json = new JSONArray(pushmessageListItems.getEventPushlist());
        String s = json.toString();
        PushConnector.unregisterInEventBus(this);
        _callbackContext.success(s);

        Log.d(TAG, "List received");
    }

}
