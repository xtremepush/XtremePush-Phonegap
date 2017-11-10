package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.model.Message;
import ie.imobile.extremepush.api.model.events.CloseInAppEvent;
import ie.imobile.extremepush.api.model.events.InAppActionDeliveredEvent;
import ie.imobile.extremepush.api.model.events.WebViewActionButtonClickEvent;
import ie.imobile.extremepush.api.model.events.WebViewRedeemEvent;
import ie.imobile.extremepush.util.LogEventsUtils;

/**
 * Created by vitaliishastun on 1/16/15.
 */

public class PopupDialog {
    private static final String TAG = PopupDialog.class.getSimpleName();
    private final PopupWindow mMessage;
    private static int statusBarHeight;
    private static int width;
    private static int height;
    private static int sb_size;
    private static int sb_size_w = 0;
    private static int mOrientation;
    private static WeakReference<Activity> mActivityHolder;
    private static boolean refreshing = false;
    public static Message pm;
    private static boolean mNotClosed;
    private static boolean sb = false;
    private static Integer visibilityMask = null;
    private static WebView inAppWebView;
    private static String slideInFrom = "";
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int fade = 0;
    private static final int FADE_DURATION = 200;
    private static final int TRANSLATE_DURATION = 1000;
    private static boolean inMultiWindow = false;
    private static WeakReference<View> decorView;
//    //Used with JavascriptInterface code at bottom of file
//    private static String messageType = "";

    public PopupDialog(PopupWindow popupMessage) {
        mMessage = popupMessage;
    }

    public static int convertDpToPixel(Double dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static Double convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (px / (metrics.densityDpi / 160d));
    }

    private static boolean shouldRefresh() {
        Activity activity = mActivityHolder.get();
        View decorView = activity.findViewById(android.R.id.content);
        int orientation;
        int width;
        int height;
        int sb_size;
        int sb_size_w = 0;
        boolean inMultiWindow = false;
        int newStatusBarHeight;

        Rect rectangle = new Rect();
        decorView.getWindowVisibleDisplayFrame(rectangle);
        newStatusBarHeight = rectangle.top;

        int mRight = rectangle.right;
        int mLeft = rectangle.left;

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        int availPlusNav = metrics.heightPixels;

        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 24) {
//            inMultiWindow = mActivityHolder.get().isInMultiWindowMode();
        }

        if (Build.VERSION.SDK_INT >= 17) {
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
        sb_size = (realHeight - availPlusNav) + newStatusBarHeight;

        if (inMultiWindow) {
            if (Build.VERSION.SDK_INT >= 24) {
                Point size = new Point(0, 0);
                display.getSize(size);
                width = size.x + newStatusBarHeight;
                height = size.y;
                height = height - newStatusBarHeight;
                sb_size = (height - availPlusNav);
            }
        }

        orientation = display.getRotation();

        if ((mRight < width) || (mLeft > 0)) {
            sb_size_w = mLeft + (width - mRight);
        }

        // If there have been any changes in the display size, orientation, the multi-window mode or
        // the status bars, then return true, otherwise return false
        return mNotClosed && (inMultiWindow != PopupDialog.inMultiWindow ||
                width != PopupDialog.width ||
                height != PopupDialog.height ||
                sb_size != PopupDialog.sb_size ||
                sb_size_w != PopupDialog.sb_size_w ||
                orientation != mOrientation);
    }

    private static String calculateDimensions() {
        Activity activity = mActivityHolder.get();
        View decorView = activity.findViewById(android.R.id.content);
        int orientation;
        int sb_collapsible;
        Rect rectangle = new Rect();
        decorView.getWindowVisibleDisplayFrame(rectangle);
        statusBarHeight = rectangle.top;
        int mRight = rectangle.right;
        int mLeft = rectangle.left;

        Display display = activity.getWindowManager().getDefaultDisplay();
        int availPlusNav = rectangle.bottom;

        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 24) {
            inMultiWindow = mActivityHolder.get().isInMultiWindowMode();
        }

        if (Build.VERSION.SDK_INT >= 17) {
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

        switch (display.getRotation()) {
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

        if ((mRight < width) || (mLeft > 0)) {
            sb_size_w = mLeft + (width - mRight);
        } else
            sb_size_w = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !inMultiWindow) {
            sb_collapsible = 1;
        } else {
            sb_collapsible = 0;
        }

        if (inMultiWindow) {
            orientation = 0;
            if (Build.VERSION.SDK_INT >= 24) {
                Point size = new Point(0, 0);
                display.getSize(size);
                width = mRight - mLeft;
                height = availPlusNav;
                if (statusBarHeight > (height / 3)) {
                    sb_size = 0;
                    height = availPlusNav - statusBarHeight;
                    statusBarHeight = 0;
                } else {
                    sb_size = statusBarHeight;
                }
                sb_size_w = 0;
            }
        }

        LogEventsUtils.sendLogTextMessage(TAG, orientation + " --- " + PopupDialog.convertPixelsToDp(width, activity) +
                " --- " + PopupDialog.convertPixelsToDp(height, activity) + " --- " + PopupDialog.convertPixelsToDp(sb_size, activity) +
                " --- " + sb_collapsible + " --- " + PopupDialog.convertPixelsToDp(sb_size_w, activity));
        return "javascript:InAppMessage.render(" + orientation + ", " + PopupDialog.convertPixelsToDp(width, activity) + ", " + PopupDialog.convertPixelsToDp(height, activity) +
                ", " + PopupDialog.convertPixelsToDp(sb_size, activity) + ", " + sb_collapsible + ", " + PopupDialog.convertPixelsToDp(sb_size_w, activity) + ");";
    }

    /**
     *  A class to set popupDialog to null,
     *
     */
    public static void dontShowInApp(Activity activity){
//        mActivityHolder = new WeakReference<Activity>(null);
//        inAppWebView = null;

        if(inAppWebView!=null)
            destroyWebview();
    }

    public static void destroyWebview() {

        inAppWebView.clearHistory();
        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        inAppWebView.clearCache(true);
        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        inAppWebView.loadUrl("about:blank");
        inAppWebView.onPause();
        inAppWebView.removeAllViews();
        inAppWebView.destroyDrawingCache();

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        inAppWebView.destroy();

        // Null out the reference so that you don't end up re-using it.
        inAppWebView = null;
    }

    public static PopupDialog showInApp(final Activity activity, final Message pm, final boolean refreshFlag) {
        LogEventsUtils.sendLogTextMessage(TAG, "PopupDialog ShowInApp");
        mNotClosed = true;
        refreshing = refreshFlag;
        mActivityHolder = new WeakReference<>(activity);
        PopupDialog.pm = pm;
        decorView = new WeakReference<>(activity.findViewById(android.R.id.content));
        View container = activity.getLayoutInflater().inflate(R.layout.big_banner, null);
        inAppWebView = (WebView) container.findViewById(R.id.banner_webview);
//        //Used with JavascriptInterface code at bottom of file
//        inAppWebView.addJavascriptInterface(new PopupInterface(), "AndroidApp");
        final int uiOptions;

        inAppWebView.getSettings().setJavaScriptEnabled(true);
        inAppWebView.loadUrl(pm.inapp);
        inAppWebView.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            inAppWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        final MyPopupWindow popupMessage = new MyPopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupMessage.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popupMessage.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogEventsUtils.sendLogTextMessage(TAG, "onDismissListener hit");
                if (!shouldRefresh()) {
                    mNotClosed = false;
                    PushConnector.postInEventBus(new CloseInAppEvent());
                    if (inAppWebView != null ) {
                        inAppWebView.loadUrl("javascript:InAppMessage.dispatched();");
                    } else
                        LogEventsUtils.sendLogTextMessage(TAG, "InAppWebView is null");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && decorView.get() != null) {
                        if (!sb && (visibilityMask != null)) {
                            decorView.get().setSystemUiVisibility(visibilityMask);
                        }
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && decorView.get() != null) {
            int tempOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                tempOptions = tempOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                tempOptions = tempOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            uiOptions = tempOptions;
            visibilityMask = decorView.get().getSystemUiVisibility();
        } else {
            uiOptions = 0;
            visibilityMask = 0;
        }

        final WebViewClient popupViewClient = new WebViewClient() {
            private int width = 0;
            private int height = 0;
            private int x = 0;
            private int y = 0;
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
                            slideInFrom = parsed.getQueryParameter("slide");
                            fade = Integer.parseInt(parsed.getQueryParameter("fade"));

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
                        //use Layer type hardware to make the inApp animation smooth
                        if (Build.VERSION.SDK_INT > 11) {
                            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        }
                        final View mDecorView = mActivityHolder.get().findViewById(android.R.id.content);
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            if (!sb) {
                                imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0, new keyboardReceiver(mDecorView, uiOptions));
                                if (focusable) {
                                    popupMessage.setFocusable(true);
                                }
                                animateIn(view);
                                popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y);
                                if (mDecorView.getSystemUiVisibility() != uiOptions)
                                    mDecorView.setSystemUiVisibility(uiOptions);
                            } else {
                                if (focusable) {
                                    imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
                                    popupMessage.setFocusable(true);
                                }
                                popupMessage.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

                                animateIn(view);
                                popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y + statusBarHeight);
                            }
                        } else {
                            if (focusable) {
                                imm.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
                                popupMessage.setFocusable(true);
                            }
                            popupMessage.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

                            animateIn(view);
                            popupMessage.showAtLocation(mDecorView, Gravity.NO_GRAVITY, x, y + statusBarHeight);
                        }

                        if (!refreshing)
                            PushConnector.postInEventBus(new InAppActionDeliveredEvent(pm));

                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        return true;
                    } else if (url.contains("inapp://action")) {
                        try {
                            String u = parsed.getQueryParameter("url");
                            String deeplink = parsed.getQueryParameter("deeplink");
                            String inapp = parsed.getQueryParameter("inapp");
                            String button = parsed.getQueryParameter("action");
                            PushConnector.postInEventBus(new WebViewActionButtonClickEvent(pm.id, u, deeplink, inapp, button, Message.OPEN, false, pm.data.toString()));
                        } catch (Exception e) {
                            LogEventsUtils.sendLogTextMessage(TAG, "InApp action failed.");
                        }
                        mNotClosed = false;
                        popupMessage.dismiss();
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && decorView.get() != null) {
                            if (!sb) {
                                decorView.get().setSystemUiVisibility(visibilityMask);
                            }
                        }
                        return true;
                    } else if (url.contains("inapp://redeem")) {
                        PushConnector.postInEventBus(new WebViewRedeemEvent(pm.id));
                        view.loadUrl("javascript:InAppMessage.dispatched();");
                        return true;
                    } else if (url.contains("inapp://close")) {
                        //call slide out animation
                        animateOut(view);
                        String button = parsed.getQueryParameter("action");
                        PushConnector.postInEventBus(new WebViewActionButtonClickEvent(pm.id, null, null, null, button, Message.CLOSE, false, pm.data.toString()));
                        mNotClosed = false;

//                        popupMessage.dismiss();
                        Handler h = new Handler();
                        //have short delay for animation to happen before popup dismissed
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                popupMessage.dismiss();
                            }
                        }, 1000);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && decorView.get() != null) {
                            if (!sb) {
                                decorView.get().setSystemUiVisibility(visibilityMask);
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
//                //type of message
//                view.loadUrl("javascript:if((typeof InAppBannerMessage) == 'object'){AndroidApp.isBanner();}");
//                view.loadUrl("javascript:if((typeof InAppEditorMessage) == 'object'){AndroidApp.isModal();}");
//                view.loadUrl("javascript:if((typeof InAppFullsizeMessage) == 'object'){AndroidApp.isFullScreen();}");
                animateIn(view);
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
        inAppWebView.setWebViewClient(popupViewClient);
        return new PopupDialog(popupMessage);
    }

    public static void redemptionResult(boolean success, int code) {
        if (success)
            inAppWebView.loadUrl("javascript:InAppMessage.redeemSuccess();");
        else
            inAppWebView.loadUrl("javascript:InAppMessage.redeemFailure(" + code + ");");
    }

    public boolean pause() {
        LogEventsUtils.sendLogTextMessage(TAG, "pause function called from manager");
        if (shouldRefresh()) {
            refreshing = true;
        } else {
            refreshing = false;
            mNotClosed = false;
        }
        return refreshing;
    }

    public void dismiss() {
        LogEventsUtils.sendLogTextMessage(TAG, "dismiss function called from manager");
        mMessage.dismiss();
    }

    public static class MyPopupWindow extends PopupWindow {
        public MyPopupWindow(View container, int width,
                             int height) {
            super(container, width, height);
        }

        @Override
        public void showAtLocation(final View parent, final int gravity, final int x, final int y) {
            parent.post(new Runnable(){
                public void run(){
                    if(parent.getWindowToken() != null){
                        MyPopupWindow.super.showAtLocation(parent, gravity, x, y);
                    }
                }
            });
        }
    }

    static class keyboardReceiver extends ResultReceiver {
        private View decorView;
        private int uiOptions;
        public final Parcelable.Creator<ResultReceiver> CREATOR = ResultReceiver.CREATOR;

        public keyboardReceiver(View view, int options) {
            super(null);
            decorView = view;
            uiOptions = options;
        }

        public void onReceiveResult(int resultCode, Bundle resultData) {
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

    public static void animateIn(final WebView wView) {
        //get variables needed for translate animation
        if (screenWidth == 0 || screenHeight == 0)
            getScreen();
        int x = 0;
        int y = 0;
        if (slideInFrom.equals("from-right"))
            x = screenWidth;

        if (slideInFrom.equals("from-left"))
            x = screenWidth * -1;

        if (slideInFrom.equals("from-top"))
            y = screenHeight * -1;

        if (slideInFrom.equals("from-bottom"))
            y = screenHeight;

        //slideIn animation
        TranslateAnimation tAnimation = new TranslateAnimation(x, 0, y, 0);
        tAnimation.setDuration(TRANSLATE_DURATION);
        tAnimation.setFillAfter(true);

        //fade animation
        AlphaAnimation fAnimation = new AlphaAnimation((Math.abs(fade - 1)), 1);
        fAnimation.setDuration(FADE_DURATION);

        AnimationSet anim = new AnimationSet(true);
        anim.addAnimation(tAnimation);
        anim.addAnimation(fAnimation);

        //view layer type was set to hardware up above, it is recommended to set it back to its original value when not being used
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //end of animation, stop using hardware layer type
            @Override
            public void onAnimationEnd(Animation animation) {
                //Stop using hardware after animation
                if (Build.VERSION.SDK_INT >= 11)
                    wView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        wView.startAnimation(anim);
    }

    //the animation to get the animation off screen
    public static void animateOut(final WebView wView) {
        //variables used to programatically define translate animation
        if (screenWidth == 0 || screenHeight == 0)
            getScreen();
        int x = 0;
        int y = 0;
        if (slideInFrom.equals("from-right"))
            x = screenWidth;
        if (slideInFrom.equals("from-left"))
            x = screenWidth * -1;
        if (slideInFrom.equals("from-top"))
            y = screenHeight * -1;
        if (slideInFrom.equals("from-bottom"))
            y = screenHeight;
        //set slideInFrom variable back to empty so next animation wont use it by accident
        slideInFrom = "";

        //slideIn animation
        TranslateAnimation tAnimation = new TranslateAnimation(0, x, 0, y);
        tAnimation.setDuration(TRANSLATE_DURATION);
        tAnimation.setFillAfter(true);

        //fade animation
        AlphaAnimation fAnimation = new AlphaAnimation(1, (Math.abs(fade - 1)));
        fAnimation.setDuration(FADE_DURATION);

        AnimationSet anim = new AnimationSet(true);
        anim.addAnimation(tAnimation);
        anim.addAnimation(fAnimation);

        wView.startAnimation(anim);
    }

    public static void getScreen() {
        if (Build.VERSION.SDK_INT >= 13) {
            WindowManager wm = (WindowManager) mActivityHolder.get().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        }
    }

////    Class used to determine the type of an inApp message
////    To use the class must be added to the webview, e.g. inAppWebView.addJavascriptInterface(new PopupInterface(), "AndroidApp");
////    Following lines of code make callbacks to the javascript interface in this class
//    view.loadUrl("javascript:if((typeof InAppBannerMessage) == 'object'){AndroidApp.isBanner();}");
//    view.loadUrl("javascript:if((typeof InAppEditorMessage) == 'object'){AndroidApp.isModal();}");
//    view.loadUrl("javascript:if((typeof InAppFullsizeMessage) == 'object'){AndroidApp.isFullScreen();}");
//    private static class PopupInterface {
//        PopupInterface() {
//        }
//
//        @JavascriptInterface
//        public void isBanner() {
//            messageType = "Banner";
//        }
//
//        @JavascriptInterface
//        public void isFullScreen() {
//            messageType = "Fullscreen";
//        }
//
//        @JavascriptInterface
//        public void isModal() {
//            messageType = "Modal";
//        }
//
//        @JavascriptInterface
//        public void testInterface(String c) {
//            LogEventUtils.sendLogTextMessage(TAG, "InterfaceWorking " + c);
//        }
//    }
}

