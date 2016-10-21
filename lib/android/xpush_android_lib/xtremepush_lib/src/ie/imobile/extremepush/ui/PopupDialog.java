package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
//import android.webkit.WebResourceError;
//import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import android.os.Handler;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.api.model.events.CloseInAppEvent;
import ie.imobile.extremepush.api.model.events.InAppActionButtonClickEvent;
import ie.imobile.extremepush.api.model.events.InAppActionDeliveredEvent;
import ie.imobile.extremepush.util.LogEventsUtils;

/**
 * Created by vitaliishastun on 1/16/15.
 */
public class PopupDialog {
    private static final String TAG = PopupDialog.class.getSimpleName();
    private final PopupWindow mMessage;
    private static int statusBarHeight;
    private static int mOrientation;
    private static WeakReference<Activity> mActivityHolder;
    private static boolean refreshing = false;
    public static PushMessage pm;
    private static boolean mNotClosed;

    public PopupDialog(PopupWindow popupMessage) {
        mMessage = popupMessage;
    }

    public static int convertDpToPixel(Double dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int)(dp * (metrics.densityDpi / 160f));
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static Double convertPixelsToDp(int px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (px / (metrics.densityDpi / 160d));
    }

    public static String calculateDimensions(){
        Activity activity = mActivityHolder.get();
        View decorView = activity.findViewById(android.R.id.content);
        int orientation;
        int width;
        int height;
        int sb_size;
        int sb_collapsible;
        Rect rectangle = new Rect();
        decorView.getWindowVisibleDisplayFrame(rectangle);
        statusBarHeight = rectangle.top;

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        int availPlusNav = metrics.heightPixels;

        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH1 = Display.class.getMethod("getRawHeight");
                Method mGetRawW1 = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW1.invoke(display);
                realHeight = (Integer) mGetRawH1.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                LogEventsUtils.sendLogTextMessage("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        width = realWidth;
        height = realHeight;
        sb_size = (realHeight - availPlusNav) + statusBarHeight;
        switch (display.getRotation()){
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_270:
                orientation = -90;
                break;
            default:
                orientation = 0;
                break;
        }
        mOrientation = display.getRotation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            sb_collapsible = 1;
        } else {
            sb_collapsible = 0;
        }

//        LogEventsUtils.sendLogTextMessage(TAG, orientation + " --- " + PopupDialog.convertPixelsToDp(width, activity) +
//                " --- " + PopupDialog.convertPixelsToDp(height, activity) + " --- " + PopupDialog.convertPixelsToDp(sb_size, activity) + " --- " + sb_collapsible);
        return "javascript:InAppMessage.render("+orientation+", "+PopupDialog.convertPixelsToDp(width, activity)+", "+PopupDialog.convertPixelsToDp(height, activity)+
                ", "+PopupDialog.convertPixelsToDp(sb_size, activity)+", "+sb_collapsible+");";
    }

    public static PopupDialog showInApp(final Activity activity, final PushMessage pm, final boolean refreshFlag) {
        mNotClosed = true;
        refreshing = refreshFlag;
        mActivityHolder = new WeakReference<>(activity);
        PopupDialog.pm = pm;
        final View decorView = activity.findViewById(android.R.id.content);
        View container = activity.getLayoutInflater().inflate(R.layout.big_banner, null);
        final WebView layoutOfPopup = (WebView) container.findViewById(R.id.banner_webview);
        final int uiOptions;
        final int visibilityMask;

        layoutOfPopup.getSettings().setJavaScriptEnabled(true);
        layoutOfPopup.loadUrl(pm.url);
        layoutOfPopup.setBackgroundColor(Color.TRANSPARENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            layoutOfPopup.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        final MyPopupWindow popupMessage = new MyPopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupMessage.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popupMessage.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogEventsUtils.sendLogTextMessage(TAG, "onDismissListener hit");
                Display display = mActivityHolder.get().getWindowManager().getDefaultDisplay();
                if (display.getRotation() == mOrientation){
                    PushConnector.postInEventBus(new CloseInAppEvent());
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int tempOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                tempOptions = tempOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                tempOptions = tempOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            uiOptions = tempOptions;
            visibilityMask = decorView.getSystemUiVisibility();
        } else {
            uiOptions = 0;
            visibilityMask = 0;
        }

        final WebViewClient popupViewClient = new WebViewClient() {
            private int width = 0;
            private int height = 0;
            private int x = 0;
            private int y = 0;
            private boolean sb = false;
            private boolean focusable = false;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogEventsUtils.sendLogTextMessage(TAG, "InApp webview event: " + url);
                if (!TextUtils.isEmpty(url)) {
                    Uri parsed = Uri.parse(url);
                    if (url.contains("inapp://position")) {
                        try {
                            String tempParam = parsed.getQueryParameter("sb");
                            if (!TextUtils.isEmpty(tempParam)) {
                                sb = (Integer.parseInt(tempParam) == 1);
                            }
                            tempParam = parsed.getQueryParameter("input");
                            if (!TextUtils.isEmpty(tempParam)) {
                                focusable = (Integer.parseInt(tempParam) == 1);
                            }
                            tempParam = parsed.getQueryParameter("height");
                            if (!TextUtils.isEmpty(tempParam)) {
                                height = PopupDialog.convertDpToPixel(Double.parseDouble(tempParam), activity);
                                popupMessage.setHeight(height);
                            }
                            tempParam = parsed.getQueryParameter("width");
                            if (!TextUtils.isEmpty(tempParam)) {
                                width = PopupDialog.convertDpToPixel(Double.parseDouble(tempParam), activity);
                                popupMessage.setWidth(width);
                            }
                            String xParam = parsed.getQueryParameter("x");
                            String yParam = parsed.getQueryParameter("y");
                            if (!(TextUtils.isEmpty(xParam) && TextUtils.isEmpty(yParam))) {
                                x = PopupDialog.convertDpToPixel(Double.parseDouble(xParam), activity);
                                y = PopupDialog.convertDpToPixel(Double.parseDouble(yParam), activity);
                            }
                        } catch (Exception e) {
                            LogEventsUtils.sendLogTextMessage(TAG, "InApp position setting failed.");
                            x = 0;
                            y = 0;
                            width = 0;
                            height = 0;
                        }
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        return true;
                    } else if (url.contains("inapp://ready")) {
                        View mDecorView = mActivityHolder.get().findViewById(android.R.id.content);
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            if (!sb) {
                                imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0, new keyboardReceiver(mDecorView, uiOptions));
                                if(focusable) {
                                    popupMessage.setFocusable(true);
                                }
                                popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y);
                                if(mDecorView.getSystemUiVisibility() != uiOptions)
                                    mDecorView.setSystemUiVisibility(uiOptions);
                            } else {
                                if (focusable) {
                                    imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
                                    popupMessage.setFocusable(true);
                                }
                                popupMessage.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                                popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y + statusBarHeight);
                            }
                        } else {
                            if(focusable) {
                                imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
                                popupMessage.setFocusable(true);
                            }
                            popupMessage.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                            popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y + statusBarHeight);
                        }
                        if(!refreshing)
                            PushConnector.postInEventBus(new InAppActionDeliveredEvent(pm));
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        return true;
                    } else if (url.contains("inapp://action")) {
                        try {
                            String u = parsed.getQueryParameter("u");
                            String um = parsed.getQueryParameter("um");
                            String button = parsed.getQueryParameter("button");
                            PushConnector.postInEventBus(new InAppActionButtonClickEvent(pm, u, um, button, PushMessage.OPEN));
                        } catch (Exception e) {
                            LogEventsUtils.sendLogTextMessage(TAG, "InApp action failed.");
                        }
                        mNotClosed = false;
                        popupMessage.dismiss();
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            if (!sb) {
                                decorView.setSystemUiVisibility(visibilityMask);
                            }
                        }
                        return true;
                    } else if (url.contains("inapp://close")) {
                        String button = parsed.getQueryParameter("button");
                        PushConnector.postInEventBus(new InAppActionButtonClickEvent(pm, null, null, button, PushMessage.CLOSE));
                        mNotClosed = false;
                        popupMessage.dismiss();
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            if (!sb) {
                                decorView.setSystemUiVisibility(visibilityMask);
                            }
                        }
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl(PopupDialog.calculateDimensions());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LogEventsUtils.sendLogTextMessage(TAG, "Error loading in-app message: " + description);
                mNotClosed = false;
                popupMessage.dismiss();
                PushConnector.postInEventBus(new CloseInAppEvent());
            }
        };
        layoutOfPopup.setWebViewClient(popupViewClient);
        return new PopupDialog(popupMessage);
    }

    public boolean dismiss() {
        LogEventsUtils.sendLogTextMessage(TAG, "dismiss function called from manager");
        Display display = mActivityHolder.get().getWindowManager().getDefaultDisplay();
        if ((display.getRotation() != mOrientation) && (mNotClosed)){
            refreshing = true;
        } else {
            refreshing = false;
//            PushConnector.postInEventBus(new CloseInAppEvent());
        }
        mMessage.dismiss();

        return refreshing;
    }

    public static class MyPopupWindow extends PopupWindow {
        public MyPopupWindow(View container, int width,
                      int height){
            super(container, width, height);
        }

        @Override
        public void showAtLocation(View parent, int gravity, int x, int y){
//            this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//            this.setFocusable(true);
            super.showAtLocation(parent, gravity, x, y);
//            this.update();
        }
    }

    static class keyboardReceiver extends ResultReceiver
    {
        private View decorView;
        private int uiOptions;
        public final Parcelable.Creator<ResultReceiver> CREATOR = ResultReceiver.CREATOR;
        public keyboardReceiver(View view, int options)
        {
            super(null);
            decorView = view;
            uiOptions = options;
        }
        public void onReceiveResult(int resultCode, Bundle resultData)
        {
            // Get a handler that can be used to post to the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                        decorView.setSystemUiVisibility(uiOptions);
                }
            };
            mainHandler.post(myRunnable);
        }
    }
}
