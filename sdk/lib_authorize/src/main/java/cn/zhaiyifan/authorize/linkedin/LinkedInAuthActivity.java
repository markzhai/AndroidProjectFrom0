package cn.zhaiyifan.authorize.linkedin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.zhaiyifan.authorize.AuthFactory;
import cn.zhaiyifan.authorize.AuthType;
import com.morecruit.authorize.R;
import com.morecruit.ext.component.logger.Logger;

/**
 * @author markzhai on 16/3/8
 * @version 1.0.0
 */
public class LinkedInAuthActivity extends Activity {

    private static final String TAG = "LinkedInAuthActivity";

    private static final String REQUEST_URI = "http://qa.m.hicrew.cn/account3rd/login/source/LinkedIn_Web";
    private static final String REDIRECT_URI = "http://qa.m.hicrew.cn/account3rd/";

    private static final String RESPONSE_TYPE_VALUE = "code";

    private WebView webView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_in_auth);

        webView = (WebView) findViewById(R.id.linked_in_web_view);
        webView.requestFocus(View.FOCUS_DOWN);
        pd = ProgressDialog.show(this, "", "Loading..", true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Uri uri = Uri.parse(authorizationUrl);

                    String code = uri.getQueryParameter(RESPONSE_TYPE_VALUE);

                    // null if the user doesn't allow authorization to our application
                    if (code == null) {
                        finish();
                        return true;
                    }

                    Intent intent = new Intent();
                    intent.putExtra(LinkedInAuth.KEY_INTENT_CODE, code);
                    AuthFactory.getAuth(AuthType.LINKED_IN).onActivityResult(0, 0, intent);

                    Logger.i(TAG, "Auth token received: " + code);
                    finish();
                } else {
                    Logger.i(TAG, "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        String authUrl = REQUEST_URI;
        webView.loadUrl(authUrl);
    }
}
