package edu.cnm.deepdive.slidingtiles;

import android.app.Application;
import com.facebook.stetho.Stetho;
import edu.cnm.deepdive.slidingtiles.Service.GoogleSignInService;

public class SlidingTilesApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setContext(this);
    Stetho.initializeWithDefaults(this);
  }
}
