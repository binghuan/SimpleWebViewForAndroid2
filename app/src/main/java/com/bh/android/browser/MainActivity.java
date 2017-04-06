package com.bh.android.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView = null;
    private EditText mUrlInputField = null;
    private ProgressBar mProgressBar = null;
    private ImageButton mButtonClearInput = null;

    private final String TAG = "BH_Browser";

    private final boolean DBG = true;

    private void showLoadingProgress(boolean isVisible) {
        if (isVisible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void loadUrl(View v) {


        String targetUrl = mUrlInputField.getText().toString();
        mWebView.loadUrl(targetUrl);
        if (DBG) Log.v(TAG, "loadUrl: " + targetUrl);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        showLoadingProgress(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (DBG) Log.v(TAG, ">> onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonClearInput = (ImageButton) findViewById(R.id.clearinput);
        mButtonClearInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrlInputField.setText("");
            }
        });

        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mUrlInputField = (EditText) findViewById(R.id.url_input);
        mUrlInputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (DBG) Log.v(TAG, "onEditorAction: actionID = " + actionId);

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_NEXT) {
                    loadUrl(v);
                    return true;
                } else if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    loadUrl(v);
                    return true;
                }

                return false;
            }
        });


        CookieManager cookieMgr = CookieManager.getInstance();
        cookieMgr.setAcceptCookie(true);
        //cookieMgr.setAcceptThirdPartyCookies(mWebView, true);

        //20161228@BH_Lin: Method to debug the WebView.
        // Reference: https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                showLoadingProgress(false);
                getSupportActionBar().setTitle(view.getTitle());
            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //mWebView.loadUrl("http://192.168.0.121:8080/index.html");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DBG) Log.v(TAG, ">> onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DBG) Log.v(TAG, ">> onPause");
    }
}
