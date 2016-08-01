package my.santh.shopping.shoppinglist;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

/**
 *
 * SettingsListActivity class for Preference settings correspoding to lists includes sorting lists by price and alphabetically .
 * And can select preffered Currency
 */

public class SettingsListActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Display the fragment as the main content.
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsListFragment())
                    .commit();



        }

    /**
     * Fragement Activity class for setting preferences and generating toasts
     */
        public static class SettingsListFragment extends PreferenceFragment {

Context context;
            private SettingsListActivity me;
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.prefernceslist);

                ListPreference listPreference = (ListPreference) findPreference("Currency");
                listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Toast.makeText(getActivity(), " Selected Currency " + newValue.toString(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                CheckBoxPreference checkBoxPreference=(CheckBoxPreference)findPreference("Sort Lists");
                checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            Toast.makeText(getActivity(), "Sort lists alphabetically is enabled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Sort lists alphabetically is disabled",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                CheckBoxPreference checkBoxPreference1=(CheckBoxPreference)findPreference("Sort Price");
                checkBoxPreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.toString().equals("true")) {
                            Toast.makeText(getActivity(), "Sort lists by price is enabled",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Sort lists by price is disabled",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });



            }
        }

    /**
     *
     * onBackPressed Method to redirect to correct intent activity
     */

    public void onBackPressed() {
        Intent intent = getIntent();
        String strdata = intent.getExtras().getString("List");
        if (strdata.equals("main")) {
            Intent intentMain = new Intent(this, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentMain);

        } else if (strdata.equals("List")) {
            Intent intentMain = new Intent(this, ListActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int ListId = intent.getIntExtra("ListID", 0);
            String ListName = intent.getStringExtra("listname");

            intentMain.putExtra("id", ListId);
            intentMain.putExtra("listname", ListName);
            startActivity(intentMain);
        }

    }




    }


