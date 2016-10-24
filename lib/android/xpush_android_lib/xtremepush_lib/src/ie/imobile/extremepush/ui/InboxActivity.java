package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.protocol.HTTP;

import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.model.InboxMessage;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.network.ConnectionManager;
import ie.imobile.extremepush.util.BusProvider;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;

import android.content.res.Resources;

import java.io.ByteArrayOutputStream;

public class InboxActivity extends Activity {
    private WebView webView;
    private static final String TAG = InboxActivity.class.getSimpleName();
    private static boolean rightHandSide = true;
    private String params;
    private static Intent data;

    public String getBase64IconString(){
        Resources appR = this.getResources();
        int notificationIcon = this.getApplicationContext().getApplicationInfo().icon;
        if(SharedPrefUtils.getIcon(this.getApplicationContext()) != null) {
            int tmp = appR.getIdentifier(SharedPrefUtils.getIcon(this.getApplicationContext()), "drawable", this.getApplicationContext().getPackageName());
            if (tmp != 0)
                notificationIcon = tmp;
        }
        Bitmap backup = BitmapFactory.decodeResource(appR, notificationIcon);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        backup.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte [] ba = bos.toByteArray();
        String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
        return ba1;
    }

    private void closeInbox(){
        webView.loadUrl("javascript: try { var cache = Inbox.getCache(); var badge = Inbox.getBadge(); InboxJavaCallback.returnMessages(cache, badge); } catch (err) { InboxJavaCallback.messageFail(err.message); }");
    }

    public class InboxInterface {

        InboxInterface(){}

        @JavascriptInterface
        public void returnMessages(String messages, String badge) {
            LogEventsUtils.sendLogTextMessage(TAG, "Badge: " + badge + ",  messages: " + messages);
            SharedPrefUtils.setInboxMessages(messages, InboxActivity.this.getApplicationContext());
            String old = String.valueOf(SharedPrefUtils.getInboxBadge(InboxActivity.this.getApplicationContext()));
            if(!TextUtils.isEmpty(badge) && !TextUtils.equals(old, badge)) {
                SharedPrefUtils.setInboxBadge(badge, InboxActivity.this.getApplicationContext());
                data.putExtra("badgeRefresh", 1);
                if (InboxActivity.this.getParent() == null) {
                    InboxActivity.this.setResult(Activity.RESULT_OK, data);
                } else {
                    InboxActivity.this.getParent().setResult(Activity.RESULT_OK, data);
                }
            }
            finish();
        }

        @JavascriptInterface
        public void returnPosition(String position) {
            rightHandSide = !TextUtils.equals(position, "left");
            LogEventsUtils.sendLogTextMessage(TAG, position);
        }

        @JavascriptInterface
        public void messageFail(String message) {
            LogEventsUtils.sendLogTextMessage(TAG, "JavaScript error: " + message);
            finish();
        }

        @JavascriptInterface
        public void messageWarn(String message) {
            LogEventsUtils.sendLogTextMessage(TAG, "JavaScript warning: " + message);
        }
    }

    private class InboxClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                LogEventsUtils.sendLogTextMessage(TAG, url);
                Uri parsed = Uri.parse(url);
                if (url.contains("inbox://close")) {
                    closeInbox();
                    return true;
                } else if (url.contains("inbox://action")){
                    String pushActionId = parsed.getQueryParameter("id");
                    String u = parsed.getQueryParameter("u");
                    String um = parsed.getQueryParameter("um");
                    String button = parsed.getQueryParameter("button");
                    if(TextUtils.isEmpty(u)){
                        if(!TextUtils.isEmpty(pushActionId))
                            ConnectionManager.getInstance().hitAction(InboxActivity.this.getApplicationContext(), pushActionId, 1);
                        return true;
                    }
                    data.putExtra("pushActionId", pushActionId);
                    data.putExtra("u", u);
                    data.putExtra("um", um);
                    data.putExtra("button", button);
                    data.putExtra("open", PushMessage.OPEN);
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, data);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, data);
                    }
                    closeInbox();
                    return true;
                } else if (url.contains("inbox://subscription")){
                    String subStatus = parsed.getQueryParameter("status");
                    LogEventsUtils.sendLogTextMessage(TAG, "Subscription: " + subStatus);
                    if(!TextUtils.isEmpty(subStatus)){
                        SharedPrefUtils.setSubscriptionStatus(subStatus, InboxActivity.this.getApplicationContext());
                        ConnectionManager.getInstance().updateDevice(InboxActivity.this.getApplicationContext());
                    }
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String messages = SharedPrefUtils.getInboxMessages(InboxActivity.this.getApplicationContext());
            if(!TextUtils.isEmpty(messages)){
                view.loadUrl("javascript: try { Inbox.setCache(" + messages + "); } catch (err) { InboxJavaCallback.messageWarn(err.message); }");
            }
            if(!TextUtils.isEmpty(params))
                view.loadUrl("javascript: try { Inbox.setDeviceParams(" + params + "); } catch (err) { InboxJavaCallback.messageWarn(err.message); }");
            view.loadUrl("javascript: try { Inbox.launch(); } catch (err) { InboxJavaCallback.messageFail(err.message); }");
            view.loadUrl("javascript: try { var result = Inbox.getPosition(); InboxJavaCallback.returnPosition(result); } catch (err) { InboxJavaCallback.messageWarn(err.message); }");
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK){
            webView.loadUrl("javascript: try { var result = Inbox.close(); } catch (err) { InboxJavaCallback.messageFail(err.message); }");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void showInbox(InboxMessage inbox){
        BusProvider.getBus().unregister(this);
        if(inbox == null || TextUtils.isEmpty(inbox.mInbox)){
            if(!TextUtils.isEmpty(SharedPrefUtils.getInboxHtml(this))){
                showInbox(SharedPrefUtils.getInboxHtml(this));
            } else {
                LogEventsUtils.sendLogTextMessage(TAG, "Could not retrieve inbox from server and no cached version on device");
                finish();
            }
        } else {
            showInbox(inbox.mInbox);
            SharedPrefUtils.setInboxHtml(inbox.mInbox, this);
        }
    }

    public void showInbox(String inbox){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            webView.loadDataWithBaseURL(SharedPrefUtils.getInboxUrl(this), inbox, null, HTTP.UTF_8, null);
        else
            webView.loadData(inbox, "", HTTP.UTF_8);
        webView.setBackgroundColor(Color.TRANSPARENT);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        data = new Intent();
        webView = (WebView) findViewById(R.id.inbox_webview);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView.clearCache(false);
        final GestureDetector gestureDetector = new GestureDetector(this, new OnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) { return false; }

            @Override
            public void onShowPress(MotionEvent e) { }

            @Override
            public boolean onSingleTapUp(MotionEvent e) { return false; }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

            @Override
            public void onLongPress(MotionEvent e) { }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                if(rightHandSide) {
                                    LogEventsUtils.sendLogTextMessage(TAG, "Slide right");
                                    webView.loadUrl("javascript: try { var result = Inbox.close(); } catch (err) { InboxJavaCallback.messageFail(err.message); }");
                                }
                            } else {
                                if(!rightHandSide) {
                                    LogEventsUtils.sendLogTextMessage(TAG, "Slide left");
                                    webView.loadUrl("javascript: try { var result = Inbox.close(); } catch (err) { InboxJavaCallback.messageFail(err.message); }");
                                }
                            }
                        }
                        result = true;
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
//                            LogEventsUtils.sendLogTextMessage(TAG, "Slide bottom");
                        } else {
//                            LogEventsUtils.sendLogTextMessage(TAG, "Slide top");
                        }
                    }
                    result = true;
                } catch (Exception exception) {
                    LogEventsUtils.sendLogErrorMessage(TAG, exception);
                }
                return result;
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        webView.setVisibility(View.GONE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InboxInterface(), "InboxJavaCallback");
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebViewClient(new InboxClient());
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        params = "";
        try {
            boolean addressable = !TextUtils.isEmpty(SharedPrefUtils.getRegistrationId(this));
            boolean subscribed = TextUtils.equals(SharedPrefUtils.getSubscriptionStatus(this),"1");
            JSONObject jsonEntity = new JSONObject();
            jsonEntity.put("addressable", addressable);
            jsonEntity.put("subscription", subscribed);
            jsonEntity.put("id", SharedPrefUtils.getServerDeviceId(this));
            jsonEntity.put("key", SharedPrefUtils.getDeviceKey(this));
            if(!TextUtils.isEmpty(getBase64IconString()))
                jsonEntity.put("backupImage", "data:image/png;base64," + getBase64IconString());
            params = jsonEntity.toString();
        } catch (JSONException e) {
            LogEventsUtils.sendLogErrorMessage(TAG, e);
        }

        String html = SharedPrefUtils.getInboxHtml(this);
        if(TextUtils.isEmpty(html)) {
            if(ConnectionManager.getInstance().getRegistered()) {
                BusProvider.getBus().register(this);
                ConnectionManager.getInstance().getInbox(this);
            } else {
                Toast.makeText(this.getApplicationContext(), SharedPrefUtils.getInboxUnavailableMessage(this) , Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            showInbox(html);
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onResume() {
        overridePendingTransition(0, 0);
        super.onResume();
    }

}