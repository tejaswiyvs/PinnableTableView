package tejaswi_yerukalapudi.com.pinnabletableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by Teja on 8/3/15.
 */
public class ObservableHorizontalScrollView extends HorizontalScrollView {
    private HorizontalScrollViewListener mScrollViewListener = null;

    public ObservableHorizontalScrollView(Context context) {
        super(context);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(HorizontalScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (null!= mScrollViewListener) {
            mScrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
        }
    }
}
