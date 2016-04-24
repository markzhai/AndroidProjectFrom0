package cn.zhaiyifan.share.plugin.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import cn.zhaiyifan.share.IQueryShareEntryService;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;

import java.util.List;

/**
 * component分享基类。
 * 所谓component分享就是通过startActivity的方式启动第三方应用进行分享
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class BaseComponentSharePlugin implements ISharePlugin {
    protected Context mContext;
    protected String mPackageName, mActivityName;

    public BaseComponentSharePlugin() {
        mContext = getContext();
    }

    public abstract Context getContext();

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        return null;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        try {
            return doShare(context, mPackageName, mActivityName, info);
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
     * 返回包名为packageName的本地支持分享的ResolveInfo
     */
    protected ResolveInfo getShareResolveInfo(IQueryShareEntryService queryShareEntryService, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        List<ResolveInfo> list = queryShareEntryService.getShareEntryList(mContext);
        for (ResolveInfo info : list) {
            if (packageName.equalsIgnoreCase(info.activityInfo.packageName)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 当shareInfo和处理逻辑不能满足需求时，可自定义plugin
     * 或者extend ShareInfo并override本方法
     */
    protected boolean doShare(Context context, String packageName, String activityName, ShareInfo shareInfo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setComponent(new ComponentName(packageName, activityName));
        intent.putExtra(Intent.EXTRA_SUBJECT, shareInfo.mTitle);
        // 分享内容+空格+分享链接
        intent.putExtra(Intent.EXTRA_TEXT, shareInfo.mContent + " " + shareInfo.mUrl);

        if (TextUtils.isEmpty(shareInfo.mImageUrl)) {
            //新浪微博会报一个“不支持的文件格式”问题，无奈只能特殊处理一下
            /**
             if (mSpt == SharePlatform.SinaWeibo) {
             intent.setType("image/*");
             } else {
             intent.setType("text/plain");
             }
             ***/
            //新浪自相矛盾了，纯文本分享，如果设"text/plain"，老版本会报“不支持的文件格式”问题
            //如果设"image/*"，新版本会报空指针，直接crash
            intent.setType("text/plain");
        } else {
            // /storage/sdcard0/mr/qrcode_pic_tmp.jpg
            // /storage/sdcard0/mr/share_pic_tmp.jpg
            //单张图片
            intent.setType("image/*");
            Uri uri = Uri.parse(shareInfo.mImageUrl);
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            //intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, getpics());
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
