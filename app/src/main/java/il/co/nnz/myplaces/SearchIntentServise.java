package il.co.nnz.myplaces;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SearchIntentServise extends IntentService {

    private PlaceDBhelper helper = new PlaceDBhelper(this);
    public static final String ACTION_SEARCH_AROUND_ME = "il.co.nnz.myplaces.ACTION_SEARCH_AROUND_ME";

    public SearchIntentServise() {
        super("SearchIntentServise");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            URL url = new URL(intent.getStringExtra("myPlaceUrl"));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String result = "", line;

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                // result now has all the data as json! :)
                JSONObject placesNearMe = new JSONObject(result);
                Log.d("firstJSON: ", result);
                JSONArray placesNearMeArray = placesNearMe.getJSONArray("results");
                helper.deletePlacesAroundMeTable();

                for (int i=0; i<placesNearMeArray.length(); i++)
                {
                    JSONObject place = placesNearMeArray.getJSONObject(i);
                    String placeID = place.getString("place_id");
                    Log.d("place ID: ", placeID);

                    //get phone from here:
                    //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJs32SQXq6AhURLB2X0KBrt-Q&key=AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w

                    String name = place.getString("name");
                    Log.d("place name: ", name);
                    String address = place.getString("vicinity");
                    Log.d("place addrees", address);
                    String icon = place.getString("icon");
                    Log.d("place icon", icon);
                    //get the location lat&lng
                    JSONObject geometry = place.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    String lat = String.valueOf(location.getDouble("lat"));
                    String lng = String.valueOf(location.getDouble("lng"));
                    Log.d("lat-lng: ", lat + ","+lng);

                    //get the photo
                    if(place.has("photos")) {
                        JSONArray photos = place.getJSONArray("photos");
                        JSONObject photoObject = photos.getJSONObject(0);
                        String photoReference = photoObject.getString("photo_reference");
                        Log.d("photoReference: ", photoReference);
                    }

                    //taking out places of type "locality"
                    JSONArray types = place.getJSONArray("types");
                    String locality =types.getString(0);
                    Log.d("types: ", locality);


                    //insertion to DB, if not locality type
                    if (!locality.equals("locality")) {
                        Log.d("place-params", placeID + "," + name + "," + address + "," + lat + "," + lng+","+ icon);
                        Place temp = new Place(placeID, name, address, lat, lng, icon);
                        helper.insertPlace(temp);
                    }

                }

                //broadcast reciever announcing end of insertion to DB
                // in onRecieve do clear adapter and set adapter
                Intent broadcastIntent = new Intent(ACTION_SEARCH_AROUND_ME);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

            }




        } catch (MalformedURLException e) {
            Toast.makeText(SearchIntentServise.this, "MalformedURLException", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(SearchIntentServise.this, "IOException", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(SearchIntentServise.this, "JSONException", Toast.LENGTH_LONG).show();
        }
    }
}

