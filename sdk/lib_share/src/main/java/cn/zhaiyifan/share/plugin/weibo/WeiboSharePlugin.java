package cn.zhaiyifan.share.plugin.weibo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.utils.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 微信聊天分享plugin
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class WeiboSharePlugin implements ISharePlugin {
    public static final String PLUGIN_KEY = "weibo_plugin";
    private static final String NAME = "微博";

    protected SharePluginInfo mPluginInfo;

    protected String mAppKey;
    protected String mRedirectUrl;
    protected String mScope;

    private IWeiboShareAPI mWeiboShareAPI;

    public WeiboSharePlugin() {
        mAppKey = getAppKey();
        mRedirectUrl = getRedirectUrl();
        mScope = getScope();
    }

    public abstract String getAppKey();

    public abstract String getRedirectUrl();

    public abstract String getScope();

    public abstract Context getContext();

    public abstract Activity getActivity();

    public void init() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(getContext(), mAppKey);
        mWeiboShareAPI.registerApp();
    }


    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_weibo;
        }
        return mPluginInfo;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        if (info == null || TextUtils.isEmpty(mAppKey)) {
            return false;
        }

        sendMultiMessage(info,
                !TextUtils.isEmpty(info.mContent),
                info.mImageBitmap != null || !TextUtils.isEmpty(info.mImageUrl),
                !TextUtils.isEmpty(info.mUrl),
                false, false, false);

        return false;
    }

    @Override
    public boolean needPrepare(ShareInfo info, Context context) {
        return true;
    }

    @Override
    public int prepare(ShareInfo info, Context context) {

        // 优先分享bitmap
        if (info.mImageBitmap != null) {
            return ISharePlugin.PREPARE_OK;
        }

        if (!TextUtils.isEmpty(info.mImageUrl) && info.mImageUrl.toLowerCase().startsWith("http")) {

            String saveDir = getContext().getCacheDir().toString(); // Environment.getExternalStorageDirectory()
            String fileName = info.mImageUrl.substring(info.mImageUrl.lastIndexOf('/') + 1);

            String savePath = saveDir + "/" + fileName;
            File f = new File(savePath);
            if (f.exists()) {
                f.delete();
            }

            String res = "";
            try {
                res = HttpManager.downloadFile(getContext(), info.mImageUrl, saveDir, fileName);
                // 重试一次
                if (!res.equalsIgnoreCase(savePath)) {
                    if (f.exists()) {
                        f.delete();
                    }
                    res = HttpManager.downloadFile(getContext(), info.mImageUrl,
                            saveDir, fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!res.equalsIgnoreCase(savePath)) {
                return ISharePlugin.PREPARE_ERROR;
            }


            Bitmap bitmap = null;
            try {
                FileInputStream fis = new FileInputStream(savePath);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (bitmap != null)
                info.mImageBitmap = bitmap;
            else
                return ISharePlugin.PREPARE_ERROR;
        }
        return ISharePlugin.PREPARE_OK;
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage(ShareInfo info, boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj(info);
        }

        if (hasImage) {
            if (info.mImageBitmap != null)
                weiboMessage.imageObject = getImageObj(info.mImageBitmap);
            else if (!TextUtils.isEmpty(info.mImageUrl))
                weiboMessage.imageObject = getImageObj(info.mImageUrl);
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj(info);
        }


        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;


        // 3. 发送请求消息到微博，唤起微博分享界面
        {
            AuthInfo authInfo = new AuthInfo(getContext(), mAppKey, mRedirectUrl, mScope);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(getActivity(), request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0) {
                    Toast.makeText(getContext(), "微博授权错误，请稍后再试...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(Bundle bundle) {
                    // TODO Auto-generated method stub
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(getContext(), newToken);
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(ShareInfo info) {
        TextObject textObject = new TextObject();

        textObject.text = info.mContent;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bmp) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bmp);
        return imageObject;
    }

    // TODO：此种方式，对于微博，path只能是本地图片,传url不行
    private ImageObject getImageObj(final String imgPath) {

        ImageObject imageObject = new ImageObject();
        imageObject.imagePath = imgPath;
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(ShareInfo info) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();

        mediaObject.title = info.mTitle != null ? info.mTitle : "";
        mediaObject.description = info.mContent != null ? info.mContent : "";

        Bitmap thumbBitmap = null;
        if (info.mThumbResId > 0)
            thumbBitmap = BitmapFactory.decodeResource(getContext().getResources(), info.mThumbResId);
        else
            thumbBitmap = info.mThumbBmp;
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        if (thumbBitmap != null)
            mediaObject.setThumbImage(thumbBitmap);

        mediaObject.actionUrl = info.mUrl;
        String dt = !TextUtils.isEmpty(mediaObject.description) ? mediaObject.description : mediaObject.title;
        mediaObject.defaultText = dt;
        return mediaObject;
    }

}
