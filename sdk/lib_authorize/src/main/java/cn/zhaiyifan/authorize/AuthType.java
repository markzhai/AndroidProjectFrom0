package cn.zhaiyifan.authorize;

public enum AuthType {
    NULL(-1),
    /**
     * 微信登录
     */
    WECHAT(4),
    /**
     * qq登录
     */
    QQ(3),
    /**
     * 微博登录
     */
    WEIBO(2),

    /**
     * 淘宝登录
     */
    LINKED_IN(1);

    private int code;

    AuthType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        switch (code) {
            case 1:
                return "LinkedIn_Web";
            case 2:
                return "Weibo_Native";
            case 3:
                return "QQ_NATIVE";
            case 4:
                return "Weixin_Native";
        }
        return "NULL";
    }
}
