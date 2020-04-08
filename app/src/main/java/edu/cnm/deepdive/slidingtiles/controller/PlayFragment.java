package edu.cnm.deepdive.slidingtiles.controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Move;
import edu.cnm.deepdive.slidingtiles.model.Puzzle;
import edu.cnm.deepdive.slidingtiles.model.metric.InPlace;
import edu.cnm.deepdive.slidingtiles.model.metric.Measure;
import edu.cnm.deepdive.slidingtiles.view.PuzzleAdapter;
import java.io.File;
import java.util.Random;

/**
 * TODO Complete Javadocs
 */
@SuppressWarnings("unused")
public class PlayFragment extends Fragment
    implements AdapterView.OnItemClickListener, Animator.AnimatorListener, Target {

  private static final int TILE_ANIMATION_DURATION = 125;

  //region Puzzle state (candidates for viewmodel)
  private Puzzle puzzle;
  private Random rng = new Random();
  private Measure progress;
  private long elapsedTime;
  private boolean solved;
  private ProgressMonitor monitor;
  private int puzzleSize;
  private String imageSpec;
  private boolean animateSlides;
  private BitmapDrawable image;
  //endregion

  //region UI view references
  private GridView tileGrid;
  private ProgressBar progressDisplay;
  private CheckBox showOverlay;
  private TextView moveCounter;
  private TextView puzzleTimer;
  private ProgressBar loadingIndicator;
  private PuzzleAdapter adapter;
  //endregion

  //region Fragment lifecycle methods
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_play, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    readPreferences();
    setupGameControls(view);
    createPuzzle();
  }

  @Override
  public void onStart() {
    super.onStart();
    if (adapter != null) {
      checkProgress();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    monitor = null;
  }
  //endregion

  //region AdapterView.OnClickListener implementation
  @Override
  public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    int row = position / puzzleSize;
    int col = position % puzzleSize;
    Move move = puzzle.move(row, col);
    if (move != null) {
      if (animateSlides) {
        animate(view, move);
      } else {
        checkProgress();
      }
    } else {
      Toast.makeText(getContext(), R.string.no_move_message, Toast.LENGTH_SHORT).show();
    }
  }
  //endregion

  //region Animator.AnimatorListener implementation
  @Override
  public void onAnimationStart(Animator animator) {
  }

  @Override
  public void onAnimationEnd(Animator animator) {
    tileGrid.setOnItemClickListener(this);
    checkProgress();
  }

  @Override
  public void onAnimationCancel(Animator animator) {
    tileGrid.setOnItemClickListener(this);
  }

  @Override
  public void onAnimationRepeat(Animator animator) {
  }
  //endregion

  //region Target (Picasso callback) implementation
  @Override
  public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
    image = new BitmapDrawable(getResources(), bitmap);
    finalizePuzzle();
  }

  @Override
  public void onBitmapFailed(Exception e, Drawable errorDrawable) {
    loadImage(getString(R.string.image_pref_default));
  }

  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {
  }
  //endregion

  private void readPreferences() {
    @SuppressWarnings("ConstantConditions")
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    Resources res = getResources();
    String imagePrefKey = getString(R.string.image_pref_key);
    String sizePrefKey = getString(R.string.size_pref_key);
    String animationPrefKey = getString(R.string.animation_pref_key);
    imageSpec = preferences.getString(imagePrefKey, getString(R.string.image_pref_default));
    Log.d(getClass().getName(), imageSpec);
    puzzleSize = preferences.getInt(sizePrefKey, res.getInteger(R.integer.size_pref_default));
    animateSlides =
        preferences.getBoolean(animationPrefKey, res.getBoolean(R.bool.animation_pref_default));
  }

  private void setupGameControls(View root) {
    loadingIndicator = root.findViewById(R.id.loading_indicator);
    loadingIndicator.setVisibility(View.VISIBLE);
    tileGrid = root.findViewById(R.id.tile_grid);
    progressDisplay = root.findViewById(R.id.progress_display);
    showOverlay = root.findViewById(R.id.show_overlay);
    showOverlay.setOnCheckedChangeListener(
        (buttonView, isChecked) -> adapter.setOverlayVisible(isChecked));
    moveCounter = root.findViewById(R.id.move_counter);
    puzzleTimer = root.findViewById(R.id.puzzle_timer);
    root.findViewById(R.id.new_puzzle).setOnClickListener((v) -> createPuzzle());
    root.findViewById(R.id.reset_puzzle).setOnClickListener((v) -> {
      puzzle.reset();
      solved = false;
      elapsedTime = 0;
      monitor = null;
      checkProgress();
    });
  }

  private void createPuzzle() {
    Context context = getContext();
    Resources res = getResources();
    puzzle = new Puzzle(puzzleSize, rng);
    elapsedTime = 0;
    loadImage(imageSpec);
  }

  private void loadImage(String imageSpec) {
    Context context = getContext();
    Picasso picasso = Picasso.get();
    String[] parts = imageSpec.split(context.getString(R.string.image_spec_delimiter));
    String protocol = parts[1];
    String identifier = parts[2];
    if (protocol.equals(context.getString(R.string.image_resource_tag))) {
      int id = context.getResources()
          .getIdentifier(identifier, "drawable", context.getPackageName());
      picasso.load(id).centerCrop().resize(1200, 1200).into(this);
    } else if (protocol.equals(context.getString(R.string.image_uri_tag))) {
      Picasso.get().load(Uri.parse(identifier)).centerCrop().resize(1200, 1200).into(this);
    }
  }

  private void finalizePuzzle() {
    //noinspection ConstantConditions
    adapter = new PuzzleAdapter(getContext(), puzzle, image);
    adapter.setOverlayVisible(showOverlay.isChecked());
    tileGrid.setNumColumns(puzzleSize);
    tileGrid.setAdapter(adapter);
    progress = new InPlace();
    progressDisplay.setMax(puzzleSize * puzzleSize - 1);
    monitor = null;
    checkProgress();
    loadingIndicator.setVisibility(View.GONE);
  }

  private void checkProgress() {
    if (!solved && puzzle.isSolved()) {
      Toast.makeText(getContext(), R.string.solved_message, Toast.LENGTH_SHORT).show();
    }
    solved = puzzle.isSolved();
    if (!solved) {
      if (monitor == null) {
        monitor = new ProgressMonitor();
        monitor.start();
      }
      tileGrid.setOnItemClickListener(this);
    } else {
      tileGrid.setOnItemClickListener(null);
      monitor = null;
      updateTime();
    }
    adapter.notifyDataSetChanged();
    moveCounter.setText(getString(R.string.move_counter, puzzle.getMoveCount()));
    progressDisplay.setProgress(progress.getMeasure(puzzle));
  }

  private void updateTime() {
    long seconds = Math.round(elapsedTime / 1000d);
    long minutes = seconds / 60;
    seconds %= 60;
    if (monitor != null) {
      puzzleTimer.setText(getString(R.string.puzzle_timer, minutes, seconds));
    }
  }

  private void animate(View view, Move move) {
    int fromRow = move.getFromRow();
    int fromCol = move.getFromCol();
    int toRow = move.getToRow();
    int toCol = move.getToCol();
    tileGrid.setOnItemClickListener(null);
    ObjectAnimator animation = (toRow == fromRow)
        ? ObjectAnimator.ofFloat(view, "translationX", (toCol - fromCol) * view.getWidth())
        : ObjectAnimator.ofFloat(view, "translationY", (toRow - fromRow) * view.getHeight());
    animation.setDuration(TILE_ANIMATION_DURATION);
    animation.addListener(this);
    animation.start();
  }

  private class ProgressMonitor extends Thread {

    @Override
    public void run() {
      long timeStart = System.currentTimeMillis();
      long elapsedStart = elapsedTime;
      while (!solved && this == monitor) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // Do nothing
        }
        long timeNow = System.currentTimeMillis();
        elapsedTime = elapsedStart + (timeNow - timeStart);
        Activity activity = getActivity();
        if (activity != null) {
          activity.runOnUiThread(PlayFragment.this::updateTime);
        }
      }
    }

  }

}