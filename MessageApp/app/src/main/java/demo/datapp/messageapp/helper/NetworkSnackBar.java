package demo.datapp.messageapp.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import demo.datapp.messageapp.R;

public class NetworkSnackBar extends LinearLayout {
    private Animation slide_down;
    private Animation slide_up;
    private boolean isShowing;

    public NetworkSnackBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public NetworkSnackBar(Context context) {
        super(context);
        initializeViews(context);
    }

    private TextView action;

    public boolean isShowing() {
        return isShowing;
    }

    public void hide() {
        isShowing = false;
        startAnimation(slide_down);
    }

    public void show() {
        isShowing = true;
        startAnimation(slide_up);
    }

    public void addActionOnClickListener(OnClickListener listener) {
        action.setOnClickListener(listener);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.network_snackbar, this);
        slide_down = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);

        slide_up = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);

        isShowing = false;
        setVisibility(GONE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        action = (TextView) findViewById(R.id.network_error_action);
        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
