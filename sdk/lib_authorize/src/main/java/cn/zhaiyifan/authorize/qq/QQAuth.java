package cn.zhaiyifan.authorize.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.Gson;
import cn.zhaiyifan.authorize.Auth;
import cn.zhaiyifan.authorize.AuthCallback;
import cn.zhaiyifan.authorize.AuthDialog;
import cn.zhaiyifan.authorize.AuthField;
import cn.zhaiyifan.authorize.AuthResponse;
import cn.zhaiyifan.authorize.AuthType;
import com.morecruit.ext.component.logger.Logger;
import com.morecruit.ext.utils.UrlUtils;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public class QQAuth implements Auth {
    private static final String TAG = "UserSystem";

    //APP ID 1105234156
    //APP KEY t9nCuAZRbKY0xD7S
    private static final String TENCENT_APP_KEY = "1105234156";
    private static final String QQ_SCOPE = "all";
    private static final String TENCENT_ACCESS_TOKEN_URL = "https://openmobile.qq.com/oauth2.0/authorize";
    private static final String TENCENT_REDIRECT_URL = "http://qa.69night.cn/account3rd/redirectOAuth/source/QQ_Native";
    private Tencent mTencent;

    private Activity mActivity;
    private AuthCallback mCallback;

    @Override
    public void auth(final Activity activity, final AuthCallback callback) {
        mActivity = activity;
        mCallback = callback;
        Handler handler = new Handler(activity.getMainLooper());
        handler.post(() -> {
            mTencent = Tencent.createInstance(TENCENT_APP_KEY, activity.getApplicationContext());
            mTencent.login(activity, QQ_SCOPE, loginListener);
        });

    }

    private IUiListener loginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Gson gson = new Gson();
            AuthResponse response = gson.fromJson(o.toString(), AuthResponse.class);
            if (response != null) {
                Logger.e(TAG, response.getAccessToken());
                response.setAuthType(AuthType.QQ);
                mCallback.onAuthSuccess(response);
            }
        }

        @Override
        public void onError(UiError uiError) {
            new AuthDialog(mActivity, AuthType.QQ, buildAuthUrl(), mCallback).show();
        }

        @Override
        public void onCancel() {
            mCallback.onAuthCancel();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
    }

    @Override
    public void release() {
        // 反射方法调用QQAuth.AuthAgent.release方法
        try {
            Field qqAuthField = mTencent.getClass().getDeclaredField("mQQAuth");
            qqAuthField.setAccessible(true);
            QQAuth qqAuth = (QQAuth) qqAuthField.get(mTencent);
            qqAuthField.setAccessible(false);

            Field authAgentField = qqAuth.getClass().getDeclaredField("a");
            authAgentField.setAccessible(true);
            AuthAgent authAgent = (AuthAgent) authAgentField.get(qqAuth);
            authAgentField.setAccessible(false);
            authAgent.releaseResource();
            mTencent = null;
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private String buildAuthUrl() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(AuthField.CLIENT_ID_FIELD, TENCENT_APP_KEY);
        parameters.put(AuthField.RESPONSE_TYPE_FIELD, "token");
        parameters.put(AuthField.REDIRECT_URI_FIELD, TENCENT_REDIRECT_URL);
        parameters.put(AuthField.SCOPE_FIELD, QQ_SCOPE);
        parameters.put(AuthField.DISPLAY_FIELD, "mobile");
        return UrlUtils.buildUrl(TENCENT_ACCESS_TOKEN_URL, parameters);
    }
}
