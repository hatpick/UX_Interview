package demo.datapp.messageapp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ScaleAnimatiorAdapter extends AnimationAdapter {

    public ScaleAnimatiorAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override protected Animator[] getAnimators(View view) {
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
        };
    }
}
