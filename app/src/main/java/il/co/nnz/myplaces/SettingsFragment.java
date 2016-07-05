package il.co.nnz.myplaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by User on 09/06/2016.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private EditTextPreference editTextPreference;

    //onCreate!, Not onCreateView. because therhe is not layout
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        editTextPreference = (EditTextPreference) getPreferenceManager().findPreference("edit_radius");
        editTextPreference.setOnPreferenceChangeListener(this);

        getPreferenceManager().findPreference("km_mile_list").setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference("delete_favorites").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals("edit_radius")){
            editTextPreference.setSummary("The search radius is:"+newValue+"m");
        }

        if (preference.getKey().equals("km_mile_list")){
            preference.setSummary("You picked "+ newValue);
        }

        if (preference.getKey().equals("delete_favorites")){
            preference.setSummary("Favorites deleted");
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        deleteDialog.setMessage("Delete?");
        deleteDialog.setButton(deleteDialog.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            //deleteFavoritesListener.deleteAllFavorites();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("deleteFavorites", true);
                editor.commit();
                Log.d("delete favorites?", String.valueOf(sp.getBoolean("deleteFavorites", false)));

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
