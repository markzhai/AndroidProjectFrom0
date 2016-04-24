package cn.zhaiyifan.authorize;

import android.app.Activity;
import android.content.Intent;

/**
 * @author markzhai on 16/3/1
 * @version 1.0.0
 */
public interface Auth {
    void auth(Activity activity, AuthCallback callback);

    void release();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    class NullAuth implements Auth {

        @Override
        public void auth(Activity activity, AuthCallback callback) {

        }

        @Override
        public void release() {

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }
    }
}
