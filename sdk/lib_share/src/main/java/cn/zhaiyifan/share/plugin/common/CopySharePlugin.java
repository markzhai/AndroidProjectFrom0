package cn.zhaiyifan.share.plugin.common;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;

/**
 * @author markzhai
 * @version 1.0.0
 */
public class CopySharePlugin implements ISharePlugin {

    public static final String PLUGIN_KEY = "copy_plugin";
    public static final String NAME = "复制";
    public static final String COPY_SUCCESS = "复制成功";

    protected SharePluginInfo mPluginInfo;

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_copy;
        }
        return mPluginInfo;
    }

    @Override
    public boolean share(ShareInfo info, Context context, final IShareCallback callback) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                copyInNewApi(context, info);
            } else {
                android.text.ClipboardManager cm = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(info.mContent + " " + info.mUrl);
            }
            Toast.makeText(context, COPY_SUCCESS, Toast.LENGTH_SHORT).show();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void copyInNewApi(Context context, ShareInfo info) {
        android.content.ClipboardManager cm = (android.content.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText(info.mTitle,
                info.mContent + " " + info.mUrl));
    }
}
