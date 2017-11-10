package ie.imobile.extremepush.ui;

import ie.imobile.extremepush.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class WebViewActivity extends Activity {

    public static final String EXTRAS_URL = "extras_url";

    private WebView webView;
    private ImageButton closeButton;
    private ImageButton shareButton;
    private ImageButton openInBrowser;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 24 && this.isInMultiWindowMode())
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        else
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_webview);

        parseIntent();
        initViews();
        setupViews();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        url = intent.getExtras().getString(EXTRAS_URL);
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.webview_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        closeButton = (ImageButton) findViewById(R.id.webview_close);
        shareButton = (ImageButton) findViewById(R.id.webview_share);
        openInBrowser = (ImageButton) findViewById(R.id.webview_view_in_browser);
    }

    private void setupViews() {
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        closeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, "Share URL"));
            }
        });

        openInBrowser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                finish();
            }
        });
    }
}
