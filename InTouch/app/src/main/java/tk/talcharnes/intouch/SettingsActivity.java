package tk.talcharnes.intouch;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Tal on 2/12/2017.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new tk.talcharnes.intouch.SettingsFragment())
                .commit();
    }
}
