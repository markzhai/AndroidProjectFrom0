package cn.zhaiyifan.share.plugin.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.morecruit.ext.component.logger.Logger;
import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * 微信聊天分享plugin
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class WeChatSharePlugin implements ISharePlugin {
    public static final String PLUGIN_KEY = "wechat_plugin";
    private static final String NAME = "微信";
    private static final int THUMB_SIZE = 80;
    private static IWXAPI mAPI;
    protected SharePluginInfo mPluginInfo;
    protected String mAppId;
    protected boolean mIsTimeLine = false;

    public WeChatSharePlugin() {
        mAppId = getAppId();
    }

    private static byte[] bmpToBytes(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, THUMB_SIZE, baos);
        return baos.toByteArray();
    }

    public abstract String getAppId();

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_wechat;
        }
        return mPluginInfo;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        if (info == null || TextUtils.isEmpty(mAppId)) {
            return false;
        }
        if (mAPI == null) {
            mAPI = WXAPIFactory.createWXAPI(context, mAppId, true);
            mAPI.registerApp(mAppId);
        }
        try {
            if (TextUtils.isEmpty(info.mImageUrl) && info.mImageBitmap == null) {
                if (TextUtils.isEmpty(info.mUrl)) {
                    //纯文字
                    shareText(info.mTitle, info.mContent, mIsTimeLine);
                } else {
                    //文字加链接
                    //由于微信朋友如果没有标题会有一块灰色的很难看，所以如果没标题就把分享内容作为超链接的标题好了
                    String weixinTitle = info.mTitle;
                    shareHypeLink(weixinTitle, info.mContent, null,
                            null, THUMB_SIZE, mIsTimeLine);
                }
            } else if (TextUtils.isEmpty(info.mUrl)
                    && info.mImageBitmap != null) {
                //纯图
                sharePic(info.mImageBitmap, null, mIsTimeLine);
            } else {
                //文字图片和链接
                shareHypeLink(info.mTitle, info.mContent, info.mImageUrl, info.mUrl, THUMB_SIZE, mIsTimeLine);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean needPrepare(ShareInfo info, Context context) {
        return false;
    }

    @Override
    public int prepare(ShareInfo info, Context context) {
        return ISharePlugin.PREPARE_OK;
    }

    /**
     * 无论是对话，还是朋友圈中，都只显示text字段，如果text中有链接，链接在对话和朋友圈中都可点击。
     *
     * @param title      这个字段传null就好，没有目前微信的SDK没有用到。
     * @param text       要分享的文本。
     * @param isTimeline 分享至微信聊天，还是朋友圈。true:分享至朋友圈;false:分享至微信聊天
     */
    private void shareText(String title, String text, boolean isTimeline) {

        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.title = title;
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareWechatUtils.buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;

        // 调用api接口发送数据到微信
        mAPI.sendReq(req);
    }

    /**
     * 分享链接。在对话中，显示title，pic，description三个元素。在朋友圈中，显示title、pic两个元素。
     *
     * @param title      标题
     * @param text       朋友圈中不显示。
     * @param picPath    缩略图的本地路径
     * @param url        链接的地址
     * @param isTimeline 分享至微信聊天，还是朋友圈。true:分享至朋友圈;false:分享至微信聊天
     */
    private void shareHypeLink(String title, String text, String picPath,
                               String url, int thumbNailSize, boolean isTimeline) {
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = text;
        if (picPath != null) {
            Bitmap thumb = ShareWechatUtils.extractThumbNail(picPath,
                    thumbNailSize, thumbNailSize, true);
            if (thumb != null) {
                Logger.e("Share:", "图片不为null");
                msg.setThumbImage(thumb);
            }
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareWechatUtils.buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        mAPI.sendReq(req);
    }

    private void sharePic(Bitmap bmp, Bitmap thumbBmp, boolean isTimeline) {


        WXImageObject imgObj = new WXImageObject();
        imgObj.imageData = bmpToBytes(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        // 缩略图
        if (thumbBmp != null) {
            msg.thumbData = bmpToBytes(thumbBmp);
        } else if (bmp != null) {
            Bitmap thumb = ShareWechatUtils.extractThumbNail(bmp, THUMB_SIZE, THUMB_SIZE, true);
            if (thumb != null) {
                Logger.e("Share:", "图片不为null");
            }
//			msg.setThumbImage(thumb);
            msg.thumbData = bmpToBytes(thumb);
        }

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareWechatUtils.buildTransaction("img");
        req.message = msg;

        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        mAPI.sendReq(req);
    }

}
