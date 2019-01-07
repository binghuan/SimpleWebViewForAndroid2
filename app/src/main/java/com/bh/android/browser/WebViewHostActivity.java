package com.bh.android.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WebViewHostActivity extends AppCompatActivity
        implements WebViewFragment.OnFragmentInteractionListener {

    private View mContentView;
    private boolean mIsActionBarEnabled;

    private WebViewFragment mWebViewFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view_host);

        mIsActionBarEnabled = true;
        mContentView = findViewById(R.id.webView_fragment_container);
        mWebViewFragment = new WebViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.ARG_URL, getIntent().getData().toString());
        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            mIsActionBarEnabled = intentBundle.getBoolean("SHOW_ACTION_BAR");
        }

        mWebViewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.webView_fragment_container, mWebViewFragment).commit();

        // Set up the user interaction to manually show or hide the system UI.
        if (mIsActionBarEnabled) {
            show();
        } else {
            hide();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    private final boolean DBG = true;
    private final String TAG = "BH_WebViewHost" + this.getClass().getSimpleName();

    private void hide() {
        if (DBG) Log.v(TAG, ">> Hide");
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        hideNavigationBar();
    }

    @SuppressLint("InlinedApi")
    private void show() {
        if (DBG) Log.v(TAG, ">> show");

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);

        hideNavigationBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideNavigationBar() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                //| View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    @Override
    public void onPageFinished(String title, String url) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(url);
    }
}
