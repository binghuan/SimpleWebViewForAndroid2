package com.bh.android.browser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class AppJavaScriptProxy {

    private TelephonyManager mTelephonyMgr = null;
    private Activity mActivity = null;
    private WebView mWebView = null;
    private Context mContext = null;

    public AppJavaScriptProxy(Activity activity, WebView webview) {
        this.mActivity = activity;
        this.mWebView = webview;
        mContext = activity.getApplicationContext();
        mTelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @JavascriptInterface
    public String getDeviceId() {
        return Build.SERIAL;
    }

    @JavascriptInterface
    public void back() {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mWebView.canGoBack()) {
                    mWebView.goBack();

                } else {
                    mActivity.onBackPressed();
                }
            }
        });
    }

    @JavascriptInterface
    public String getAccessKey() {
        // key from btoa("key_for_longfei")
        return "a2V5X2Zvcl9sb25nZmVp";
    }

    @JavascriptInterface
    public void hideActionBar() {
        if (mActivity instanceof WebViewHostActivity) {
            ((WebViewHostActivity) mActivity).hideActionBar();
        }
    }
}
