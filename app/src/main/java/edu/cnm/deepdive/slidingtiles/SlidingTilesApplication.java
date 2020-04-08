package edu.cnm.deepdive.slidingtiles;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SlidingTilesApplication extends Application {

  private static final String IMAGE_PARTS_DELIMITER = ":";
  private static final String DRAWABLE_RES_TYPE = "drawable";
  private static final String FILENAME_FORMAT = "images/%s.png";
  private static final int BUFFER_SIZE = 16_384;

  @Override
  public void onCreate() {
    super.onCreate();
    saveBaseImages();
  }

  private void saveBaseImages() {
    Single.fromCallable(() -> {
      Resources res = getResources();
      String[] imageSpecs = res.getStringArray(R.array.puzzle_image_resources);
      List<String> images = new LinkedList<>();
      for (String imageSpec : imageSpecs) {
        String[] parts = imageSpec.split(IMAGE_PARTS_DELIMITER);
        String displayName = parts[0];
        String resourceName = parts[1];
        String fileName = String.format(FILENAME_FORMAT, resourceName);
        int resourceId = res.getIdentifier(resourceName, DRAWABLE_RES_TYPE, getPackageName());
        try (
            InputStream input = res.openRawResource(resourceId);
            OutputStream output = this.openFileOutput(fileName, 0);
        ) {
          byte[] buffer = new byte[BUFFER_SIZE];
          for (int bytesRead = input.read(buffer); bytesRead >= 0; bytesRead = input.read(buffer)) {
            output.write(buffer);
          }
          output.flush();
        }
      }
      return images;
    })
        .flatMapCompletable((images) -> Completable.fromAction(() -> {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          String availableImagesKey = getString(R.string.available_images_pref_key);
          Set<String> imagesPref = prefs.getStringSet(availableImagesKey, new HashSet<>());
          imagesPref.addAll(images);
          prefs.edit()
              .putStringSet(availableImagesKey, imagesPref)
              .apply();
        }))
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

}
