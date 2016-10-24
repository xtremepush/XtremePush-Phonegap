package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.util.UrlUtils;

public class DisplayPushActivity extends Activity {
    public static final String PUSH_MESSAGE_DESC = "ie.imobile.extremepush.ui.DisplayPushActivity";
    private static final String TAG = DisplayPushActivity.class.getSimpleName();
    public static final String HIT_URL_ERROR = "HitUrl failed: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.display_push_message, null);
        TextView messageDesc = (TextView) view.findViewById(R.id.display_message_desc);

        final PushMessage pushMessage = getIntent().getParcelableExtra(PUSH_MESSAGE_DESC);
        messageDesc.setText(pushMessage.alert);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Push received")
                .setCancelable(false)
                .setView(view);
        if (!TextUtils.isEmpty(pushMessage.url)) {
            dialogBuilder = dialogBuilder.setPositiveButton(R.string.push_dialog_view, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (TextUtils.equals(pushMessage.um, PushMessage.OUTSIDE)) {
                        UrlUtils.openUrlInBrowser(DisplayPushActivity.this, pushMessage.url);
                    } else if (TextUtils.equals(pushMessage.um, PushMessage.INSIDE)){
                        UrlUtils.openUrlInWebView(DisplayPushActivity.this, pushMessage.url);
                    } else if (TextUtils.equals(pushMessage.um, PushMessage.DEEPLINK)){
                        UrlUtils.openUrlAsDeepLink(DisplayPushActivity.this, pushMessage.url);
                    } else UrlUtils.openUrlInWebView(DisplayPushActivity.this, pushMessage.url);
                    finish();
                }
            });
        }
        AlertDialog dialog = dialogBuilder.setNegativeButton(R.string.push_dialog_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create();

        dialog.show();
    }
}
