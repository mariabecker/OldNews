package oldnews.de.oldnews;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

import oldnews.de.oldnews.db.Article;

/**
 * Created by maike on 26.11.17.
 */



public class JsonUtils {

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

    public static String getStringFromJson(JSONObject newsItemJson, String key) {

        try {
            String value = newsItemJson.getString(key);
            //TODO: format String into DD.MM.YYYY format
            return value;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIntFromJson(JSONObject newsItemJson, String key){
        try{
            int value = newsItemJson.getInt(key);
            return value;
        }catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Article buildArticle(JSONObject json){
        Article a = new Article();

        try{
            a.externalId = json.getInt(ID);
            a.dateIssued = json.getString(DATE_ISSUED);
            a.newspaperName = json.getString(NEWSPAPER_NAME);
            a.volume = json.getString(NEWSPAPER_VOLUME);
            a.issue = json.getString(NEWSPAPER_ISSUE);
            a.headline = json.getString(NEWSPAPER_HEADLINE);
            a.text = json.getString(NEWSPAPER_TEXT);
            a.imageUrl = json.getString(IMAGE_URL);
            a.imageWidth = json.getInt(IMAGE_WIDTH);
            a.imageHeight = json.getInt(IMAGE_HEIGHT);
            a.imageSize = json.getInt(IMAGE_SIZE);
            a.isDeleted = json.getBoolean(IS_DELETED);
            a.lastModified = json.getString(LAST_MODIFIED);
            a.pageNr = json.getString(PAHE_NR);
        }catch(JSONException e) {
            e.printStackTrace();
            return null;
        }

        return a;
    }

}
