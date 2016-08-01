package my.santh.shopping.shoppinglist;


import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

/**
 *
 * Preference Settings for Item .Includes Sorting Items alphabetically and by category and hiding purchased items.
 */
public class SettingsItemActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsItemFragment())
                .commit();
    }

    /**
     * Fragement Activity class for setting preferences and generating toasts
     */
    public static class SettingsItemFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferncesitem);


            CheckBoxPreference checkBoxPreference=(CheckBoxPreference)findPreference("Sort Items");
            CheckBoxPreference checkBoxPreference1=(CheckBoxPreference)findPreference("Sort Category");
            CheckBoxPreference checkBoxPreference2=(CheckBoxPreference)findPreference("Hide Items");
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(getActivity(), "Sort Items alphabetically is enabled",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Sort Items alphabetically is disabled",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            checkBoxPreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(getActivity(), "Sort Items by Category is enabled",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Sort Items by Category is disabled",
                                Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            checkBoxPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true")) {
                        Toast.makeText(getActivity(), "Hide Items  is enabled",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Hide Items is disabled",
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

    public  void onBackPressed()
    {
        Intent intent = getIntent();
        String strdata = intent.getExtras().getString("List");
        if (strdata.equals("main")) {
            Intent intentMain = new Intent(this, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentMain);

        }
        else if(strdata.equals("List"))
        {
            Intent intentMain = new Intent(this, ListActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int ListId=intent.getIntExtra("ListID", 0);
            String ListName=intent.getStringExtra("listname");

            intentMain.putExtra("id",ListId);
            intentMain.putExtra("listname", ListName);
            startActivity(intentMain);
        }
    }
}
