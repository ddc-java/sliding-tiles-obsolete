package edu.cnm.deepdive.slidingtiles;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.slidingtiles.service.GoogleSignInService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class SlidingTilesApplication extends Application {

  private static final String DRAWABLE_RES_TYPE = "drawable";
  private static final int BUFFER_SIZE = 16_384;

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setContext(this);
    Stetho.initializeWithDefaults(this);
    Picasso.setSingletonInstance(new Picasso.Builder(this).build());
    saveBaseImages();
  }

  private void saveBaseImages() {
    Single.fromCallable(() -> {
      Resources res = getResources();
      Pattern imageDelimiter = Pattern.compile(getString(R.string.image_spec_delimiter));
      String imageSpecFormat = getString(R.string.image_spec_format);
      String[] imageSpecs = res.getStringArray(R.array.available_images_resources);
      List<String> images = new LinkedList<>();
      File directory = getDir(getString(R.string.image_subdirectory), MODE_PRIVATE);
      String filenameFormat = getString(R.string.image_filename_format);
      for (String imageSpec : imageSpecs) {
        String[] parts = imageDelimiter.split(imageSpec);
        String displayName = parts[0];
        String resourceName = parts[1];
        int resourceId = res.getIdentifier(resourceName, DRAWABLE_RES_TYPE, getPackageName());
        String filename = String.format(filenameFormat, resourceName);
        File file = new File(directory, filename);
        if (!file.exists()) {
          try (
              InputStream input = res.openRawResource(resourceId);
              OutputStream output = new FileOutputStream(file);
          ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int bytesRead = input.read(buffer); bytesRead >= 0;
                bytesRead = input.read(buffer)) {
              output.write(buffer);
            }
            output.flush();
          }
        }
        images.add(String.format(imageSpecFormat, displayName, filename));
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
