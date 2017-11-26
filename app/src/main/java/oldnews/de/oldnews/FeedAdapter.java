package oldnews.de.oldnews;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldnews.de.oldnews.db.AppDatabase;
import oldnews.de.oldnews.db.Article;
import oldnews.de.oldnews.db.ArticleDao;

/**
 * Created by maike on 12.11.17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedAdapterViewHolder> {

    private static final String TAG = FeedAdapter.class.getSimpleName();
    private static final String ID = "id";
    private static final String DATE_ISSUED = "dateIssued";
    private static final String NEWSPAPER_NAME = "newspaperTitle";
    private static final String NEWSPAPER_VOLUME = "volume";
    private static final String NEWSPAPER_ISSUE = "issue";
    private static final String NEWSPAPER_HEADLINE = "headline";
    private static final String NEWSPAPER_TEXT = "text";
    private static final String IMAGE_URL = "imageUrl";
    private static final String IMAGE_WIDTH = "imageWidth";
    private static final String IMAGE_HEIGHT = "imageHeight";
    private static final String IMAGE_SIZE = "imageSize";
    private static final String IS_DELETED = "isDeleted";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String PAHE_NR = "pageNr";


    private ArrayList<NewsItem> mNewsItems;

    private final ArticleDao articleDao;
    private Map<Integer, Article> fetchedArticles;

    public FeedAdapter() {

        AppDatabase db = AppDatabase.getDatabase();
        articleDao = db.articleDao();
        fetchedArticles = new HashMap<>();


        mNewsItems = new ArrayList<NewsItem>();
        new FetchFromDB().execute();

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

    public void setNewsItemsFromDB(Article[] articles){
        int oldPosition = mNewsItems.size()-1;

        int newItems = 0;

        for(int i = 0; i < articles.length; i++){
            Article a = articles[i];

            String dateIssued = a.dateIssued;
            String newspaperName = a.newspaperName;
            String text = a.headline + "\n\n" + a.text;

            fetchedArticles.put(a.externalId, a);

            NewsItem newsItem = new NewsItem(text, dateIssued, newspaperName);
            mNewsItems.add(newsItem);
            newItems++;
        }


        notifyItemRangeChanged(oldPosition, newItems);
    }

    public void saveNewsItems(JSONArray newsItemsJson) {

        if(newsItemsJson == null) return;

        int oldPosition = mNewsItems.size()-1;
        int newItems = 0;

        List<Article> articlesToSave = new ArrayList<>();
        List<Article> articlesToUpdate = new ArrayList<>();

        for(int i = 0; i < newsItemsJson.length(); i++) {

            try {
                JSONObject currentJsonNewsItem = newsItemsJson.getJSONObject(i);

                Article a = JsonUtils.buildArticle(currentJsonNewsItem);

                if(fetchedArticles.containsKey(a.externalId)){
                    Article oldArticle = fetchedArticles.get(a.externalId);
                    if(!oldArticle.lastModified.equals(a.lastModified)){
                        articlesToUpdate.add(a);

                    }
                }else{
                    articlesToSave.add(a);

                    String dateIssued = a.dateIssued;
                    String newspaperName = a.newspaperName;
                    String text = a.headline + "\n\n" + a.text;


                    NewsItem newsItem = new NewsItem(text, dateIssued, newspaperName);
                    mNewsItems.add(newsItem);
                    newItems++;
                }
                fetchedArticles.put(a.externalId, a);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        new SaveArticles().execute(articlesToSave);
        new UpdateArticles().execute(articlesToUpdate);
        notifyItemRangeChanged(oldPosition, newItems);
    }


    public void setNewsItems(JSONArray newsItemsJson) {

        int oldPosition = mNewsItems.size()-1;

        for(int i = 0; i < newsItemsJson.length(); i++) {

            try {
                JSONObject currentJsonNewsItem = newsItemsJson.getJSONObject(i);

                String dateIssued = JsonUtils.getStringFromJson(currentJsonNewsItem, DATE_ISSUED);
                String newspaperName = JsonUtils.getStringFromJson(currentJsonNewsItem, NEWSPAPER_NAME);
                String newspaperText = JsonUtils.getStringFromJson(currentJsonNewsItem, NEWSPAPER_HEADLINE) + "\n\n" + JsonUtils.getStringFromJson(currentJsonNewsItem, NEWSPAPER_TEXT);
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

    private class FetchFromDB extends AsyncTask<Void, Void, Article[]>{

        @Override
        protected Article[] doInBackground(Void... voids) {
            return articleDao.loadAllArticles();
        }

        @Override
        protected void onPostExecute(Article[] articles) {
            setNewsItemsFromDB(articles);
        }
    }

    private class SaveArticles extends AsyncTask<List<Article>, Void, Integer>{


        @Override
        protected Integer doInBackground(List<Article>... articles) {
            Article[] articleArray = new Article[articles[0].size()];
            articleDao.insertArticles(articles[0].toArray(articleArray));

            return 0;
        }
    }


    private class UpdateArticles extends AsyncTask<List<Article>, Void, Integer>{


        @Override
        protected Integer doInBackground(List<Article>... articles) {
            List<Article> articleList = articles[0];
            for(Article a: articleList){
                articleDao.updateArticle(a.externalId, a.headline, a.text, a.isDeleted, a.lastModified);
            }

            return 0;
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
            //Log.d(TAG, jsonArray.toString());
            Log.d(TAG, ""+(jsonArray == null));
            saveNewsItems(jsonArray);
        }
    }
}
