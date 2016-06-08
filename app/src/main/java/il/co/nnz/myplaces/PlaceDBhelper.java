package il.co.nnz.myplaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 08/06/2016.
 */
public class PlaceDBhelper extends SQLiteOpenHelper {

    public static final String TABLE_KEY="places_table";
    public static final String ID_KEY="_id";
    public static final String NAME_KEY="name";
    public static final String ADDRESS_KEY="address";
    public static final String IMAGE_KEY="image";


    public PlaceDBhelper(Context context) {
        super(context, "placesDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "  + TABLE_KEY + " ( "+ID_KEY +" INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_KEY + " TEXT, "
                + ADDRESS_KEY + " TEXT, " + IMAGE_KEY + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
