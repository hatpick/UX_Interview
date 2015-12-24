package demo.datapp.messageapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.timeago.TimeAgo;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import demo.datapp.messageapp.helper.CircleTransform;
import demo.datapp.messageapp.helper.ItemTouchHelperAdapter;
import demo.datapp.messageapp.helper.ItemTouchHelperViewHolder;
import demo.datapp.messageapp.R;
import demo.datapp.messageapp.helper.Utils;
import demo.datapp.messageapp.model.MessageModel;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_LOADING = 0;
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private Context mContext;
    private ArrayList<MessageModel> messages;
    private final String TAG = MessageAdapter.class.getName();
    private TimeAgo timeAgo;
    private CircleTransform circleTransform;
    private SimpleDateFormat sdf;

    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onItemDismiss(int position) {
        removeMessage(position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public MessageAdapter(Context mContext, ArrayList<MessageModel> messages, RecyclerView recyclerView) {
        this.messages = messages;
        this.mContext = mContext;
        this.timeAgo = new TimeAgo();
        this.circleTransform = new CircleTransform();

        sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    private String dateStringToTimeAgoString(String dateString) {
        try {
            return timeAgo.timeAgo(sdf.parse(dateString));
        } catch (ParseException e) {
            Log.e(TAG, "Bad date format:" + (dateString != null ? dateString : "NULL"));
            return dateString;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_layout, parent, false);
            vh = new MessageHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_layout, parent, false);
            vh = new ProgressViewHolder(v);
            ((ProgressViewHolder)vh).progressBar.getIndeterminateDrawable().setColorFilter(
                    mContext.getResources().getColor(R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder) {
            MessageHolder messageHolder = (MessageHolder) holder;
            MessageModel messageModel = messages.get(position);
            messageHolder.authorName.setText(messageModel.getAuthorName());
            messageHolder.messageContent.setText(messageModel.getContent());
            messageHolder.messageDate.setText(dateStringToTimeAgoString(messageModel.getUpdatedDate()));
            Picasso.with(mContext)
                    .load(mContext.getString(R.string.message_author_avatar_url_prefix) + messageModel.getAuthorAvatar())
                    .resize((int) Utils.convertDpToPixel(40.0f, mContext), (int) Utils.convertDpToPixel(40.0f, mContext))
                    .centerInside()
                    .transform(circleTransform)
                    .into(messageHolder.authorAvatar);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void removeMessage(int position) {
        messages.remove(position);
        notifyItemRangeRemoved(position, 1);
    }

    public void addMessage(MessageModel mm, int position) {
        messages.add(position, mm);
        notifyItemInserted(position);
    }

    public void clearMessages() {
        messages.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position) != null ? VIEW_ITEM : VIEW_LOADING;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public ImageView authorAvatar;
        public TextView authorName;
        public CardView messageHolder;
        public TextView messageDate;
        public TextView messageContent;

        MessageHolder(View view) {
            super(view);
            this.messageHolder = (CardView) view.findViewById(R.id.message_holder);
            this.authorAvatar = (ImageView) view.findViewById(R.id.message_author_avatar);
            this.authorName = (TextView) view.findViewById(R.id.message_author_name);
            this.messageContent = (TextView) view.findViewById(R.id.message_content);
            this.messageDate = (TextView) view.findViewById(R.id.message_date);
        }

        @Override
        public void onItemSelected() {}

        @Override
        public void onItemClear() {}
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.loading_more_progress);
        }
    }
}
