package il.co.nnz.myplaces;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, LocationListener {

    //interface 2> declare a variable of type OnNameListener (our interface)
    public static goToMapListener mapListener;
    private goToFavoritesListener fragmentListener;

    final static String API_KEY = "AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w";
    private String myPlaceUrl;
    private double mylat;
    private double mylng;

    private String name = null;
    private boolean wasSearch=false;

    private LocationManager locationManager;
    String provider;

    PlaceAdapter adapter;
    RecyclerView searchPlacesRecyclerView;
    PlaceDBhelper helper;

    Place onLongClickplace;

    int radius;
    SharedPreferences sp;

    EditText editSearch;

    public static final String ACTION_ADD_TO_FAVORITES = "il.co.nnz.myplaces.ACTION_ADD_TO_FAVORITES";


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);


        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //radius = Integer.parseInt(sp.getString("edit_radius", "500"));

        searchPlacesRecyclerView = (RecyclerView) v.findViewById(R.id.searchPlacesRecyclerView);
        searchPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        helper = new PlaceDBhelper(getContext());

        if (helper.getAroundMePlaces() != null) {
            adapter = new PlaceAdapter(getContext(), helper.getAroundMePlaces());
            searchPlacesRecyclerView.setAdapter(adapter);
        }

        editSearch = (EditText) v.findViewById(R.id.searchEdit);


        v.findViewById(R.id.searchAroundBtn).setOnClickListener(this);


        //register the reciever
        // create the receiver object to read the broadcast
        SearchAroundMeReceiver receiver = new SearchAroundMeReceiver();
        // create IntentFilter ==> the connection to a specific action
        IntentFilter filter = new IntentFilter(SearchIntentServise.ACTION_SEARCH_AROUND_ME);
        // register the receiver ==> start listening
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);

        // 1> Get the location manager
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        // 2> Decide on the provider
        provider = locationManager.NETWORK_PROVIDER;

        try {
            locationManager.requestLocationUpdates(provider, 1000, 5, this);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "You do not have permission to get location", Toast.LENGTH_SHORT).show();
        }

        return v;
    }
/*
    @Override
    public boolean onQueryTextSubmit(String query) {

        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        name = query;
        wasSearch=true;

        try {
            locationManager.requestLocationUpdates(provider, 1000, 5, this);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "You do not have permission to get location", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

*/
    @Override
    public void onClick(View v) {

        name = editSearch.getText().toString();

        try {
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d ("edit search", name);
        wasSearch=true;

        try {
            locationManager.requestLocationUpdates(provider, 1000, 5, this);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "You do not have permission to get location", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        mylat = location.getLatitude();
        mylng = location.getLongitude();
        Log.d("myOwnPlace lat-mylng", mylat + "," + mylng);

        //stop listening to location changes
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
        }


        radius = Integer.parseInt(sp.getString("edit_radius", "500"));

        if (wasSearch) {

            if (name == null) {
                myPlaceUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mylat + "," + mylng + "&radius=" + radius + "&key=AIzaSyDOGTQd-WQUludiZ5P7wt8upx08R_2mjiU";
            } else {
                myPlaceUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mylat + "," + mylng + "&radius=" + radius + "&name=" + name + "&key=AIzaSyDOGTQd-WQUludiZ5P7wt8upx08R_2mjiU";
            }
            Log.d("myOwnPlace Url: ", myPlaceUrl);
            Intent in = new Intent(getContext(), SearchIntentServise.class);
            in.putExtra("myPlaceUrl", myPlaceUrl);
            getContext().startService(in);
            Toast.makeText(getContext(), getString(R.string.start_searching), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    //BroadcastReceiver inner class
    public class SearchAroundMeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            adapter = new PlaceAdapter(getContext(), helper.getAroundMePlaces());
            searchPlacesRecyclerView.setAdapter(adapter);
        }
    }


    // 1> add the RecyclerView library
    // 2> create the adapter class
    public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {

        // 8> define the structure to save the data
        ArrayList<Place> places;
        public PlaceDBhelper helper;
        private Context context;

        // 9> ctor that gets data
        public PlaceAdapter(Context context, ArrayList<Place> places) {
            this.places = places;
            this.context = context;
            //helper = new PlaceDBhelper(context);
        }

        // 10> what to do when creating a new ViewHolder for a new item
        @Override
        public PlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.place_item, parent, false);
            return new MyViewHolder(v);
        }

        // 11> what to do when binding data (Place) to an item
        @Override
        public void onBindViewHolder(PlaceAdapter.MyViewHolder holder, int position) {
            Place place = places.get(position);
            holder.bindPlace(place);
        }

        // 12> return how many items in the data
        @Override
        public int getItemCount() {
            return places.size();
        }

        // 3> create ViewHolder class
        // 5> create the item layout xml file
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            // 6> define the views in the holder
            private TextView textName, textAddress, textDistane;
            private ImageView categoryImage;

            // 4> add ctor
            public MyViewHolder(View itemView) {
                super(itemView);
                textName = (TextView) itemView.findViewById(R.id.textName);
                textAddress = (TextView) itemView.findViewById(R.id.textAddress);
                textDistane = (TextView) itemView.findViewById(R.id.textDistance);
                categoryImage = (ImageView) itemView.findViewById(R.id.categoryImage);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            // 7> create the binding method
            public void bindPlace(Place place) {
                textName.setText(place.getName());
                textAddress.setText(place.getAddress());
                Log.d("icon",place.getIcon() );
                Picasso.with(getContext()).load(place.getIcon()).into(categoryImage);

                float[] result = new float[1];

                if (mylat>0 && mylng>0) {
                    Location.distanceBetween(Double.parseDouble(place.getLat()), Double.parseDouble(place.getLng()), mylat, mylng, result);

                    //Location.distanceBetween(32.1465, 34.8519, 32.1460, 34.8510, result);
                    float distance = result[0];
                    distance = distance / 1000;
                    distance = Float.parseFloat(new DecimalFormat("#.##").format(distance));
                    String dis = String.valueOf(distance);
                    textDistane.setText(dis+"Km");  //NEED TO FIX IT
                } else {
                    textDistane.setText("Calculating distanse...");  //NEED TO FIX IT
                }

            }

            @Override
            public void onClick(View v) {

                Place onClickPlace = places.get(getAdapterPosition());

                //interface 4> run the method
                mapListener.goToMapFragment(2, onClickPlace, 1);

            }

            @Override
            public boolean onLongClick(View v) {
                onLongClickplace = places.get(getAdapterPosition());
                registerForContextMenu(v);
                return false;
            }

        }
        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.share, Menu.NONE, R.string.share);
        menu.add(Menu.NONE, R.id.navigate, Menu.NONE, "Navigate to place");
        menu.add(Menu.NONE, R.id.add_to_favorites, Menu.NONE, "Add to favorites");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, onLongClickplace.getName() + ",\n" + onLongClickplace.getAddress() + ",\n map: http://maps.google.com/maps?q=loc:"+Double.parseDouble(onLongClickplace.getLat())+","+Double.parseDouble(onLongClickplace.getLng()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;


            case R.id.navigate:
                String uri="geo:"+Double.parseDouble(onLongClickplace.getLat())+","+Double.parseDouble(onLongClickplace.getLng());
                Intent shareIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                if (shareIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
                break;


            case R.id.add_to_favorites:

                helper.addToFAvorite(onLongClickplace);
                //broadcast reciever announcing end of insertion to DB
                // onRecieve do clear adapter and set adapter
                Intent broadcastIntent = new Intent(ACTION_ADD_TO_FAVORITES);

                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(broadcastIntent);

                fragmentListener.goToFavoritesFragment(0);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //interface 1> create an interface with the method/s
    public interface goToMapListener{
       void goToMapFragment (int position, Place place, int cameFromFragment);

    }

    //interface 3> attach the interface variable to the host activity

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mapListener = (goToMapListener) context;
        fragmentListener = (goToFavoritesListener) context;
    }


    public interface goToFavoritesListener{
        void goToFavoritesFragment (int position);
    }

}

