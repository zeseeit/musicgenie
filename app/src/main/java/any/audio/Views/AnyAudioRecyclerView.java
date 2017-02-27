package any.audio.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import any.audio.Config.Constants;

/**
 * Created by Ankit on 2/3/2017.
 */

public class AnyAudioRecyclerView extends RecyclerView {

    int direction = Constants.DIRECTION.VERTICAL;

    public AnyAudioRecyclerView(Context context,int direction) {
        super(context);
        this.direction = direction;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        double FLING_SCALE_DOWN_FACTOR = 0.8;
        if(direction==Constants.DIRECTION.VERTICAL){
            velocityY *= FLING_SCALE_DOWN_FACTOR;
        }else{
            velocityX *= FLING_SCALE_DOWN_FACTOR;
        }

        return super.fling(velocityX, velocityY);
    }
}
