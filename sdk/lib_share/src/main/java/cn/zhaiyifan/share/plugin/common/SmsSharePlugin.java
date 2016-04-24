package cn.zhaiyifan.share.plugin.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;

/**
 * 短信分享plugin
 *
 * @author markzhai
 * @version 1.0.0
 */
public class SmsSharePlugin implements ISharePlugin {

    public static final String PLUGIN_KEY = "sms_plugin";
    public static final String NAME = "短信";

    protected SharePluginInfo mPluginInfo;

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_sms;
        }
        return mPluginInfo;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        try {
            String shareContent = info.mContent + " " + info.mUrl;
            if (Build.VERSION.SDK_INT > 18) { //At least KitKat
                return shareSMSKitKatOrAfter(context, shareContent);
            } else {
                return shareSMSbeforeKitKat(context, shareContent);
            }
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

    private boolean shareSMSbeforeKitKat(Context context, String shareContent) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", shareContent);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private boolean shareSMSKitKatOrAfter(Context context, String shareContent) {
        try {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

            if (defaultSmsPackageName != null) {
                // Can be null in case that there is no default, then the user would be able
                // to choose any app that support this intent.
                sendIntent.setPackage(defaultSmsPackageName);
            }
            context.startActivity(sendIntent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
