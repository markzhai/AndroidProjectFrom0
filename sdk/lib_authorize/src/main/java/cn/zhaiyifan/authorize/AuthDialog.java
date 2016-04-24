package cn.zhaiyifan.authorize;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.morecruit.authorize.R;
import com.morecruit.ext.utils.StringUtils;
import com.morecruit.ext.utils.UrlUtils;

import java.lang.ref.WeakReference;
import java.util.Map;

import com.morecruit.ext.component.logger.Logger;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public class AuthDialog extends Dialog {
    private static final String TAG = "AuthDialog";

    private WebView mWebView;
    private ProgressDialog mProgressDialog;

    private AuthType mAuthType;
    private String mAuthUrl;
    private AuthCallback mAuthCallback;
    private WeakReference<Activity> mActivityWeakReference;

    /**
     * @param activity activity
     * @param authType see {@link AuthType}
     * @param authUrl  String
     * @param callback AuthCallback
     */
    public AuthDialog(Activity activity, AuthType authType, String authUrl, AuthCallback callback) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        if (StringUtils.isEmpty(authUrl)) {
            throw new IllegalArgumentException("authUrl can not be null.");
        }
        mActivityWeakReference = new WeakReference<>(activity);
        mAuthType = authType;
        mAuthUrl = authUrl;
        mAuthCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = createProgressDialog();
        mProgressDialog.show();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mWebView = new WebView(getContext());
        setUpWebView();
    }

    private void setUpWebView() {
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new AuthWebViewClient());
        mWebView.setWebChromeClient(new AuthWebChromeClient());
        mWebView.loadUrl(mAuthUrl);
        Logger.d(TAG, "loadUrl: " + mAuthUrl);
        mWebView.setVisibility(View.INVISIBLE);
        addContentView(mWebView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mProgressDialog.isShowing()) {
            dismissProgressDialog();
        } else if (mAuthCallback != null) {
            mAuthCallback.onAuthCancel();
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setTitle(getContext().getString(R.string.auth_waiting_dialog_title));
        progressDialog.setMessage(getContext().getString(R.string.auth_waiting_dialog_message));
        progressDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                onBackPressed();
            }
            return true;
        });
        return progressDialog;
    }

    private class AuthWebViewClient extends WebViewClient {
        private static final int MILLISECOND = 1000;

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.d("UserSystem", "onPageStarted: " + url);
            Map<String, Object> parameters = UrlUtils.getParams(url);
            String openId = (String) parameters.get(AuthField.OPEN_ID_FIELD);
            String accessToken = (String) parameters.get(AuthField.ACCESS_TOKEN_FIELD);
            String expiresIn = (String) parameters.get(AuthField.EXPIRES_IN_FIELD);
            if (isValidAccessToken(accessToken, expiresIn)) {
                if (mAuthCallback != null) {
                    mAuthCallback.onAuthSuccess(new AuthResponse(mAuthType, openId, accessToken, expiresIn));
                }
                view.stopLoading();
                dismissDialog();
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        private boolean isValidAccessToken(String token, String expiresIn) {
            if (!StringUtils.isEmpty(token) && !StringUtils.isEmpty(expiresIn)) {
                try {
                    long expiresTime = System.currentTimeMillis() + Long.parseLong(expiresIn) * MILLISECOND;
                    return (System.currentTimeMillis() < expiresTime);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    private void dismissDialog() {
        Activity activity = mActivityWeakReference.get();
        if (activity != null && !activity.isFinishing() && isShowing()) {
            dismiss();
        }
    }

    private void dismissProgressDialog() {
        Activity activity = mActivityWeakReference.get();
        if (activity != null && !activity.isFinishing() && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class AuthWebChromeClient extends WebChromeClient {
        public static final int MAX_PROGRESS = 100;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == MAX_PROGRESS) {
                dismissProgressDialog();
                mWebView.setVisibility(View.VISIBLE);
            }
        }
    }
}
