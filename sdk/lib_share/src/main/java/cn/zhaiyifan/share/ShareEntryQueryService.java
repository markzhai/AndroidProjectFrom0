package cn.zhaiyifan.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

/**
 * @author markzhai
 * @version 1.0.0
 */
public class ShareEntryQueryService implements IQueryShareEntryService {

    private static final String TAG = "ShareEntryQueryService";
    private List<ResolveInfo> mList;

    @Override
    public List<ResolveInfo> getShareEntryList(Context context) {
        if (mList == null) {
            updateShareEntryList(context);
        }
        return mList;
    }

    public void updateShareEntryList(Context context) {
        try {
            if (context != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND, null);
                shareIntent.setType("image/*");
                PackageManager pm = context.getPackageManager();
                Log.i(TAG, "queryIntentActivities start");
                mList = pm.queryIntentActivities(shareIntent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                Log.i(TAG, "queryIntentActivities end");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
