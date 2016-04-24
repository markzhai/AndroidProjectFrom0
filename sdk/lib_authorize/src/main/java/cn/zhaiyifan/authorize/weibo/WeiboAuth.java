package cn.zhaiyifan.authorize.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.zhaiyifan.authorize.Auth;
import cn.zhaiyifan.authorize.AuthCallback;
import cn.zhaiyifan.authorize.AuthField;
import cn.zhaiyifan.authorize.AuthResponse;
import cn.zhaiyifan.authorize.AuthType;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import com.morecruit.ext.component.logger.Logger;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public class WeiboAuth implements Auth {

    private static final String TAG = "UserSystem";
    private static final String SINA_APP_KEY = "3516504751";
    private static final String SINA_APP_SECRET = "4bfa87b0a38002b9ddfaa15d171812f3";
    private static final String SINA_REDIRECT_URL = "http://qa.69night.cn/account3rd/redirectOAuth/source/Weibo_Native";

    private static final String SINA_SCOPE = "all";

    private SsoHandler mSSOHandler;

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    @Override
    public void auth(Activity activity, final AuthCallback callback) {
        mSSOHandler = new SsoHandler(activity, new AuthInfo(activity, SINA_APP_KEY, SINA_REDIRECT_URL, SINA_SCOPE));
        mSSOHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
//                // 从 Bundle 中解析 Token
//                Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
//                //从这里获取用户输入的 电话号码信息
//                String  token =  mAccessToken.getToken();
//                if (mAccessToken.isSessionValid()) {
//                    // 显示 Token
//                    Log.e("qijian","token:"+token);
//                } else {
//                    // 以下几种情况，您会收到 Code：
//                    // 1. 当您未在平台上注册的应用程序的包名与签名时；
//                    // 2. 当您注册的应用程序包名与签名不正确时；
//                    // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                    String code = bundle.getString("code");
//                }
                AuthResponse response = new AuthResponse(
                        AuthType.WEIBO, bundle.getString(AuthField.UID_FIELD, "")
                        , bundle.getString(AuthField.ACCESS_TOKEN_FIELD, "")
                        , bundle.getString(AuthField.EXPIRES_IN_FIELD, ""));
                Logger.e(TAG, "token:" + bundle.getString(AuthField.ACCESS_TOKEN_FIELD, ""));
                response.setAuthType(AuthType.WEIBO);
                callback.onAuthSuccess(response);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                callback.onAuthFailed(e.getMessage());
            }

            @Override
            public void onCancel() {
                callback.onAuthCancel();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 微博授权,SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSSOHandler != null) {
            mSSOHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    @Override
    public void release() {
    }
}
