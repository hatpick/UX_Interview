package demo.datapp.photogallery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import demo.datapp.photogallery.R;
import demo.datapp.photogallery.adapter.PhotoSliderAdapter;
import demo.datapp.photogallery.helper.DepthPageTransformer;
import demo.datapp.photogallery.helper.SizeHelper;
import demo.datapp.photogallery.model.PhotoModel;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = DetailsActivity.class.getName();
    private static final String STATE_CURRENT_POSITION = "state_current_position";
    private static final String STATE_OLD_POSITION = "state_old_position";

    static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";

    private int mCurrentPosition;
    private int mOriginalPosition;
    private boolean mIsReturning;

    private ArrayList<PhotoModel> photos;
    private ImageView closeViewer;
    private TextView detailsLabel;

    private ViewPager photoSlider;
    private PhotoSliderAdapter pagerAdapter;

    private boolean hidden;
    private FrameLayout header;
    private LinearLayout footer;
    Animation fadeInAnimation;
    Animation fadeOutAnimation;
    private View mDecorView;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
        outState.putInt(STATE_OLD_POSITION, mOriginalPosition);
        outState.putParcelableArrayList("photos", photos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        postponeEnterTransition();
        setEnterSharedElementCallback(mCallback);

        if (savedInstanceState == null) {
            mCurrentPosition = getIntent().getExtras().getInt(EXTRA_CURRENT_ITEM_POSITION);
            mOriginalPosition = mCurrentPosition;
            photos = getIntent().getParcelableArrayListExtra("photos");
        } else {
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION);
            mOriginalPosition = savedInstanceState.getInt(STATE_OLD_POSITION);
            photos = savedInstanceState.getParcelableArrayList("photos");
        }

        hidden = false;

        detailsLabel = (TextView) findViewById(R.id.detail_photo_title);
        detailsLabel.setText(photos.get(mCurrentPosition).getPrettyName());

        header = (FrameLayout) findViewById(R.id.details_header);
        footer = (LinearLayout) findViewById(R.id.details_footer);
        photoSlider = (ViewPager) findViewById(R.id.photo_slider);
        photoSlider.setPageTransformer(true, new DepthPageTransformer());
        pagerAdapter = new PhotoSliderAdapter(getSupportFragmentManager(), photos.size());
        photoSlider.setAdapter(pagerAdapter);

        closeViewer = (ImageView) findViewById(R.id.close_photo_viewer);
        closeViewer.setOnClickListener(this);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnimation.setFillAfter(true);
        fadeOutAnimation= AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutAnimation.setFillAfter(true);

        photoSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                detailsLabel.setText(photos.get(position).getPrettyName());
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        photoSlider.setCurrentItem(mCurrentPosition);

        getWindow().getSharedElementEnterTransition().setDuration(getResources().getInteger(R.integer.transition_duration_millis));
    }

    private Transition makeEnterTransition(View sharedElement) {
        View rootView = pagerAdapter.getCurrentDetailsFragment().getView();
        assert rootView != null;

        TransitionSet enterTransition = new TransitionSet();

        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        enterTransition.addTransition(fade);

        enterTransition.setDuration(getResources().getInteger(R.integer.transition_duration_millis));
        return enterTransition;
    }

    private Transition makeReturnTransition() {
        View rootView = pagerAdapter.getCurrentDetailsFragment().getView();
        assert rootView != null;

        TransitionSet returnTransition = new TransitionSet();
        returnTransition.addTransition(new Fade());

        returnTransition.setDuration(getResources().getInteger(R.integer.transition_duration_millis));
        return returnTransition;
    }

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                View sharedView = pagerAdapter.getCurrentDetailsFragment().getSharedElement();
                if (sharedView == null) {
                    names.clear();
                    sharedElements.clear();
                } else if (mCurrentPosition != mOriginalPosition) {
                    names.clear();
                    sharedElements.clear();
                    names.add(sharedView.getTransitionName());
                    sharedElements.put(sharedView.getTransitionName(), sharedView);
                }
            }
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements,
                                         List<View> sharedElementSnapshots) {
            if (!mIsReturning) {
                getWindow().setEnterTransition(makeEnterTransition(getSharedElement(sharedElements)));
            }
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements,
                                       List<View> sharedElementSnapshots) {
            if (mIsReturning) {
                getWindow().setReturnTransition(makeReturnTransition());
            }
        }

        private View getSharedElement(List<View> sharedElements) {
            for (final View view : sharedElements) {
                if (view instanceof ImageView) {
                    return view;
                }
            }
            return null;
        }
    };

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.close_photo_viewer) {
            finishAfterTransition();
        }
    }

    public PhotoModel getPhoto(int position) {
        if(position < photos.size())
            return photos.get(position);
        return null;
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        getWindow().setReturnTransition(makeReturnTransition());
        Intent data = new Intent();
        data.putExtra(EXTRA_OLD_ITEM_POSITION, getIntent().getExtras().getInt(EXTRA_CURRENT_ITEM_POSITION));
        data.putExtra(EXTRA_CURRENT_ITEM_POSITION, mCurrentPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }
}


/*  if (hidden) {
        header.startAnimation(fadeInAnimation);
        footer.startAnimation(fadeInAnimation);
        showSystemUI();
    } else {
        header.startAnimation(fadeOutAnimation);
        footer.startAnimation(fadeOutAnimation);
        hideSystemUI();
    }
    hidden = !hidden;*/
