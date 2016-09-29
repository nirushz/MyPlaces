package il.co.nnz.myplaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchFragment.goToMapListener, SearchFragment.goToFavoritesListener {

    private PlaceDBhelper helper = new PlaceDBhelper(this);

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //GoogleMap map = SupportMapFragment.GetMapAsync

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    boolean tablet;
    // private MapFragment tabletMap = new MapFragment();
    //private SectionsPagerAdapterTablet tabletSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tablet = getResources().getBoolean(R.bool.has_two_panes);
        Log.d("isTablet", String.valueOf(tablet));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        Log.d("devise", String.valueOf(tablet));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //}

      //  IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
      //  Intent batteryStatus = this.registerReceiver(new PowerConnectionReceiver(), ifilter);

        //String isTablet=getText(R.string.screen_type).toString();
        //Log.d("isTablet", String.valueOf(isTablet));

    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp =PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> deleteFavorites = sp.getStringSet ("delete_favorites", null);
        if (deleteFavorites!=null && deleteFavorites.contains("yes")){
            sp.edit().remove("delete_favorites");
            helper.deleteFavorites();
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_last_search:
                helper.deletePlacesAroundMeTable();
                //connect adapter here - i can use the existing broadcast receiver
                Intent broadcastIntent = new Intent(SearchIntentServise.ACTION_SEARCH_AROUND_ME);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                break;

            case R.id.action_settings:
                Intent setting = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(setting);
                break;

            case R.id.action_exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void goToFavoritesFragment(int position) {

        mViewPager.setCurrentItem(position);
    }

    @Override
    public void goToMapFragment(int position, Place place, int cameFromFragment) {

        if (tablet == false) {
            mViewPager.setCurrentItem(position);
        }
        ((MapFragment) mSectionsPagerAdapter.getItem(2)).goToMapFragment(position, place, cameFromFragment);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private MapFragment frag = new MapFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new FavoritesFragment();
                case 1:
                    return new SearchFragment();
                case 2:
                    return frag; //new MapFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            if (tablet){
                return 2;
            }
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.favorits);
                case 1:
                    return getString(R.string.search);
                case 2:
                    return getString(R.string.map);
            }
            return null;
        }
    }


    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;


            if (isCharging) {
                Toast.makeText(getApplicationContext(), "YEEH...CHARGING...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "STOPPED CHARGING...", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
/*
        //  SectionsPagerAdapter for Tablets

    public class SectionsPagerAdapterTablet extends FragmentPagerAdapter {

        //private MapFragment frag = new MapFragment();

        public SectionsPagerAdapterTablet(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new FavoritesFragment();
                case 1:
                    return new SearchFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.favorits);
                case 1:
                    return getString(R.string.search);
            }
            return null;
        }
    }



}
*/
