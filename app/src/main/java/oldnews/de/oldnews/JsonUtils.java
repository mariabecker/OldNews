package oldnews.de.oldnews;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

/**
 * Created by maike on 26.11.17.
 */

public class JsonUtils {

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

}
