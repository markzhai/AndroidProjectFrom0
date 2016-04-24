package cn.zhaiyifan.share.plugin;

import android.content.Context;

import cn.zhaiyifan.share.IQueryShareEntryService;
import cn.zhaiyifan.share.data.ShareInfo;

/**
 * 分享插件
 *
 * @author markzhai
 * @version 1.0.0
 */
public interface ISharePlugin {
    int PREPARE_OK = 1;
    int PREPARE_ERROR = -1;

    /**
     * 插件信息
     *
     * @param queryShareEntryService 获取本地支持分享操作的ResolveInfo信息的服务
     * @return 返回null代表本plugin无效，不加入sharePluginInfo里
     */
    SharePluginInfo getSharePluginInfo(IQueryShareEntryService queryShareEntryService);

    /**
     * 分享
     *
     * @param info     内容
     * @param context  context
     * @param callback 分享过程回调
     * @return 返回是否share成功
     */
    boolean share(ShareInfo info, Context context, IShareCallback callback);

    /**
     * 是否需要长时间准备数据，由此判断是都需要客户端提示等待
     *
     * @param info    内容
     * @param context context
     * @return 返回是否需要长时间准备数据
     */
    boolean needPrepare(ShareInfo info, Context context);

    /**
     * 准备数据
     *
     * @param info    内容
     * @param context context
     * @return 返回是否准备成功或错误码
     */
    int prepare(ShareInfo info, Context context);
}
