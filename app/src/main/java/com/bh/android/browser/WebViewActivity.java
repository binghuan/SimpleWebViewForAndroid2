package com.bh.android.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {

    private final boolean DBG = true;
    private final String TAG = "BH_Browser_" + this.getClass().getSimpleName();

    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private TextView mTextViewForTitle = null;
    private TextView mTextViewForURL = null;

    private void showLoadingProgress(boolean isVisible) {
        if (isVisible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mWebView = (WebView) findViewById(R.id.core_web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);

        CookieManager cookieMgr = CookieManager.getInstance();
        cookieMgr.setAcceptCookie(true);
        //cookieMgr.setAcceptThirdPartyCookies(mWebView, true);

        //20161228@BH_Lin: Method to debug the WebView.
        // Reference: https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        mTextViewForTitle = (TextView) findViewById(R.id.text_view_title);
        mTextViewForURL = (TextView) findViewById(R.id.text_view_url);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                showLoadingProgress(false);
                getSupportActionBar().setTitle(view.getTitle());

                mTextViewForTitle.setText(mWebView.getTitle());
                mTextViewForURL.setText(mWebView.getUrl());

            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.addJavascriptInterface(new AppJavaScriptProxy(this,mWebView), "androidAppProxy");

        //mWebView.loadUrl("http://192.168.0.121:8080/index.html");

        boolean isTriggeredByIntent = handleIntent();
        if (isTriggeredByIntent) {
            getSupportActionBar().hide();
        }

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean handleIntent() {

        boolean isUrlIntentReceived = false;

        Uri dataUri = getIntent().getData();
        if (getIntent().getAction().equals(Intent.ACTION_VIEW) &&
                dataUri != null) {
            if (DBG) Log.v(TAG, "data uri=" + dataUri);
            isUrlIntentReceived = true;

            loadUrl(dataUri.toString());
        }

        return isUrlIntentReceived;

    }

    private void loadUrl(String url) {
        if (DBG) Log.v(TAG, "loadUrl: __" + url + "__");
        mTextViewForURL.setText(url);
        mWebView.loadUrl(url);
        showLoadingProgress(true);
    }
}
