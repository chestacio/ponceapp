package cl.grobic.ponceapp.ponceapp.Activities;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cl.grobic.ponceapp.ponceapp.R;

public class AjustesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }
}
