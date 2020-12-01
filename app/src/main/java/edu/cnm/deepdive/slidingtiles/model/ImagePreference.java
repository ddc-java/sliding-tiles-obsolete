/*
 *  Copyright 2020 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.slidingtiles.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import edu.cnm.deepdive.slidingtiles.R;
import java.util.Objects;

@SuppressWarnings("unused")
public class ImagePreference extends DialogPreference implements Target {

  private String value;

  public ImagePreference(Context context, AttributeSet attrs,
      int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public ImagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public ImagePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ImagePreference(Context context) {
    super(context);
  }

  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getString(index);
  }

  @Override
  protected void onSetInitialValue(@Nullable Object defaultValue) {
    Context context = getContext();
    String imageSpec = getPersistedString((String) defaultValue);
    String[] parts = imageSpec.split(context.getString(R.string.image_spec_delimiter));
    if (Objects.equals(parts[1], context.getString(R.string.image_uri_tag))) {
      ContentResolver resolver = context.getContentResolver();
      Uri uri = Uri.parse(parts[2]);
      String title = null;
      String[] columns = {Media._ID};
      boolean found = false;
      try (Cursor cursor = resolver.query(uri, columns, null, null, null)) {
        if (cursor != null && cursor.moveToFirst()) {
          found = true;
          setValue(imageSpec);
        }
      }
      if (!found) {
        setValue((String) defaultValue);
      }
    } else {
      setValue(imageSpec);
    }
  }

  @Override
  public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
    setIcon(new BitmapDrawable(getContext().getResources(), bitmap));
  }

  @Override
  public void onBitmapFailed(Exception e, Drawable errorDrawable) {
    setIcon(R.drawable.ic_photo_library);
  }

  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    if (value != null && !value.isEmpty() && !value.equals(this.value)) {
      this.value = value;
      persistString(value);
      loadIcon(value);
    }
  }

  private void loadIcon(String imageSpec) {
    Context context = getContext();
    Picasso picasso = Picasso.get();
    String[] parts = imageSpec.split(context.getString(R.string.image_spec_delimiter));
    String protocol = parts[1];
    String identifier = parts[2];
    if (protocol.equals(context.getString(R.string.image_resource_tag))) {
      int id = context.getResources()
          .getIdentifier(identifier, "drawable", context.getPackageName());
      picasso.load(id).centerCrop().resize(200, 200).into(this);
    } else if (protocol.equals(context.getString(R.string.image_uri_tag))) {
      picasso.load(Uri.parse(identifier)).centerCrop().resize(200, 200).into(this);
    }
  }

  public interface OnRequestBrowseListener {

    void onRequestBrowse(String key);

  }

}
