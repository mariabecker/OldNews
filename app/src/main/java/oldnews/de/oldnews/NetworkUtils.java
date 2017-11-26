package oldnews.de.oldnews;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.Scanner;

/**
 * Created by maike on 25.11.17.
 */

public class NetworkUtils {

    final static String TAG = "OldNews NetworkUtils";

    final static String BASE_URL = "http://altpapier-app.de/api/v1/content";
    final static String PARAM_QUERY = "";
    final static String PAGE_NUMBER_URL = "page";
    final static String PAGE_SIZE_URL = "pagesize";

    public static URL buildRequestUrl(int pageNumber, int pageSize) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PAGE_NUMBER_URL, Integer.toString(pageNumber))
                .appendQueryParameter(PAGE_SIZE_URL, Integer.toString(pageSize))
                .build();

        URL apiRequestUrl = null;
        try {
            apiRequestUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return apiRequestUrl;
    }

    public static JSONArray requestNewsItems(URL url) throws IOException {
        HttpURLConnection apiConnection = (HttpURLConnection) url.openConnection();
        JSONArray newsItems = null;
        BufferedReader inputReader = null;
        try {
           inputReader = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
           StringBuilder stringBuilder = new StringBuilder();
           String inputLine;

           while((inputLine = inputReader.readLine()) != null) {
               stringBuilder.append(inputLine);
           }

           String response = stringBuilder.toString();
           Log.d(TAG, "response from api " + response);

           newsItems = new JSONArray(response);

        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            if(inputReader != null) {
                inputReader.close();
            }

            apiConnection.disconnect();
        }

        return newsItems;
    }

}
