package cn.zhaiyifan.share.plugin.qq;

import android.content.pm.ResolveInfo;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.plugin.SharePluginInfo;
import cn.zhaiyifan.share.plugin.common.BaseComponentSharePlugin;

/**
 * QQ空间分享 plugin
 * 如果本机未装QQ空间，则不会添加到plugin列表中
 * 如需要未安装也显示，请自定义plugin, 在getSharePluginInfo处返回非null，并且自己处理未安装情况
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class QzoneSharePlugin extends BaseComponentSharePlugin {
    public static final String PLUGIN_KEY = "qzone_plugin";
    public static final String NAME = "QQ空间";
    public static String QZONE_PACKAGENAME = "com.qzone";

    protected SharePluginInfo mPluginInfo;

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        ResolveInfo info = getShareResolveInfo(queryShareEntryService, QZONE_PACKAGENAME);
        if (info == null) {
            return null;
        }
        if (mPluginInfo == null) {
            mPackageName = info.activityInfo.packageName;
            mActivityName = info.activityInfo.name;
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_qzone;
        }
        return mPluginInfo;
    }

}
