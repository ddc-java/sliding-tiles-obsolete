package edu.cnm.deepdive.slidingtiles.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.ImagePreference;
import edu.cnm.deepdive.slidingtiles.view.ImagePreferenceDialogFragment;
import edu.cnm.deepdive.slidingtiles.model.ImagePreference.OnRequestBrowseListener;

@SuppressWarnings("unused")
public class SettingsFragment extends PreferenceFragmentCompat
    implements OnRequestBrowseListener {

  private static final int PICK_EXTERNAL_GALLERY_CODE = 1001;
  private static final String KEY_KEY = "key";
  private static final String MIME_TYPE_FILTER = "image/*";

  private String key;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    if (preference instanceof ImagePreference) {
      String tag = ImagePreferenceDialogFragment.class.getName();
      if (getParentFragmentManager().findFragmentByTag(tag) == null) {
        ImagePreferenceDialogFragment fragment = ImagePreferenceDialogFragment.createInstance(
            preference.getKey(), ((ImagePreference) preference).getValue());
        fragment.setTargetFragment(this, 0);
        fragment.show(getParentFragmentManager(), tag);
      }
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
    if (requestCode == PICK_EXTERNAL_GALLERY_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        String imageSpec = encode(intent);
        ImagePreference pref = findPreference(key);
        pref.setValue(imageSpec);
        key = null;
      }
    } else {
      super.onActivityResult(requestCode, resultCode, intent);
    }
  }

  @Override
  public void onRequestBrowse(String key) {
    Intent intent = new Intent();
    intent.setType(MIME_TYPE_FILTER);
    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
    this.key = key;
    startActivityForResult(intent, PICK_EXTERNAL_GALLERY_CODE);
  }

  @SuppressWarnings("ConstantConditions")
  private String encode(Intent intent) {
    Uri uri = intent.getData();
    Context context = getContext();
    ContentResolver resolver = context.getContentResolver();
    String[] columns = {Media.DISPLAY_NAME, Media.TITLE};
    try (Cursor cursor = resolver.query(uri, columns, null, null, null)) {
      cursor.moveToFirst();
      String title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
      if (title == null) {
        title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
      }
      return getString(R.string.image_spec_format, title, getString(R.string.image_uri_tag), uri);
    }
  }

}
