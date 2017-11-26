package oldnews.de.oldnews;

import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.URL;

import oldnews.de.oldnews.db.AppDatabase;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    private static final String TAG = "OldNews App";
    public final static int PAGE_SIZE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppDatabase.initDatabase(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), this));

        URL testUrl = NetworkUtils.buildRequestUrl(1, 10);
        Log.v(TAG, "created url " + testUrl.toString());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareNewsText(View view) {
        String mimeType = "plain/text";

        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle(R.string.share)
                .setText("Hallo. Ich bin ein Test.")
                .startChooser();
    }

}
