package cn.zhaiyifan.share.plugin;

/**
 * 分享过程的回调
 *
 * @author markzhai
 * @version 1.0.0
 */
public interface IShareCallback {
    /**
     * 分享失败 入参错误码
     */
    int SHARE_FAIL_PREPARE_ERROR = ISharePlugin.PREPARE_ERROR;
    int SHARE_FAIL_PREPARE_UNKOWN = 0;

    /**
     * 准备分享(准备过程包含如:shorten url等分享前操作)
     */
    void onSharePrepare();

    /**
     * 开始分享, 启动界面等
     */
    void onShareStart();

    /**
     * 分享调用已执行(如:已唤起第三方app等)
     */
    void onShareFinish();

    void onShareFail(int errCode);
}
