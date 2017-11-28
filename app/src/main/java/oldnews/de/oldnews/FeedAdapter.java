package oldnews.de.oldnews;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private String mode;

    public FeedAdapter(String mode) {

        this.mode = mode;
        AppDatabase db = AppDatabase.getDatabase();
        articleDao = db.articleDao();
        fetchedArticles = new HashMap<>();


        mNewsItems = new ArrayList<NewsItem>();

        if(mode.equals("all")) {
            load();

            Log.d(TAG, "starting api request");
            URL apiRequest = NetworkUtils.buildRequestUrl(1, MainActivity.PAGE_SIZE);
            Log.d(TAG, "starting new async task");
            new FetchNewsItems().execute(apiRequest);
        }else if(mode.equals("fav")){
            loadFavourites();
        }
        /*for(int i = 0; i < 100; i++) {
            Log.d(TAG, "adding item " + i);
            NewsItem newsItem = new NewsItem();
            mNewsItems.add(newsItem);
        }*/


    }


    private void loadFavourites() {
        mNewsItems.clear();
        new FetchFavsFromDb().execute();
    }

    private void load(){
        mNewsItems.clear();
        new FetchFromDB().execute();
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

            NewsItem newsItem = new NewsItem(a.id, text, dateIssued, newspaperName, a.isFavourite);
            mNewsItems.add(newsItem);
            newItems++;
        }

        notifyItemRangeChanged(0, mNewsItems.size());

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


//                    NewsItem newsItem = new NewsItem(text, dateIssued, newspaperName);
//                    mNewsItems.add(newsItem);
                    newItems++;
                }
                fetchedArticles.put(a.externalId, a);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        new SaveArticles().execute(articlesToSave);
        new UpdateArticles().execute(articlesToUpdate);
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
        NewsItem item = mNewsItems.get(position);
        String newsText = item.getNewsText();
        Log.d(TAG, "onBind fav: " + item.isFavourite() + " - pos: " + position + " - text " + newsText);
        holder.mNewsText.setText(newsText);
        holder.mNewspaperName.setText(item.getNewspaperName());
        holder.mDate.setText(item.getNewsDate());
        if(item.isFavourite()) {
            holder.mFavoriteFill.setVisibility(View.VISIBLE);
        }else{
            holder.mFavoriteFill.setVisibility(View.INVISIBLE);
        }
        //holder.mFavoriteButton.getDrawable().setColorFilter(item.isFavourite()? 0xFFFFFF00:0xFF000000, PorterDuff.Mode.MULTIPLY);
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
        private ImageView mFavoriteFill;


        FeedAdapterViewHolder(View view) {
            super(view);

            mCardView = (CardView) view.findViewById(R.id.card_view);
            mNewsText = (TextView) view.findViewById(R.id.news_text);
            mDate = (TextView) view.findViewById(R.id.date);
            mNewspaperName = (TextView) view.findViewById(R.id.newspaper_name);
            mFavoriteButton = (ImageButton) view.findViewById(R.id.favorite_button);
            mFavoriteFill = (ImageView) view.findViewById(R.id.favorite_fill);

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if(mNewsItems.size()>0) {
                        NewsItem item = mNewsItems.get(position);
                        Log.d(TAG, "favclick " + item.getNewsText());
                        new FavArticle().execute(item);
                    }
                }
            });
        }
    }

    private class FetchFromDB extends AsyncTask<Void, Void, Article[]>{

        @Override
        protected Article[] doInBackground(Void... voids) {
            Article[] articles = articleDao.loadAllArticles();
            Log.d(TAG, "requesting content from database, got " + articles.length + " items");
            return articles;
        }

        @Override
        protected void onPostExecute(Article[] articles) {
            setNewsItemsFromDB(articles);
        }
    }

    private class FetchFavsFromDb extends AsyncTask<Void, Void, Article[]>{

        @Override
        protected Article[] doInBackground(Void... voids) {
            Article[] articles = articleDao.loadFavouriteArticles();
            return articles;
        }

        @Override
        protected void onPostExecute(Article[] articles) {
            setNewsItemsFromDB(articles);
        }
    }

    private class FavArticle extends AsyncTask<NewsItem, Void, Integer>{

        @Override
        protected Integer doInBackground(NewsItem... items) {

            if(items[0].isFavourite()){
                articleDao.setArticleUnfavourite(items[0].getId());
            }else{
                articleDao.setArticleFavourite(items[0].getId());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(mode.equals("all")){
                load();
            }else if(mode.equals("fav")){
                loadFavourites();
            }
        }
    }

    private class SaveArticles extends AsyncTask<List<Article>, Void, Integer>{


        @Override
        protected Integer doInBackground(List<Article>... articles) {
            Article[] articleArray = new Article[articles[0].size()];
            articleDao.insertArticles(articles[0].toArray(articleArray));

            Log.d(TAG, "saved  " + articles.length + " items");
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            load();
        }
    }


    private class UpdateArticles extends AsyncTask<List<Article>, Void, Integer>{


        @Override
        protected Integer doInBackground(List<Article>... articles) {
            List<Article> articleList = articles[0];
            for(Article a: articleList){
                articleDao.updateArticle(a.externalId, a.headline, a.text, a.isDeleted, a.lastModified);
            }

            Log.d(TAG, "updated " + articleList.size() + " items");
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
