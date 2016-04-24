package cn.zhaiyifan.share.plugin.wechat;

import cn.zhaiyifan.share.IQueryShareEntryService;
import com.morecruit.share.R;
import cn.zhaiyifan.share.plugin.SharePluginInfo;

/**
 * 微信朋友圈分享plugin
 *
 * @author markzhai
 * @version 1.0.0
 */
public abstract class WeChatMomentsSharePlugin extends WeChatSharePlugin {
    public static final String PLUGIN_KEY = "wechat_timeline_plugin";
    private static final String NAME = "朋友圈";

    public WeChatMomentsSharePlugin() {
        super();
        mIsTimeLine = true;
    }

    @Override
    public SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService) {
        if (mPluginInfo == null) {
            mPluginInfo = new SharePluginInfo();
            mPluginInfo.mPluginKey = PLUGIN_KEY;
            mPluginInfo.mName = NAME;
            mPluginInfo.mIconResource = R.drawable.share_icon_wechat_timeline;
        }
        return mPluginInfo;
    }
}
