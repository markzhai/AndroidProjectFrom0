package cn.zhaiyifan.authorize;

import android.content.Context;
import android.content.SharedPreferences;

import com.morecruit.ext.Ext;

public class AuthPersister {

    private static final String TENCENT_SHARED_PREFERENCE = "TENCENT_CREW_TOKEN";
    private static final String SINA_SHARED_PREFERENCE = "SINA_CREW_TOKEN";
    private static final String LINKED_IN_SHARED_PREFERENCE = "LINKED_IN_CREW_TOKEN";

    public AuthResponse load(AuthType authType) {
        String preferencesName = sharedPreferencesName(authType);

        if (preferencesName != null) {
            SharedPreferences preferences = Ext.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
            return new AuthResponse(authType
                    , preferences.getString(AuthField.OPEN_ID_FIELD, "")
                    , preferences.getString(AuthField.ACCESS_TOKEN_FIELD, "")
                    , preferences.getString(AuthField.EXPIRES_IN_FIELD, ""));
        }
        return null;
    }

    public void save(AuthResponse response) {
        String preferencesName = sharedPreferencesName(response.getAuthType());
        if (preferencesName != null) {
            SharedPreferences preferences = Ext.getContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AuthField.ACCESS_TOKEN_FIELD, response.getAccessToken());
            editor.putString(AuthField.EXPIRES_IN_FIELD, response.getExpiresIn());
            editor.putString(AuthField.OPEN_ID_FIELD, response.getOpenId());
            editor.commit();
        }
    }

    private static String sharedPreferencesName(AuthType authType) {
        switch (authType) {
            case QQ:
                return TENCENT_SHARED_PREFERENCE;
            case WEIBO:
                return SINA_SHARED_PREFERENCE;
            case LINKED_IN:
                return LINKED_IN_SHARED_PREFERENCE;
            default:
                return null;
        }
    }
}
