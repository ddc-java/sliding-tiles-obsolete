package edu.cnm.deepdive.slidingtiles.controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Move;
import edu.cnm.deepdive.slidingtiles.model.Puzzle;
import edu.cnm.deepdive.slidingtiles.model.metric.InPlace;
import edu.cnm.deepdive.slidingtiles.model.metric.Measure;
import edu.cnm.deepdive.slidingtiles.view.PuzzleAdapter;
import java.util.Random;

public class PlayFragment extends Fragment
    implements AdapterView.OnItemClickListener, Animator.AnimatorListener {

  private static final int TILE_ANIMATION_DURATION = 125;
  private static final int PUZZLE_SIZE = 4;

  private Puzzle puzzle;
  private PuzzleAdapter adapter;
  private GridView tileGrid;
  private ProgressBar progressDisplay;
  private CheckBox showOverlay;
  private TextView moveCounter;
  private TextView puzzleTimer;
  private Button newPuzzle;
  private Button resetPuzzle;
  private Random rng;
  private Measure progress;
  private long elapsedTime;
  private boolean solved;
  private ProgressMonitor monitor;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_play, container, false);
    rng = new Random();
    setupGameControls(root);
    createPuzzle();
    return root;
  }

  @Override
  public void onStart() {
    super.onStart();
    checkProgress();
  }

  @Override
  public void onStop() {
    super.onStop();
    monitor = null;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    int row = position / PUZZLE_SIZE;
    int col = position % PUZZLE_SIZE;
    Move move = puzzle.move(row, col);
    if (move != null) {
      animate(view, move);
    } else {
      Toast.makeText(getContext(), R.string.no_move_message, Toast.LENGTH_SHORT).show();
    }
  }

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

  private void setupGameControls(View root) {
    tileGrid = root.findViewById(R.id.tile_grid);
    progressDisplay = root.findViewById(R.id.progress_display);
    showOverlay = root.findViewById(R.id.show_overlay);
    showOverlay.setOnCheckedChangeListener(
        (buttonView, isChecked) -> adapter.setOverlayVisible(isChecked));
    moveCounter = root.findViewById(R.id.move_counter);
    puzzleTimer = root.findViewById(R.id.puzzle_timer);
    newPuzzle = root.findViewById(R.id.new_puzzle);
    newPuzzle.setOnClickListener((v) -> createPuzzle());
    resetPuzzle = root.findViewById(R.id.reset_puzzle);
    resetPuzzle.setOnClickListener((v) -> {
      puzzle.reset();
      solved = false;
      elapsedTime = 0;
      monitor = null;
      checkProgress();
    });
  }

  private void createPuzzle() {
    puzzle = new Puzzle(PUZZLE_SIZE, rng);
    elapsedTime = 0;
    BitmapDrawable image =
        (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.android_robot_circle);
    progress = new InPlace();
    progressDisplay.setMax(puzzle.getSize() * puzzle.getSize() - 1);
    adapter = new PuzzleAdapter(getContext(), puzzle, image);
    adapter.setOverlayVisible(showOverlay.isChecked());
    tileGrid.setNumColumns(PUZZLE_SIZE);
    tileGrid.setAdapter(adapter);
    monitor = null;
    checkProgress();
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