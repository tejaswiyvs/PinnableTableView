package tejaswi_yerukalapudi.com.pinnabletableview.Lib.Helper;

import android.content.Context;

/**
 * Created by Teja on 8/12/2015.
 */
public class Helper {

    public static int getHeightForDP(Context ctx, float dp) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}