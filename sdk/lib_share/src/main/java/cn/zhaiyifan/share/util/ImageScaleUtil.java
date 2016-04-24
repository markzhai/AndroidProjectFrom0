package cn.zhaiyifan.share.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageScaleUtil {

    public static Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Options queryImgFileOptions(String fileName) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        return options;
    }

    public static boolean checkBitmapFitsInMemory(long bmpwidth, long bmpheight, int bmpdensity) {
        long requestSize = bmpwidth * bmpheight * bmpdensity;
        return requestSize <= getFreeMemorySize();
    }

    public static long getFreeMemorySize() {
        long max = Runtime.getRuntime().maxMemory();
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapRemaining = Runtime.getRuntime().freeMemory();
        long nativeUsage = Debug.getNativeHeapAllocatedSize();
        return (max - heapSize - nativeUsage) + heapRemaining;
    }

    public static int getBitmapBpp(Config cfg) {
        int ret = 2;
        if (cfg != null) {
            switch (cfg) {
                case ALPHA_8:
                    ret = 1;
                    break;
                case ARGB_4444:
                case RGB_565:
                    ret = 2;
                    break;
                case ARGB_8888:
                    ret = 4;
                    break;
            }
        }
        return ret;
    }

    public static Bitmap decodeFileWithMemCheck(String fileName, int dstWidth, int dstHeight,
                                                ScalingLogic scalingLogic) throws FileNotFoundException {
        final File f = new File(fileName);

        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);

        Bitmap unscaledBitmap = null;
        options.inTempStorage = new byte[64 * 1024];
        options.inPurgeable = true;
        unscaledBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        return unscaledBitmap;
    }

    public static Bitmap decodeFile(String fileName, int dstWidth, int dstHeight,
                                    ScalingLogic scalingLogic) {
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                    dstHeight, scalingLogic);

            Bitmap unscaledBitmap = null;
            Bitmap img = null;
            //请在基础类外的业务逻辑类加判断
            unscaledBitmap = BitmapFactory.decodeFile(fileName, options);

            if (scalingLogic == ScalingLogic.SCALE_CROP) {
                img = createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, ScalingLogic.CROP);
                unscaledBitmap.recycle();
                return img;
            } else
                return unscaledBitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                            ScalingLogic scalingLogic) {
        if (unscaledBitmap == null) {
            return null;
        }
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeigh) {
        if (unscaledBitmap == null) {
            return null;
        }

        int w = unscaledBitmap.getWidth();
        int h = unscaledBitmap.getHeight();
        if (w < 0 || h < 0 || dstWidth < 0 || dstHeigh < 0) {
            return unscaledBitmap;
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(dstWidth, dstHeigh, Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, dstWidth / 2 - w / 2, dstHeigh / 2 - h / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeigh, int backgroundColor, Config config) {
        if (unscaledBitmap == null) {
            return null;
        }

        int w = unscaledBitmap.getWidth();
        int h = unscaledBitmap.getHeight();
        if (w < 0 || h < 0 || dstWidth < 0 || dstHeigh < 0) {
            return unscaledBitmap;
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(dstWidth, dstHeigh, config);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(unscaledBitmap, dstWidth / 2 - w / 2, dstHeigh / 2 - h / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeigh, int backgroundColor) {
        return createScaledBitmap(unscaledBitmap, dstWidth, dstHeigh, backgroundColor, Config.ARGB_8888);
    }

    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                          ScalingLogic scalingLogic) {

        if ((dstWidth == 0) || (dstHeight == 0))
            return 1;

        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else if (scalingLogic == ScalingLogic.SCALE_CROP) {
            final float wAspect = srcWidth / dstWidth;
            final float hAspect = srcHeight / dstHeight;
            return (int) ((wAspect > hAspect) ? wAspect : hAspect);
        } else {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;
            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;
            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;
            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    public enum ScalingLogic {
        CROP, FIT, SCALE_CROP
    }

}
