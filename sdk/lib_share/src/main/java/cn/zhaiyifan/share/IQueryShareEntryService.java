package cn.zhaiyifan.share;

import android.content.Context;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * 获取本地支持分享操作的ResolveInfo信息
 *
 * @author markzhai
 * @version 1.0.0
 */
public interface IQueryShareEntryService {

    /**
     * 获取本地支持分享操作的ResolveInfo信息
     *
     * @param context 不能为空
     */
    List<ResolveInfo> getShareEntryList(Context context);
}
