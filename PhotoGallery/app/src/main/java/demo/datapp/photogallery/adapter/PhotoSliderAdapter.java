package demo.datapp.photogallery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import demo.datapp.photogallery.fragment.PhotoSlideFragment;

/**
 * Created by hat on 12/18/15.
 */
public class PhotoSliderAdapter extends FragmentStatePagerAdapter {
    private final int count;
    private PhotoSlideFragment mCurrentFragment;

    public PhotoSliderAdapter(FragmentManager fm, int c) {
        super(fm);
        count = c;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoSlideFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentFragment = (PhotoSlideFragment) object;
    }

    public PhotoSlideFragment getCurrentDetailsFragment() {
        return mCurrentFragment;
    }
}
