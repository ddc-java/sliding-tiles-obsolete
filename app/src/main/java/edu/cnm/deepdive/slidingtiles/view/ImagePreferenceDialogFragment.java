package edu.cnm.deepdive.slidingtiles.view;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceDialogFragmentCompat;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.ImagePreference;
import edu.cnm.deepdive.slidingtiles.model.ImagePreference.OnRequestBrowseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImagePreferenceDialogFragment extends PreferenceDialogFragmentCompat {

  private static final String KEY_KEY = "key";
  private static final String VALUE_KEY = "value";

  private ImagePreference preference;

  public static ImagePreferenceDialogFragment createInstance(String key, String value) {
    ImagePreferenceDialogFragment fragment = new ImagePreferenceDialogFragment();
    Bundle args = new Bundle();
    args.putString(KEY_KEY, key);
    args.putString(VALUE_KEY, value);
    fragment.setArguments(args);
    return fragment;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected void onPrepareDialogBuilder(Builder builder) {
    Context context = getContext();
    boolean externalAccess =
        (ContextCompat.checkSelfPermission(context, permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED);
    String value = getArguments().getString(VALUE_KEY);
    preference = (ImagePreference) getPreference();
    String[] resourceImages = context.getResources().getStringArray(R.array.resource_images);
    Arrays.sort(resourceImages);
    List<String> options = new ArrayList<>();
    Collections.addAll(options, resourceImages);
    if (externalAccess) {
      if (Arrays.binarySearch(resourceImages, value) < 0) {
        options.add(value);
      }
      options.add(getString(R.string.browse_gallery_spec));
    }
    builder
        .setTitle(preference.getTitle())
        .setAdapter(new GalleryAdapter(context, options, value), (dlg, which) -> {
          String imageSpec = options.get(which);
          String[] parts = imageSpec.split(getString(R.string.image_spec_delimiter));
          update((parts.length > 1) ? imageSpec : null);
        })
        .setPositiveButton(null, null);
  }

  @Override
  public void onDialogClosed(boolean positiveResult) {
  }

  private void update(@Nullable String imageSpec) {
    if (imageSpec != null) {
      preference.setValue(imageSpec);
    } else {
      if (getTargetFragment() instanceof OnRequestBrowseListener) {
        ((OnRequestBrowseListener) getTargetFragment())
            .onRequestBrowse(preference.getKey());
      } else if (getActivity() instanceof OnRequestBrowseListener) {
        ((OnRequestBrowseListener) getActivity())
            .onRequestBrowse(preference.getKey());
      }
    }
  }

}
