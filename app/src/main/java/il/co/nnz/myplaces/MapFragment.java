package il.co.nnz.myplaces;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, SearchFragment.goToMapListener{

    private static GoogleMap myMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_map, container, false);

        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap =googleMap;
    }

    //show the place that was clicked clicked on map
    @Override
    public void goToMapFragment(int position, Place place) {

        myMap.clear();
        LatLng placeLocation = null;
        double lat =Double.parseDouble(place.getLat());
        double lng =Double.parseDouble(place.getLng());
        placeLocation = new LatLng(lat,lng) ;

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));
        myMap.addMarker(new MarkerOptions().position(placeLocation).title(place.getName()).alpha(0.6f));

    }

}
