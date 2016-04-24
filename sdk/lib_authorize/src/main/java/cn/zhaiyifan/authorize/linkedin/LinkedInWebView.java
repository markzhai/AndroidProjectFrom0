package cn.zhaiyifan.authorize.linkedin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * @author markzhai on 16/3/8
 * @version 1.0.0
 */
public class LinkedInWebView extends WebView {

    public LinkedInWebView(Context context) {
        this(context, null);
    }

    public LinkedInWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkedInWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!hasFocus()) {
                    requestFocus();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }


}
