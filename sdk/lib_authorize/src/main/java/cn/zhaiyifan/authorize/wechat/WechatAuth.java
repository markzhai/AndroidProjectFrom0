package cn.zhaiyifan.authorize.wechat;

import android.app.Activity;
import android.content.Intent;

import cn.zhaiyifan.authorize.Auth;
import cn.zhaiyifan.authorize.AuthCallback;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.morecruit.ext.component.logger.Logger;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public class WechatAuth implements Auth {

    public static final String TENCENT_MMS_APP_ID = "wxa681a91e314c4c48";
    public static final String TENCENT_MMS_APP_SECRET = "ed67f01a01c0b5aaabc158107da47eb5";

    private static final String WX_USERINFO_SCOPE = "snsapi_userinfo";

    private static IWXAPI mMmsApi;

    @Override
    public void auth(Activity activity, final AuthCallback callback) {
        mMmsApi = WXAPIFactory.createWXAPI(activity.getApplicationContext(), TENCENT_MMS_APP_ID, true);
        mMmsApi.registerApp(TENCENT_MMS_APP_ID);

//        activity.runOnUiThread(() -> {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = WX_USERINFO_SCOPE;
            req.state = "crew_state_" + System.currentTimeMillis();
            mMmsApi.sendReq(req);
            Logger.d("UserSystem", "wechat sendReq");
//        });
    }

    @Override
    public void release() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
