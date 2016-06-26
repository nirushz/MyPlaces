package il.co.nnz.myplaces;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    FavoritesAdapter adapter;
    RecyclerView favoritesRecyclerView;
    PlaceDBhelper helper;

    Place onLongClickPlace;

    SharedPreferences sp;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorites, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        favoritesRecyclerView = (RecyclerView) v.findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        helper = new PlaceDBhelper(getContext());

        String deleteFavorites = (sp.getString("delete_favorites_values", "no")).toString();
        if (deleteFavorites.equals("yes")){
            helper.deleteFavorites();
            adapter = new FavoritesAdapter(getContext(), helper.getFavoritePlaces());
            favoritesRecyclerView.setAdapter(adapter);
        }

        if (helper.getFavoritePlaces()!=null) {
            adapter = new FavoritesAdapter(getContext(), helper.getFavoritePlaces());
            favoritesRecyclerView.setAdapter(adapter);
        }

        //register the reciever
        // create the receiver object to read the broadcast
        FavoritesReceiver receiver = new FavoritesReceiver();
        // create IntentFilter ==> the connection to a specific action
        IntentFilter filter = new IntentFilter(SearchFragment.ACTION_ADD_TO_FAVORITES);
        // register the receiver ==> start listening
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);

        return v;
    }

    // 1> add the RecyclerView library
    // 2> create the adapter class
    private class FavoritesAdapter extends RecyclerView.Adapter <FavoritesAdapter.FavoritesViewHolder> {

        // 8> define the structure to save the data
        ArrayList<Place> places;
        public PlaceDBhelper helper;
        private Context context;

        // 9> ctor that gets data
        public FavoritesAdapter(Context context, ArrayList<Place> places){
            this.places = places;
            this.context = context;
        }

        // 10> what to do when creating a new ViewHolder for a new item
        @Override
        public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.place_item, parent, false);
            return new  FavoritesViewHolder(v);
        }


        // 11> what to do when binding data (Place) to an item
        @Override
        public void onBindViewHolder(FavoritesViewHolder holder, int position) {
            Place place = places.get(position);
            holder.bindPlace(place);
        }

        // 12> return how many items in the data
        @Override
        public int getItemCount() {  return places.size();  }


        // 3> create ViewHolder class
        // 5> create the item layout xml file
        public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

            // 6> define the views in the holder
            private TextView textName, textAddress, textDistane;
            private ImageView imagePlace;
            //PlaceDBhelper helper;


            // 4> add ctor
            public FavoritesViewHolder(View itemView) {
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
                textDistane.setText("5Km");  //NEED TO FIX IT

            }

            @Override
            public void onClick(View v) {

                Place onClickPlace = places.get(getAdapterPosition());
                /*
                LatLng placeLocation = null;
                double lat =Double.parseDouble(onClickPlace.getLat());
                double lng =Double.parseDouble(onClickPlace.getLng());
                placeLocation = new LatLng(lat,lng) ;
                */

                SearchFragment.mapListener.goToMapFragment(2, onClickPlace);

            }

            @Override
            public boolean onLongClick(View v) {

                onLongClickPlace = places.get(getAdapterPosition());
                helper = new PlaceDBhelper(getContext());


                AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
                deleteDialog.setMessage("Delete "+onLongClickPlace.getName()+"?");
                deleteDialog.setButton(deleteDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long itemId = onLongClickPlace.getId();
                        helper.deletePlace(itemId);
                        adapter = new FavoritesAdapter(getContext(), helper.getFavoritePlaces());
                        favoritesRecyclerView.setAdapter(adapter);
                    }
                });
                deleteDialog.setButton(deleteDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteDialog.show();

                return false;
            }

        }

    }

    //BroadcastReceiver inner class
    private class FavoritesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter = new FavoritesAdapter(getContext(),helper.getFavoritePlaces() );
            favoritesRecyclerView.setAdapter(adapter);
        }
    }
}
