package demo.datapp.photogallery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import demo.datapp.photogallery.R;
import demo.datapp.photogallery.activity.DetailsActivity;
import demo.datapp.photogallery.activity.MainActivity;
import demo.datapp.photogallery.helper.SizeHelper;
import demo.datapp.photogallery.model.PhotoModel;

/**
 * Created by hat on 12/18/15.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    private int imageWidth;
    private Context mContext;
    private ArrayList<PhotoModel> photos;
    private final String TAG = PhotoAdapter.class.getName();

    public PhotoAdapter(Context mContext, ArrayList<PhotoModel> photos) {
        this.photos = photos;
        this.mContext = mContext;

        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int totalMargin = (int) SizeHelper.convertDpToPixel(48.0f, mContext);
        imageWidth = (width - totalMargin)/2;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        PhotoHolder photoHolder = new PhotoHolder(v, imageWidth, new PhotoHolder.IMyViewHolderClicks() {
            @Override
            public void onClick(View caller, int position) {
                MainActivity activity = (MainActivity) mContext;
                activity.setmIsReentering(false);
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(EXTRA_CURRENT_ITEM_POSITION, position);
                intent.putExtra("photos", photos);
                ImageView photoView = (ImageView) caller.findViewById(R.id.photo_item_view);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(activity, photoView, photoView.getTransitionName());
                mContext.startActivity(intent, options.toBundle());
            }
        });

        return photoHolder;
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        PhotoModel photoModel = photos.get(position);
        holder.photoView.setTransitionName(mContext.getString(R.string.photo_transition_prefix) + position);
        holder.photoView.setTag(mContext.getString(R.string.photo_transition_prefix) + position);
        Picasso.with(mContext)
                .load(Uri.parse(photoModel.getUrl()))
                .into(holder.photoView);
    }

    public void remove(int position) {
        photos.remove(position);
        notifyItemRemoved(position);
    }

    public void add(PhotoModel pm, int position) {
        photos.add(position, pm);
        notifyItemInserted(position);
    }

    public void clear() {
        photos.clear();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public ImageView photoView;
        public FrameLayout photoHolder;
        public IMyViewHolderClicks mListener;

        PhotoHolder(View view, int width, IMyViewHolderClicks listener) {
            super(view);
            this.mListener = listener;
            this.photoView = (ImageView) view.findViewById(R.id.photo_item_view);
            this.photoHolder = (FrameLayout) view.findViewById(R.id.photo_item_holder);
            this.photoView.getLayoutParams().width = width;
            this.photoView.getLayoutParams().height = (int)(width/1.37f);
            this.photoHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, this.getAdapterPosition());
        }

        public static interface IMyViewHolderClicks {
            public void onClick(View caller, int position);
        }
    }
}
