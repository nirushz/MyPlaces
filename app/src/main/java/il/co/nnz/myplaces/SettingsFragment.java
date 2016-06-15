package il.co.nnz.myplaces;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by User on 09/06/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    //onCreate!, Not onCreateView. because therhe is not layout
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
