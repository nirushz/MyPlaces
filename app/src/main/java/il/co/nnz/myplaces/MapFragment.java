package il.co.nnz.myplaces;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, SearchFragment.goToMapListener {

    private static GoogleMap myMap;
    ImageButton phoneBtn, websiteBtn;
    private PlaceDBhelper helper;
    String phone;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_map, container, false);
        phoneBtn = (ImageButton) v.findViewById(R.id.phoneBtn);
        websiteBtn = (ImageButton) v.findViewById(R.id.websiteBtn);

        helper = new PlaceDBhelper(getContext());

        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap =googleMap;
    }

    //show the place that was clicked clicked on map
    @Override
    public void goToMapFragment(int position, final Place place, int cameFromFragment) {

        myMap.clear();
        LatLng placeLocation = null;
        double lat =Double.parseDouble(place.getLat());
        double lng =Double.parseDouble(place.getLng());
        placeLocation = new LatLng(lat,lng) ;

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));
        myMap.addMarker(new MarkerOptions().position(placeLocation).title(place.getName()).alpha(0.6f));

        Log.d("placeID", String.valueOf(place.getId()));
        if (cameFromFragment==1){
        helper.getMoreDetails(place); }

      //  Log.d("placePhone", place.getPhone());
      //  Log.d("placeWebsite", place.getWebsite());

        if (place.getPhone()==null) {
            phoneBtn.setVisibility(View.INVISIBLE);
        }else {
            phoneBtn.setVisibility(View.VISIBLE);
        }

        if (place.getWebsite()==null) {
            websiteBtn.setVisibility(View.INVISIBLE);
        }else {
            websiteBtn.setVisibility(View.VISIBLE);
        }


        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent phoneCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.getPhone()));
                startActivity(phoneCall);
            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getWebsite()));
                startActivity(openWebsite);
            }
        });


    }


}
