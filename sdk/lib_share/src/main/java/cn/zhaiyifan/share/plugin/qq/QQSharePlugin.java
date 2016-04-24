package cn.zhaiyifan.share.plugin.qq;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;
import com.tencent.open.SocialConstants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * QQ分享plugin
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class QQSharePlugin implements ISharePlugin {

    public static final String PLUGIN_KEY = "qq_plugin";
    private static final String NAME = "QQ";
    private static Tencent mAPI;
    private SharePluginInfo mPluginInfo;
    private String mAppId;

    public QQSharePlugin() {
        mAppId = getAppId();
    }

    public abstract String getAppId();

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_qq;
        }
        return mPluginInfo;
    }

    @Override
    public boolean needPrepare(ShareInfo info, Context context) {
        return false;
    }

    @Override
    public int prepare(ShareInfo info, Context context) {
        return ISharePlugin.PREPARE_OK;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        if (info == null || TextUtils.isEmpty(mAppId)) {
            return false;
        }
        if (mAPI == null) {
            mAPI = Tencent.createInstance(mAppId, context.getApplicationContext());
        }
        try {
            Bundle params = new Bundle();
            //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_SUMMARY不能全为空，最少必须有一个是有值的。
            params.putString(SocialConstants.PARAM_TITLE, info.mTitle);
            if (!TextUtils.isEmpty(info.mVideoUrl)) {
                params.putString(SocialConstants.PARAM_PLAY_URL, info.mVideoUrl);
            } else if (!TextUtils.isEmpty(info.mImageUrl)) { // 分享图片的URL或者本地路径
                params.putString(SocialConstants.PARAM_IMAGE_URL, info.mImageUrl);
            }
            // 待测: qq分享接口不支持bitmap,可以存为本地图片文件后，通过本地路径传上去
            // 见 http://wiki.open.qq.com/index.php?title=Android_API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E&=45038#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89

            if (!TextUtils.isEmpty(info.mUrl)) {
                //targetUrl这条分享消息被好友点击后的跳转URL
                params.putString(SocialConstants.PARAM_TARGET_URL, info.mUrl);
            }
            //可选	String	分享的消息摘要，最长50个字。
            params.putString(SocialConstants.PARAM_SUMMARY, info.mContent);
            mAPI.shareToQQ((Activity) context, params, new IUiListener() {

                @Override
                public void onComplete(Object o) {
                    callback.onShareFinish();
                }

                @Override
                public void onError(UiError uiError) {
                    // TODO 整个框架做一套的errorListener
                }

                @Override
                public void onCancel() {
                }
            });
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
