package any.audio.Managers;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by Ankit on 2/3/2017.
 */

public class AnyAudioLinearLayoutManager extends LinearLayoutManager {

// --Commented out by Inspection START (2/25/2017 3:18 PM):
//    public AnyAudioLinearLayoutManager(Context context) {
//        super(context);
//    }
// --Commented out by Inspection STOP (2/25/2017 3:18 PM)

        private static final float MILLISECONDS_PER_INCH = 5f; //default is 25f (bigger = slower)


        public AnyAudioLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public AnyAudioLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return AnyAudioLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }


}
