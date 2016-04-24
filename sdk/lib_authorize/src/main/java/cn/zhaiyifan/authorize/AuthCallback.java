package cn.zhaiyifan.authorize;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public interface AuthCallback {
    /**
     * onAuthSuccess
     *
     * @param response 认证响应
     */
    void onAuthSuccess(AuthResponse response);

    /**
     * @param reason 错误消息
     */
    void onAuthFailed(String reason);

    void onAuthCancel();
}
