package cn.zhaiyifan.authorize;

import cn.zhaiyifan.authorize.linkedin.LinkedInAuth;
import cn.zhaiyifan.authorize.qq.QQAuth;
import cn.zhaiyifan.authorize.wechat.WechatAuth;
import cn.zhaiyifan.authorize.weibo.WeiboAuth;

public class AuthFactory {

    private static WeiboAuth mWeiboAuth;
    private static QQAuth mQQAuth;
    private static WechatAuth mWechatAuth;
    private static LinkedInAuth mLinkedInAuth;

    static public Auth getAuth(AuthType authType) {
        switch (authType) {
            case WEIBO:
                if (null == mWeiboAuth) {
                    mWeiboAuth = new WeiboAuth();
                }
                return mWeiboAuth;
            case QQ:
                if (null == mQQAuth) {
                    mQQAuth = new QQAuth();
                }
                return mQQAuth;
            case WECHAT:
                if (null == mWechatAuth) {
                    mWechatAuth = new WechatAuth();
                }
                return mWechatAuth;
            case LINKED_IN:
                if (null == mLinkedInAuth) {
                    mLinkedInAuth = new LinkedInAuth();
                }
                return mLinkedInAuth;
            default:
                return new Auth.NullAuth();
        }
    }
}
