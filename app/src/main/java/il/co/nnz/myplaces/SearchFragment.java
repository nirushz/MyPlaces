package il.co.nnz.myplaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, LocationListener, SearchView.OnQueryTextListener {

    //interface 2> declare a variable of type OnNameListener (our interface)
    private goToMapListener mapListener;
    private goToFavoritesListener fragmentListener;

    final static String API_KEY ="AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w";
    private String myPlaceUrl;
    private double mylat;
    private double mylng;
    private int radius=500;
    private String name =null;

    private LocationManager locationManager;
    String provider;

    PlaceAdapter adapter;
    RecyclerView searchPlacesRecyclerView;
    PlaceDBhelper helper;

    Place onLongClickplace;

    public static final String ACTION_ADD_TO_FAVORITES = "il.co.nnz.myplaces.ACTION_ADD_TO_FAVORITES";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);


        /*
        ArrayList<Place> searchedPlaces = new ArrayList<>();
        Place p1 = new Place("4446656","home", "amoraim 7 Yavne", "32.1","34.8" );
        Place p2 = new Place("4446656","home", "amoraim 7 Yavne", "32.1","34.8" );
        searchedPlaces.add(p1);
        searchedPlaces.add(p2);
        */

        searchPlacesRecyclerView = (RecyclerView) v.findViewById(R.id.searchPlacesRecyclerView);
        searchPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        helper = new PlaceDBhelper(getContext());

        if (helper.getAroundMePlaces()!=null) {
            adapter = new PlaceAdapter(getContext(), helper.getAroundMePlaces());
            searchPlacesRecyclerView.setAdapter(adapter);
        }


        ((SearchView) v.findViewById(R.id.placeSearchView)).setIconifiedByDefault(false);
        ((SearchView) v.findViewById(R.id.placeSearchView)).setOnQueryTextListener(this);

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

        return v;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        name=query;

        Toast.makeText(getContext(), "name: "+name, Toast.LENGTH_SHORT).show();
        try {
            locationManager.requestLocationUpdates(provider, 1000, 5, this);
        }
        catch (SecurityException e){
            Toast.makeText(getContext(), "You do not have permission to get location", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) { return false; }


    @Override
    public void onClick(View v) {

        try {
            locationManager.requestLocationUpdates(provider, 1000, 5, this);
        }
        catch (SecurityException e){
            Toast.makeText(getContext(), "You do not have permission to get location", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        mylat = location.getLatitude();
        mylng = location.getLongitude();
        Log.d("myOwnPlace lat-mylng",mylat+","+mylng);

        //stop listening to location changes
        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {}


        if (name==null) {
            myPlaceUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mylat + "," + mylng + "&radius=" + radius + "&key=AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w";
        } else {
            myPlaceUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mylat + "," + mylng + "&radius=" + radius + "&name=" + name + "&key=AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w";
        }
        Log.d("myOwnPlace Url: ", myPlaceUrl);
        Intent in = new Intent(getContext(), SearchIntentServise.class);
        in.putExtra("myPlaceUrl", myPlaceUrl);
        getContext().startService(in);
        Toast.makeText(getContext(), getString(R.string.start_searching), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) {  }

    @Override
    public void onProviderDisabled(String provider) { }




    //BroadcastReceiver inner class
    public class SearchAroundMeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            adapter = new PlaceAdapter(getContext(),helper.getAroundMePlaces() );
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
        public PlaceAdapter(Context context, ArrayList<Place> places){
            this.places = places;
            this.context = context;
            //helper = new PlaceDBhelper(context);
        }

        // 10> what to do when creating a new ViewHolder for a new item
        @Override
        public PlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.place_item, parent, false);
            return new  MyViewHolder(v);
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
            private ImageView imagePlace;

            // 4> add ctor
            public MyViewHolder(View itemView) {
                super(itemView);
                textName = (TextView) itemView.findViewById(R.id.textName);
                textAddress = (TextView) itemView.findViewById(R.id.textAddress);
                textDistane = (TextView) itemView.findViewById(R.id.textDistance);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            // 7> create the binding method
            public void bindPlace (Place place){
                textName.setText(place.getName());
                textAddress.setText(place.getAddress());
                float[] result = new float[1];
                //mylng = LocationServices.FusedLocationApi.getLastLocation();
                //Location.distanceBetween(Double.parseDouble(place.getLat()), Double.parseDouble(place.getLng()),32.1460, 34.8510, result);
                Location.distanceBetween(32.1465, 34.8519, 32.1460, 34.8510, result);
                float distance = result[0];
                distance = distance/1000;
                distance = Float.parseFloat(new DecimalFormat("#.##").format(distance));
                String dis = String.valueOf(distance);
                textDistane.setText(dis+"Km");  //NEED TO FIX IT
            }

            @Override
            public void onClick(View v) {

                Place onClickPlace = places.get(getAdapterPosition());
                LatLng placeLocation = null;
                //String lat = onClickPlace.getLat();
                //Log.d("lat-string:", lat);
                double lat =Double.parseDouble(onClickPlace.getLat());
                double lng =Double.parseDouble(onClickPlace.getLng());
                placeLocation = new LatLng(lat,lng) ;

                //interface 4> run the method
                mapListener.goToMapFragment(2, onClickPlace);


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
        //getContext().getMenuInflater().inflate(R.menu.contex_menu, menu);
        menu.add(Menu.NONE, R.id.share, Menu.NONE, "share");
        menu.add(Menu.NONE, R.id.add_to_favorites, Menu.NONE, "add to favorites");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.share:

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
       void goToMapFragment (int position, Place place);

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

