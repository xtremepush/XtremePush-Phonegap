package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.api.model.events.BusEvent;
import ie.imobile.extremepush.api.model.events.CloseInAppEvent;
import ie.imobile.extremepush.api.model.events.InAppActionButtonClickEvent;
import ie.imobile.extremepush.util.LogEventsUtils;

/**
 * Created by vitaliishastun on 1/16/15.
 */
public class PopupDialog {
    private static final String TAG = PopupDialog.class.getSimpleName();
    private final PopupWindow mMessage;

    public PopupDialog(PopupWindow popupMessage) {
        mMessage = popupMessage;
    }

    public static PopupDialog showTopBanner(Activity activity, PushMessage pm) {
        int offset = getStatusBarHeight(activity);
        int height = (int) (getScreenHeight(activity) * 0.25f);
        return showBanner(activity, pm, offset, height, Gravity.TOP);
    }

    public static PopupDialog showBottomBanner(Activity activity, PushMessage pm) {
        int offset = 0;
        int height = (int) (getScreenHeight(activity) * 0.25f);
        return showBanner(activity, pm, offset, height, Gravity.BOTTOM);
    }

    public static PopupDialog showCenterBanner(Activity activity, PushMessage pm) {
        int height = (int) (getScreenHeight(activity) * 0.25f);
        int offset = 0;
        return showBanner(activity, pm, offset, height, Gravity.CENTER);
    }

    public static PopupDialog showPopupBanner(Activity activity, PushMessage pm) {
        int offset = getStatusBarHeight(activity);
        int height = getScreenHeight(activity) - offset;
        return showBanner(activity, pm, offset, height, Gravity.TOP);
    }

    public static PopupDialog showScreenBanner(Activity activity, final PushMessage pm) {
        final View decorView = activity.findViewById(android.R.id.content);
        View container = activity.getLayoutInflater().inflate(R.layout.big_banner, null);
        ImageView closeBtn = (ImageView) container.findViewById(R.id.banner_close_btn);
        final WebView layoutOfPopup = (WebView) container.findViewById(R.id.banner_webview);

        layoutOfPopup.getSettings().setJavaScriptEnabled(true);
        layoutOfPopup.loadUrl(pm.source);
        final PopupWindow popupMessage = new PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutOfPopup.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogEventsUtils.sendLogTextMessage(TAG, "InApp webview event: " + url);
                if (url.contains("xtremepush://open_push")) {
                    PushConnector.postInEventBus(new InAppActionButtonClickEvent(pm));
                    PushConnector.postInEventBus(new CloseInAppEvent());
                    popupMessage.dismiss();
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                popupMessage.showAtLocation(decorView, Gravity.TOP, 0, 0);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushConnector.postInEventBus(new CloseInAppEvent());
                popupMessage.dismiss();
            }
        });
        return new PopupDialog(popupMessage);
    }

    private static PopupDialog showBanner(Activity activity, final PushMessage pm, final int offset, int height, final int gravity) {
        final View decorView = activity.findViewById(android.R.id.content);
        View container = activity.getLayoutInflater().inflate(R.layout.small_banner, null);
        ImageView closeBtn = (ImageView) container.findViewById(R.id.banner_close_btn);
        final WebView layoutOfPopup = (WebView) container.findViewById(R.id.banner_webview);

        layoutOfPopup.getSettings().setJavaScriptEnabled(true);
        layoutOfPopup.loadUrl(pm.source);
        final PopupWindow popupMessage = new PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT,
                height);
        layoutOfPopup.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogEventsUtils.sendLogTextMessage(TAG, "InApp webview event: " + url);
                if (url.contains("xtremepush://open_push")) {
                    PushConnector.postInEventBus(new BusEvent<>(pm));
                    PushConnector.postInEventBus(new CloseInAppEvent());
                    popupMessage.dismiss();
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                popupMessage.showAtLocation(decorView, gravity, 0, offset);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushConnector.postInEventBus(new CloseInAppEvent());
                popupMessage.dismiss();
            }
        });
        return new PopupDialog(popupMessage);
    }

    public void dismiss() {
        mMessage.dismiss();
    }

    private static int getScreenHeight(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } else {
            Display display = activity.getWindowManager().getDefaultDisplay();
            return display.getHeight();
        }
    }
    private static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        Integer actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    activity.getResources().getDisplayMetrics());
        }
        return result + actionBarHeight;
    }
}
