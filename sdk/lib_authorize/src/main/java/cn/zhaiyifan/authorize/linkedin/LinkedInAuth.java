package cn.zhaiyifan.authorize.linkedin;

import android.app.Activity;
import android.content.Intent;

import cn.zhaiyifan.authorize.Auth;
import cn.zhaiyifan.authorize.AuthCallback;
import cn.zhaiyifan.authorize.AuthResponse;
import cn.zhaiyifan.authorize.AuthType;

/**
 * LinkedIn第三方登录认证
 *
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public class LinkedInAuth implements Auth {

    private AuthCallback mCallback;
    public static final String KEY_INTENT_CODE = "code";

    @Override
    public void auth(Activity activity, AuthCallback callback) {
        mCallback = callback;
        activity.startActivity(new Intent(activity, LinkedInAuthActivity.class));
    }

    @Override
    public void release() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            AuthResponse response = new AuthResponse(AuthType.LINKED_IN);
            response.setCode(data.getStringExtra(KEY_INTENT_CODE));
            mCallback.onAuthSuccess(response);
        } else {
            mCallback.onAuthFailed("授权失败");
        }
    }
}
