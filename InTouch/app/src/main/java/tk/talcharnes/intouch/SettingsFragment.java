package tk.talcharnes.intouch;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tal on 2/12/2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
