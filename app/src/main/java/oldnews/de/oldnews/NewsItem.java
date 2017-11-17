package oldnews.de.oldnews;

import android.content.Context;
import android.widget.ImageButton;

import java.util.Date;

/**
 * Created by maike on 12.11.17.
 */

public class NewsItem {

    private String mNewsText;
    private String mDate;
    private String mNewspaperName;
    //private ImageButton mFavorites_button;

    public NewsItem() {

        this.mNewsText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        this.mDate = "03.11.1917";
        this.mNewspaperName = "Zeitungsname";
        //this.mFavorites_button = new ImageButton();
    }

    public NewsItem(String newsText, String date, String newspaperName) {
        this.mNewsText = newsText;
        this.mDate = date;
        this.mNewspaperName = newspaperName;
        //this.mFavorites_button = imageButton;
    }

    public String getNewsText() {
        return this.mNewsText;
    }

    public String getNewsDate() {
        return this.mDate;
    }

    public String getNewspaperName() {
        return this.mNewspaperName;
    }
}
