package cn.zhaiyifan.authorize;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("openid")
    private String mOpenId = "";
    @SerializedName("access_token")
    private String mAccessToken = "";
    @SerializedName("expires_in")
    private String mExpiresIn = "";
    @SerializedName("refresh_token")
    private String mRefreshToken = "";
    @SerializedName("scope")
    private String mScope = "";

    @SerializedName("code")
    private String mCode = "";

    /**
     * 处理是否成功
     */
    @SerializedName("success")
    public boolean success;

    /**
     * 登录成功时
     * 调用业务服务器创建session后的业务数据
     */
    public String data;

    private AuthType mAuthType;

    public AuthResponse(AuthType authType) {
        mAuthType = authType;
    }

    public AuthResponse(AuthType authType, String openId, String accessToken, String expiresIn) {
        mAuthType = authType;
        mOpenId = openId;
        mAccessToken = accessToken;
        mExpiresIn = expiresIn;
    }

    public void setOpenId(String mOpenId) {
        this.mOpenId = mOpenId;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public void setExpiresIn(String mExpiresIn) {
        this.mExpiresIn = mExpiresIn;
    }

    public void setRefreshToken(String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }

    public void setScope(String scope) {
        mScope = mScope;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getData() {
        return data;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getScope() {
        return mScope;
    }

    /**
     * 设置认证类型
     *
     * @param authType 认证类型
     */
    public void setAuthType(AuthType authType) {
        mAuthType = authType;
    }

    /**
     * 获取AuthType
     *
     * @return authType
     */
    public AuthType getAuthType() {
        return mAuthType;
    }

    /**
     * @return 获取access token
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * @return 获取有效期
     */
    public String getExpiresIn() {
        return mExpiresIn;
    }


    public String getOpenId() {
        return mOpenId;
    }
}
