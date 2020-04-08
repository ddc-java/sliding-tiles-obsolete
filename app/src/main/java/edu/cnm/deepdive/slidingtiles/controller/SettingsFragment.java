package edu.cnm.deepdive.slidingtiles.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import edu.cnm.deepdive.slidingtiles.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class SettingsFragment extends PreferenceFragmentCompat implements Target {

  private SharedPreferences preferences;
  private ListPreference imagePref;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
    //noinspection ConstantConditions
    preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    setupImagePreferences();
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);
  }

  private void setupImagePreferences() {
    String imagePrefKey = getString(R.string.image_pref_key);
    imagePref = findPreference(imagePrefKey);
    List<String> availableImages =
        new ArrayList<>(preferences
            .getStringSet(getString(R.string.available_images_pref_key), new HashSet<>()));
    List<String> entries = new LinkedList<>();
    List<String> values = new LinkedList<>();
    Collections.sort(availableImages);
    Pattern imageSpecDelimiter = Pattern.compile(getString(R.string.image_spec_delimiter));
    @SuppressWarnings("ConstantConditions")
    File directory = getContext().getDir(getString(R.string.image_subdirectory), Context.MODE_PRIVATE);
    for (String imageSpec : availableImages) {
      String[] parts = imageSpecDelimiter.split(imageSpec);
      String displayName = parts[0];
      String fileName = parts[1];
      File file = new File(directory, fileName);
      if (file.exists()) {
        entries.add(displayName);
        values.add(fileName);
      }
    }
    entries.add(getString(R.string.browse_gallery_entry));
    values.add("");
    imagePref.setEntries(entries.toArray(new String[0]));
    imagePref.setEntryValues(values.toArray(new String[0]));
    setImagePrefIcon(directory,
        preferences.getString(imagePrefKey, getString(R.string.image_pref_default)));
    imagePref.setOnPreferenceChangeListener((preference, newValue) -> {
      if (!((String) newValue).isEmpty()) {
        setImagePrefIcon(directory, (String) newValue);
        return true;
      } else {
        return false;
      }
    });
  }

  private void setImagePrefIcon(File directory, String filename) {
    Picasso.get().load(new File(directory, filename)).into(this);
  }

  @Override
  public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
    imagePref.setIcon(new BitmapDrawable(getResources(), bitmap));
  }

  @Override
  public void onBitmapFailed(Exception e, Drawable errorDrawable) {
    imagePref.setIcon(android.R.drawable.ic_menu_gallery);
  }

  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {
  }

}
