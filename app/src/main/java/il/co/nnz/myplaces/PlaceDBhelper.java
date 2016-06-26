package il.co.nnz.myplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 08/06/2016.
 */
public class PlaceDBhelper extends SQLiteOpenHelper {

    public static final String SEARCH_TABLE_KEY="search_table";
    public static final String ID_KEY="_id";
    public static final String PLACE_ID_KEY="place_id";
    public static final String NAME_KEY="name";
    public static final String ADDRESS_KEY="address";
    public static final String LAT_KEY="lat";
    public static final String LNG_KEY="lng";
    public static final String IMAGE_KEY="image";
    public static final String ICON_KEY="icon";


    public static final String FAVORITES_TABLE_KEY="favorites_table";
    public static final String F_ID_KEY="_id";
    public static final String F_PLACE_ID_KEY="place_id";
    public static final String F_NAME_KEY="name";
    public static final String F_ADDRESS_KEY="address";
    public static final String F_LAT_KEY="lat";
    public static final String F_LNG_KEY="lng";
    public static final String F_IMAGE_KEY="image";
    public static final String F_ICON_KEY="icon";


    public PlaceDBhelper(Context context) {
        super(context, "placesDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL("CREATE TABLE "  + TABLE_KEY + " ( "+ID_KEY +" INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_KEY + " TEXT, "
          //      + ADDRESS_KEY + " TEXT, " + IMAGE_KEY + " TEXT )");


        String searchTable = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                SEARCH_TABLE_KEY, ID_KEY, PLACE_ID_KEY, NAME_KEY, ADDRESS_KEY, LAT_KEY, LNG_KEY, IMAGE_KEY, ICON_KEY);
        db.execSQL(searchTable);

        String favoritesTable = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                FAVORITES_TABLE_KEY, F_ID_KEY, F_PLACE_ID_KEY, F_NAME_KEY, F_ADDRESS_KEY, F_LAT_KEY, F_LNG_KEY, F_IMAGE_KEY,F_ICON_KEY);
        db.execSQL(favoritesTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertPlace (Place place)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLACE_ID_KEY, place.getPlaceID());
        values.put(NAME_KEY, place.getName());
        values.put(ADDRESS_KEY, place.getAddress());
        values.put(LAT_KEY, place.getLat());
        values.put(LNG_KEY, place.getLng());
        values.put(IMAGE_KEY, place.getImage());
        values.put(ICON_KEY, place.getIcon());
        db.insert(SEARCH_TABLE_KEY, null, values);
        db.close();
    }

    public ArrayList<Place> getAroundMePlaces() {
        ArrayList<Place> places = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " +SEARCH_TABLE_KEY, null);
        while (c.moveToNext())
        {
            long id = c.getLong(c.getColumnIndex(ID_KEY));
            String name = c.getString(c.getColumnIndex(NAME_KEY));
            String address = c.getString(c.getColumnIndex(ADDRESS_KEY));
            String lat = c.getString(c.getColumnIndex(LAT_KEY));
            String lng = c.getString(c.getColumnIndex(LNG_KEY));
            String icon = c.getString(c.getColumnIndex(ICON_KEY));
            Log.d("place-params-db",id+","+ name+","+address+","+lat+","+lng);
            Place temp = new Place(id, name, address, lat, lng, icon);
            places.add(temp);
        }
        db.close();
        return places;
    }

    public void deletePlacesAroundMeTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SEARCH_TABLE_KEY, null,null);
        db.close();
    }

    //------------------------
    // favorites table methots
    //------------------------

    public void addToFAvorite(Place onLongClickplace) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLACE_ID_KEY, onLongClickplace.getPlaceID());
        values.put(NAME_KEY, onLongClickplace.getName());
        values.put(ADDRESS_KEY, onLongClickplace.getAddress());
        values.put(LAT_KEY, onLongClickplace.getLat());
        values.put(LNG_KEY, onLongClickplace.getLng());
        values.put(IMAGE_KEY, onLongClickplace.getImage());
        db.insert(FAVORITES_TABLE_KEY, null, values);
        db.close();
    }

    public ArrayList<Place> getFavoritePlaces() {

        ArrayList<Place> places = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " +FAVORITES_TABLE_KEY, null);
        while (c.moveToNext())
        {
            long id = c.getLong(c.getColumnIndex(ID_KEY));
            String name = c.getString(c.getColumnIndex(NAME_KEY));
            String address = c.getString(c.getColumnIndex(ADDRESS_KEY));
            String lat = c.getString(c.getColumnIndex(LAT_KEY));
            String lng = c.getString(c.getColumnIndex(LNG_KEY));
            Place temp = new Place(id, name, address, lat, lng);
            places.add(temp);
        }
        db.close();
        return places;
    }

    public void deletePlace(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FAVORITES_TABLE_KEY, F_ID_KEY+"= "+itemId ,null);
        db.close();
    }

    public void deleteFavorites() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FAVORITES_TABLE_KEY, null,null);
        db.close();
    }
}
