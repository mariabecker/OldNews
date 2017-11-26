package oldnews.de.oldnews;

import android.content.Context;
import android.net.Network;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by maike on 12.11.17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private static final String DATE_ISSUED = "dateIssued";
    private static final String NEWSPAPER_NAME = "newspaperTitle";
    private static final String NEWSPAPER_TEXT = "text";

    private ArrayList<NewsItem> mNewsItems;

    public FeedAdapter() {
        mNewsItems = new ArrayList<NewsItem>();

        Log.d(TAG, "starting api request");
        URL apiRequest = NetworkUtils.buildRequestUrl(1, MainActivity.PAGE_SIZE);
        Log.d(TAG, "starting new async task");
        new FetchNewsItems().execute(apiRequest);

        /*for(int i = 0; i < 100; i++) {
            Log.d(TAG, "adding item " + i);
            NewsItem newsItem = new NewsItem();
            mNewsItems.add(newsItem);
        }*/
    }


    public void setNewsItems(JSONArray newsItemsJson) {

        int oldPosition = mNewsItems.size()-1;

        for(int i = 0; i < newsItemsJson.length(); i++) {

            try {
                JSONObject currentJsonNewsItem = newsItemsJson.getJSONObject(i);

                String dateIssued = JsonUtils.getStringFromJson(currentJsonNewsItem, DATE_ISSUED);
                String newspaperName = JsonUtils.getStringFromJson(currentJsonNewsItem, NEWSPAPER_NAME);
                String newspaperText = JsonUtils.getStringFromJson(currentJsonNewsItem, NEWSPAPER_TEXT);
                //String newspaperText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

                NewsItem newsItem = new NewsItem(newspaperText, dateIssued, newspaperName);
                mNewsItems.add(newsItem);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        notifyItemRangeChanged(oldPosition, newsItemsJson.length());
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
        holder.mNewspaperName.setText(mNewsItems.get(position).getNewspaperName());
        holder.mDate.setText(mNewsItems.get(position).getNewsDate());
    }

    @Override
    public int getItemCount() {
        if(mNewsItems == null) {
            return 0;
        }
        return mNewsItems.size();
    }

    class FeedAdapterViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mNewsText;
        private TextView mDate;
        private TextView mNewspaperName;
        private ImageButton mFavoriteButton;

        FeedAdapterViewHolder(View view) {
            super(view);

            mCardView = (CardView) view.findViewById(R.id.card_view);
            mNewsText = (TextView) view.findViewById(R.id.news_text);
            mDate = (TextView) view.findViewById(R.id.date);
            mNewspaperName = (TextView) view.findViewById(R.id.newspaper_name);
            mFavoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
        }
    }

    private class FetchNewsItems extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(URL... urls) {
            URL url = urls[0];
            Log.d(TAG, "requesting content from " + url);
            JSONArray newsItems = null;

            try {
                newsItems = NetworkUtils.requestNewsItems(url);
            } catch(IOException e ) {
                e.printStackTrace();
            }

            return newsItems;
        }


        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            //TODO: finish this method by constructing newsItems from the json objects in the array
            Log.d(TAG, jsonArray.toString());
            setNewsItems(jsonArray);
        }
    }
}
