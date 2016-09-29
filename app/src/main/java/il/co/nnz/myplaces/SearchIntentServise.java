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

                    //***********************************************

                        URL thisPlaceUrl = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key=AIzaSyC-VJcttQOPCyqtGqck1MysH84Qe3Va37w");
                    Log.d("secondUrl", String.valueOf(thisPlaceUrl));
                        HttpURLConnection connection1 = (HttpURLConnection) thisPlaceUrl.openConnection();
                        InputStream stream1 = connection1.getInputStream();
                        InputStreamReader reader1 = new InputStreamReader(stream1);
                        BufferedReader bufferedReader1 = new BufferedReader(reader1);

                        String result1 = "", line1;

                        while ((line1 = bufferedReader1.readLine()) != null) {
                            result1 += line1;
                        }
                    Log.d("secondJSON: ", result1);

                        JSONObject thisPlace = new JSONObject(result1);
                        JSONObject thisPlaceResult =thisPlace.getJSONObject("result");

                        String phone=null, website=null;
                        if(thisPlaceResult.has("international_phone_number")) {
                            phone = thisPlaceResult.getString("international_phone_number");
                            Log.d("phone: ", phone);
                        }
                    if (thisPlaceResult.has("website")){
                        website = thisPlaceResult.getString("website");
                        Log.d ("website", website);
                    }


                    //***********************************************

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
                        Log.d("place-params", placeID + "," + name + "," + address + "," + lat + "," + lng+","+ icon+","+ phone+","+ website);
                        Place temp = new Place(placeID, name, address, lat, lng, icon, phone, website);
                        helper.insertPlace(temp);
                    }

                }

                //broadcast reciever announcing end of insertion to DB
                // in onRecieve do clear adapter and set adapter
                Intent broadcastIntent = new Intent(ACTION_SEARCH_AROUND_ME);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

            }




        } catch (MalformedURLException e) {
            Toast.makeText(SearchIntentServise.this, R.string.malformedurlexception, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(SearchIntentServise.this, R.string.ioexception, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), R.string.jsonexception , Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

