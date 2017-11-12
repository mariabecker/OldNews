package oldnews.de.oldnews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maike on 12.11.17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder> {

    public static final String TAG = FeedAdapter.class.getSimpleName();

    private ArrayList<NewsItem> mNewsItems;

    public FeedAdapter() {
        mNewsItems = new ArrayList<NewsItem>();

        for(int i = 0; i < 100; i++) {
            Log.d(TAG, "adding item " + i);
            NewsItem newsItem = new NewsItem("item nr " + i);
            mNewsItems.add(newsItem);
        }
    }

    @Override
    public FeedAdapter.FeedAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.news_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FeedAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.FeedAdapterViewHolder holder, int position) {
        String newsText = mNewsItems.get(position).getNewsText();
        Log.d(TAG, "onBind " + position + " " + newsText);
        holder.mNewsText.setText(newsText);
    }

    @Override
    public int getItemCount() {
        if(mNewsItems == null) {
            return 0;
        }
        return mNewsItems.size();
    }

    class FeedAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mNewsText;

        FeedAdapterViewHolder(View view) {
            super(view);

            mNewsText = (TextView) view.findViewById(R.id.news_element);
        }

    }
}
