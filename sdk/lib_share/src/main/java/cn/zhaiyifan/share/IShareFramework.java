package cn.zhaiyifan.share;

import android.content.Context;

import cn.zhaiyifan.share.data.ShareInfo;
import cn.zhaiyifan.share.plugin.IShareCallback;
import cn.zhaiyifan.share.plugin.ISharePlugin;
import cn.zhaiyifan.share.plugin.SharePluginInfo;

import java.util.List;

/**
 * 分享框架接口
 * 注意：该接口所有方法不支持并发
 *
 * @author markzhai
 * @version 1.0.0
 */
public interface IShareFramework {
    /**
     * 注册plugin
     *
     * @return 返回被替换的plugin，如没有替换plugin，返回null
     */
    ISharePlugin registerSharePlugin(ISharePlugin plugin);

    /**
     * 删除plugin
     *
     * @return 返回删除的plugin，如没有删除plugin，返回null
     */
    ISharePlugin unRegisterSharePlugin(String pluginKey);

    /**
     * 获取已注册的pluginInfo。
     * 使用List,方便需要过滤未安装应用插件的需求。
     */
    List<SharePluginInfo> getPluginInfos();

    /**
     * 更新pluginInfo列表,重新获取sharePluginInfo
     * 使用场景: 在使用component share时(如分享到微博)，需要根据当前是否已安装微博客户端来修改plugin的信息
     */
    void updatePluginInfos(Context context);

    /**
     * @param pluginKey 分享目标pluginKey
     * @param info      分享内容
     * @param context   context
     * @param callback  分享过程回调
     */
    void share(String pluginKey, ShareInfo info, Context context, IShareCallback callback);
}
