package demo.datapp.photogallery.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import demo.datapp.photogallery.R;
import demo.datapp.photogallery.activity.DetailsActivity;
import demo.datapp.photogallery.helper.BitmapTransform;
import demo.datapp.photogallery.helper.SizeHelper;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by hat on 12/18/15.
 */
public class PhotoSlideFragment extends Fragment implements ImageViewTouch.OnImageViewTouchSingleTapListener {
    private static final String ARG_SELECTED_IMAGE_POSITION = "arg_selected_image_position";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slider_item, container, false);
        int selectedPosition = getArguments().getInt(ARG_SELECTED_IMAGE_POSITION);
        DetailsActivity activity = (DetailsActivity) getActivity();

        ImageViewTouch detailPhotoView = (ImageViewTouch)rootView.findViewById(R.id.detail_photo);
        detailPhotoView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        detailPhotoView.setTransitionName(activity.getString(R.string.photo_transition_prefix) + selectedPosition);
        detailPhotoView.setSingleTapListener(this);
        Picasso.with(getActivity())
                .load(Uri.parse(activity.getPhoto(selectedPosition).getUrl()))
                .into(detailPhotoView);

        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });

        return rootView;
    }

    @Nullable
    public View getSharedElement() {
        return getView().findViewById(R.id.detail_photo);
    }

    public static PhotoSlideFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_IMAGE_POSITION, position);
        PhotoSlideFragment fragment = new PhotoSlideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSingleTapConfirmed() {
        DetailsActivity activity = (DetailsActivity) getActivity();
        activity.toggleUI();
    }
}
