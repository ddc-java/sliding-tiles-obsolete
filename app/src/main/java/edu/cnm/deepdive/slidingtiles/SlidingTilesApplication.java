package edu.cnm.deepdive.slidingtiles;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.slidingtiles.service.GoogleSignInService;

public class SlidingTilesApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setContext(this);
    Stetho.initializeWithDefaults(this);
    Picasso.setSingletonInstance(new Picasso.Builder(this).build());
  }

}
