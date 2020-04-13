package edu.cnm.deepdive.slidingtiles.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Move;
import edu.cnm.deepdive.slidingtiles.model.Puzzle;
import edu.cnm.deepdive.slidingtiles.model.Tile;
import edu.cnm.deepdive.slidingtiles.model.metric.InPlace;
import edu.cnm.deepdive.slidingtiles.model.metric.Measure;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class PlayViewModel extends AndroidViewModel
    implements LifecycleObserver, SharedPreferences.OnSharedPreferenceChangeListener {

  private static final long TIMER_INTERVAL = 250;

  private final MutableLiveData<Long> elapsedTime;
  private final MutableLiveData<Boolean> solved;
  private final MutableLiveData<Boolean> animateSlides;
  private final MutableLiveData<BitmapDrawable> image;
  private final MutableLiveData<String> title;
  private final MutableLiveData<Tile[][]> tiles;
  private final MutableLiveData<Integer> progress;
  private final MutableLiveData<Integer> moveCount;
  private final Measure measure;
  private final Random rng;
  private final String imagePrefKey;
  private final String sizePrefKey;
  private final String animationPrefKey;
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Set<ImageTarget> targets;
  private Puzzle puzzle;
  private int size;
  private long lastTick;
  private Timer timer;

  public PlayViewModel(@NonNull Application application) {
    super(application);
    elapsedTime = new MutableLiveData<>(0L);
    solved = new MutableLiveData<>(false);
    animateSlides = new MutableLiveData<>();
    image = new MutableLiveData<>();
    title = new MutableLiveData<>();
    tiles = new MutableLiveData<>();
    progress = new MutableLiveData<>();
    moveCount = new MutableLiveData<>();
    measure = new InPlace();
    rng = new Random();
    imagePrefKey = application.getString(R.string.image_pref_key);
    sizePrefKey = application.getString(R.string.size_pref_key);
    animationPrefKey = application.getString(R.string.animation_pref_key);
    targets = new HashSet<>();
    setupPreferences(application);
    createPuzzle();
  }

  public LiveData<Long> getElapsedTime() {
    return elapsedTime;
  }

  public LiveData<Boolean> getSolved() {
    return solved;
  }

  public LiveData<Boolean> getAnimateSlides() {
    return animateSlides;
  }

  public LiveData<BitmapDrawable> getImage() {
    return image;
  }

  public LiveData<String> getTitle() {
    return title;
  }

  public LiveData<Tile[][]> getTiles() {
    return tiles;
  }

  public LiveData<Integer> getProgress() {
    return progress;
  }

  public LiveData<Integer> getMoveCount() {
    return moveCount;
  }

  public Move move(int row, int col) {
    Move move = puzzle.move(row, col);
    if (move != null) {
      update();
    }
    return move;
  }

  public void createPuzzle() {
    puzzle = new Puzzle(size, rng);
    elapsedTime.setValue(0L);
    resumeTimer();
    update();
  }

  public void reset() {
    puzzle.reset();
    elapsedTime.setValue(0L);
    update();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    Context context = getApplication();
    Resources res = context.getResources();
    if (key.equals(imagePrefKey)) {
      loadImage(preferences
          .getString(imagePrefKey, context.getString(R.string.image_pref_default)));
    } else if (key.equals(sizePrefKey)) {
      size = preferences.getInt(sizePrefKey, res.getInteger(R.integer.size_pref_default));
    } else if (key.equals(animationPrefKey)) {
      animateSlides.postValue(
          preferences.getBoolean(animationPrefKey, res.getBoolean(R.bool.animation_pref_default)));
    }
  }

  private void setupPreferences(@NonNull Context context) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    preferences.registerOnSharedPreferenceChangeListener(this);
    onSharedPreferenceChanged(preferences, imagePrefKey);
    onSharedPreferenceChanged(preferences, sizePrefKey);
    onSharedPreferenceChanged(preferences, animationPrefKey);
  }

  private void update() {
    boolean solved = puzzle.isSolved();
    if (solved) {
      pauseTimer();
    }
    this.solved.setValue(solved);
    tiles.setValue(puzzle.getTiles());
    progress.setValue(measure.getMeasure(puzzle));
    moveCount.setValue(puzzle.getMoveCount());
  }

  @OnLifecycleEvent(Event.ON_PAUSE)
  private void pauseTimer() {
    if (this.timer != null) {
      Timer timer = this.timer;
      this.timer = null;
      timer.cancel();
    }
  }

  @OnLifecycleEvent(Event.ON_RESUME)
  private void resumeTimer() {
    if (!puzzle.isSolved()) {
      timer = new Timer();
      lastTick = System.currentTimeMillis();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          if (timer != null) {
            updateTimer();
          }
        }
      }, TIMER_INTERVAL, TIMER_INTERVAL);
    }
  }

  private void updateTimer() {
    long now = System.currentTimeMillis();
    long difference = now - lastTick;
    lastTick = now;
    //noinspection ConstantConditions
    elapsedTime.postValue(elapsedTime.getValue() + difference);
  }

  private void loadImage(String imageSpec) {
    Context context = getApplication();
    Picasso picasso = Picasso.get();
    String[] parts = imageSpec.split(context.getString(R.string.image_spec_delimiter));
    String title = parts[0];
    String protocol = parts[1];
    String identifier = parts[2];
    ImageTarget target = new ImageTarget(title);
    if (protocol.equals(context.getString(R.string.image_resource_tag))) {
      int id = context.getResources()
          .getIdentifier(identifier, "drawable", context.getPackageName());
      targets.add(target);
      picasso.load(id).centerCrop().resize(1200, 1200).into(target);
    } else if (protocol.equals(context.getString(R.string.image_uri_tag))) {
      targets.add(target);
      picasso.load(Uri.parse(identifier)).centerCrop().resize(1200, 1200).into(target);
    }
  }

  private class ImageTarget implements Target {

    private final String title;

    private ImageTarget(String title) {
      this.title = title;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
      targets.remove(this);
      image.postValue(new BitmapDrawable(getApplication().getResources(), bitmap));
      PlayViewModel.this.title.postValue(title);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
      targets.remove(this);
      loadImage(getApplication().getString(R.string.image_pref_default));
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }

  }

}
