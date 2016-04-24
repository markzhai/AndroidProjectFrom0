package cn.zhaiyifan.share;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;
import cn.zhaiyifan.share.util.ShareLinkWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markzhai
 * @version 1.0.0
 */
public class ShareFrameworkService implements IShareFramework {

    private final Map<String, ISharePlugin> mPluginMap = new LinkedHashMap<String, ISharePlugin>();
    private ShareEntryQueryService mShareEntryQueryService = new ShareEntryQueryService();
    private Handler mHandler;

    @Override
    public ISharePlugin registerSharePlugin(ISharePlugin plugin) {
        synchronized (mPluginMap) {
            ISharePlugin result = addPlugin(plugin);
            if (result != null) return result;
        }
        return null;
    }

    private ISharePlugin addPlugin(ISharePlugin plugin) {
        if (plugin != null) {
            SharePluginInfo pluginInfo = plugin.getSharePluginInfo(mShareEntryQueryService);
            if (pluginInfo != null) {
                String key = pluginInfo.mPluginKey;
                if (!TextUtils.isEmpty(key)) {
                    return mPluginMap.put(key, plugin);
                }
            }
        }
        return null;
    }

    @Override
    public ISharePlugin unRegisterSharePlugin(String pluginKey) {
        synchronized (mPluginMap) {
            if (!TextUtils.isEmpty(pluginKey)) {
                return mPluginMap.remove(pluginKey);
            }
        }
        return null;
    }

    @Override
    public List<SharePluginInfo> getPluginInfos() {
        synchronized (mPluginMap) {
            List<SharePluginInfo> list = new ArrayList<SharePluginInfo>();
            Collection<ISharePlugin> entryCollection = mPluginMap.values();
            for (ISharePlugin sharePlugin : entryCollection) {
                list.add(sharePlugin.getSharePluginInfo(mShareEntryQueryService));
            }
            return list;
        }
    }

    @Override
    public void updatePluginInfos(Context context) {
        mShareEntryQueryService.updateShareEntryList(context);// 先更新shareEntry，再重新取pluginInfo
        synchronized (mPluginMap) {
            Collection<ISharePlugin> entries = mPluginMap.values();
            List<ISharePlugin> list = new ArrayList<ISharePlugin>();
            for (ISharePlugin plugin : entries) {
                list.add(plugin);
            }
            mPluginMap.clear();
            for (ISharePlugin plugin : list) {
                addPlugin(plugin); // addPlugin会重新拿一遍pluginInfo
            }
        }
    }

    @Override
    public void share(String pluginKey, final ShareInfo info, final Context context, final IShareCallback callback) {
        final ISharePlugin plugin = mPluginMap.get(pluginKey);
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        new Thread() {
            @Override
            public void run() {
                if (plugin != null) {
                    dealShareInfo(info);
                    if (plugin.needPrepare(info, context)) {
                        callback.onSharePrepare();
                        int res = plugin.prepare(info, context);
                        if (res != ISharePlugin.PREPARE_OK) {
                            callback.onShareFail(res);
                            return;
                        }
                    }
                    mHandler.post(() -> {
                        callback.onShareStart();
                        plugin.share(info, context, callback);
                        callback.onShareFinish();
                    });
                }
            }
        }.start();
    }

    private void dealShareInfo(final ShareInfo info) {
        info.mContent = ShareLinkWrapper.wrapShareTextWithLink(info.mContent);
    }
}
