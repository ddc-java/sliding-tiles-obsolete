package edu.cnm.deepdive.slidingtiles.controller;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.slidingtiles.R;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
  }

  public static class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences preferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.settings, rootKey);
      preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
      ListPreference imagePref = findPreference(getString(R.string.image_pref_key));
      Set<String> availableImages =
          preferences.getStringSet(getString(R.string.available_images_pref_key), new HashSet<>());
      List<String> entries = new LinkedList<>();
      List<String> values = new LinkedList<>();
      imagePref.setEntries(getResources().getStringArray(R.array.puzzle_image_names));
      imagePref.setEntryValues(getResources().getStringArray(R.array.puzzle_image_names));
    }

  }

}
