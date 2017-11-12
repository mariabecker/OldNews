package oldnews.de.oldnews;

/**
 * Created by maike on 12.11.17.
 */

public class NewsItem {

    private String newsText;

    public NewsItem() {
        this.newsText = "leer";
    }

    public NewsItem(String newsText) {
        this.newsText = newsText;
    }

    public String getNewsText() {
        return this.newsText;
    }
}
