package demo.datapp.photogallery.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import demo.datapp.photogallery.R;
import demo.datapp.photogallery.adapter.PhotoAdapter;
import demo.datapp.photogallery.model.PhotoModel;

public class MainActivity extends AppCompatActivity {
    static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";

    private static final String TAG = MainActivity.class.getName();
    private final int ITEM_PER_ROW = 2;
    private RecyclerView photosView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PhotoModel> photosList;
    private PhotoAdapter photoAdapter;
    private View mDecorView;

    private Bundle mTmpState;
    private boolean mIsReentering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setExitSharedElementCallback(mCallback);

        mDecorView = getWindow().getDecorView();
        photosList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, photosList);
        photosView = (RecyclerView) findViewById(R.id.photos_view);
        mLayoutManager = new StaggeredGridLayoutManager(ITEM_PER_ROW, StaggeredGridLayoutManager.VERTICAL);
        photosView.setItemAnimator(new DefaultItemAnimator());
        photosView.setLayoutManager(mLayoutManager);
        photosView.setAdapter(photoAdapter);

        listFiles("photos");
    }

    private void listFiles(String dirFrom) {
        if(photoAdapter.getItemCount() > 0) {
            photoAdapter.clear();
        }
        Resources resources = getResources();
        AssetManager assetManager = resources.getAssets();
        String fileList[] = new String[0];
        ImageValidator imageValidator = new ImageValidator();
        try {
            fileList = assetManager.list(dirFrom);
            PhotoModel photoModel;
            int i = 0;
            for (String s:fileList) {
                if(imageValidator.validate(s)) {
                    photoModel = new PhotoModel(s);
                    photoAdapter.add(photoModel, i);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.e("TAG", "error loading files");
        }
    }

    private class ImageValidator{

        private Pattern pattern;
        private Matcher matcher;

        private static final String IMAGE_PATTERN =
                "([^\\s]+(\\.(?i)(jpg))$)";

        public ImageValidator(){
            pattern = Pattern.compile(IMAGE_PATTERN);
        }

        public boolean validate(final String image){

            matcher = pattern.matcher(image);
            return matcher.matches();

        }
    }

    public void setmIsReentering(boolean mIsReentering) {
        this.mIsReentering = mIsReentering;
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mIsReentering = true;
        mTmpState = new Bundle(data.getExtras());
        int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
        int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
        if (oldPosition != currentPosition) {
            photosView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        photosView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                photosView.getViewTreeObserver().removeOnPreDrawListener(this);
                photosView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReentering) {
                int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
                int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
                if (currentPosition != oldPosition) {
                    String newTransitionName = getString(R.string.photo_transition_prefix) + currentPosition;
                    View newSharedView = photosView.findViewWithTag(newTransitionName);
                    if (newSharedView != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedView);
                    }
                }
                mTmpState = null;
            }

            if (!mIsReentering) {
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);

                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            } else {
                names.remove(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                sharedElements.remove(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                names.remove(Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                sharedElements.remove(Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            }
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements,
                                         List<View> sharedElementSnapshots) {
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements,
                                       List<View> sharedElementSnapshots) {
        }
    };

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
