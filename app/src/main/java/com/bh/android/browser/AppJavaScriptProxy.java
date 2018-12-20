package com.bh.android.browser;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class AppJavaScriptProxy {

    private Activity mActivity = null;
    private WebView mWebView  = null;
    public AppJavaScriptProxy(Activity activity,WebView webview) {
        this.mActivity = activity;
        this.mWebView  = webview;
    }

    @JavascriptInterface
    public void back() {


        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(mWebView.canGoBack()) {
                    mWebView.goBack();

                } else {
                    mActivity.onBackPressed();
                }
            }
        });

    }
}
