package cn.zhaiyifan.share.data;

import android.graphics.Bitmap;

/**
 * @author markzhai
 * @version 1.0.0
 */
public class ShareInfo {
    /**
     * 分享内容title
     */
    public String mTitle;
    /**
     * 分享内容文本
     */
    public String mContent;
    /**
     * 分享链接
     */
    public String mUrl;
    /**
     * 分享图片url
     */
    public String mImageUrl;
    /**
     * 分享视频url
     */
    public String mVideoUrl;
    /**
     * 分享图片bitmap
     */
    public Bitmap mImageBitmap;

    //微博3.2版sdk，url分享必须要有thumb
    //以下参数2选1即可
    //注意：最终压缩过的缩略图大小不得超过 32kb。
    public int mThumbResId;

    public Bitmap mThumbBmp;
}
