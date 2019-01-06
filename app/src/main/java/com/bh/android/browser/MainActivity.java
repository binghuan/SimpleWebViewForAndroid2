package com.bh.android.browser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
        implements InputTextFragment.OnFragmentInteractionListener {

    private WebView mWebView = null;
    private EditText mUrlInputField = null;
    private ProgressBar mProgressBar = null;
    private ImageButton mButtonClearInput = null;
    private Context mContext = null;
    private FragmentManager mFragmentMgr = null;
    private InputTextFragment mInputTextFragment = null;

    private final String TAG = "BH_Browser";

    private final boolean DBG = true;

    private void showLoadingProgress(boolean isVisible) {
        if (isVisible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadUrl(String url) {
        if (DBG) Log.v(TAG, "loadUrl: __" + url + "__");
        if (url != null) {
            mUrlInputField.setText(url);
        }
        doLoadUrl();
    }

    private void doLoadUrl() {

        String targetUrl = mUrlInputField.getText().toString();
        mWebView.loadUrl(targetUrl);
        if (DBG) Log.v(TAG, "doLoadUrl: " + targetUrl);
        //hideIME(v);
        hideKeyboard(this);
        showLoadingProgress(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_inject:

                if (mInputTextFragment == null) {
                    mInputTextFragment = new InputTextFragment();
                    mFragmentMgr.beginTransaction().add(R.id.fragment_container, mInputTextFragment).commit();
                }
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                break;

            case R.id.action_open_file:
                performFileSearch();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                loadUrl(uri.toString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (DBG) Log.v(TAG, ">> onCreate");
        mContext = this;
        mFragmentMgr = getSupportFragmentManager();

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
                    loadUrl(null);
                    return true;
                } else if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    loadUrl(null);
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
                mUrlInputField.setText(view.getUrl());

            }
        });
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.addJavascriptInterface(new AppJavaScriptProxy(this, mWebView), "androidAppProxy");
        //mWebView.loadUrl("http://192.168.0.121:8080/index.html");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DBG) Log.v(TAG, ">> onStop");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DBG) Log.v(TAG, ">> onPause");
    }

    @Override
    public void onJavaScriptInjection(String injectionString) {
        Log.v(TAG, "onJavaScriptInjection: " + injectionString);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        String content = injectionString.replaceAll("\n", "").replaceAll("\r", "");
        mWebView.loadUrl("javascript:" + content);
        mUrlInputField.setText(mWebView.getUrl());
    }

    @Override
    public void onCancelPressed() {
        Log.v(TAG, "onCancelPressed");
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    private static final int READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

}
