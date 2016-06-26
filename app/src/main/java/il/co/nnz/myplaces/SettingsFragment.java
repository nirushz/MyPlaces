package il.co.nnz.myplaces;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by User on 09/06/2016.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    private EditTextPreference editTextPreference;

    //onCreate!, Not onCreateView. because therhe is not layout
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        editTextPreference = (EditTextPreference) getPreferenceManager().findPreference("edit_radius");
        editTextPreference.setOnPreferenceChangeListener(this);

        getPreferenceManager().findPreference("km_mile_list").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference.getKey().equals("edit_radius")){
            editTextPreference.setSummary("The search radius is:"+newValue+"m");
        }

        if (preference.getKey().equals("km_mile_list")){
            preference.setSummary("You picked"+ newValue);
        }

        return true;
    }

}
