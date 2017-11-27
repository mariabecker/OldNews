package oldnews.de.oldnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by maike on 12.11.17.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentArrayList;
    private ArrayList<String> mTitleList;

    public MainPageAdapter(FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        mFragmentArrayList = new ArrayList<Fragment>();

        mFragmentArrayList.add(new FeedFragment());
        //mFragmentArrayList.add(new FeedFragment());
        //mFragmentArrayList.add(new FeedFragment());

        mTitleList = new ArrayList<String>();
        mTitleList.add(context.getResources().getString(R.string.feed_tab));
        mTitleList.add(context.getResources().getString(R.string.favorites_tab));
        mTitleList.add(context.getResources().getString(R.string.ocr_tab));

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        if(mFragmentArrayList == null) {
            return 0;
        }
        return mFragmentArrayList.size();
    }
}
