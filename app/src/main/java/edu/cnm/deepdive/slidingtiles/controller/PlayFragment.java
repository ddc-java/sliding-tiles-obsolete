/*
 *  Copyright 2020 Deep Dive Coding/CNM Ingenuity, Inc.
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
package edu.cnm.deepdive.slidingtiles.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Move;
import edu.cnm.deepdive.slidingtiles.model.Tile;
import edu.cnm.deepdive.slidingtiles.view.PuzzleAdapter;
import edu.cnm.deepdive.slidingtiles.viewmodel.PlayViewModel;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO Complete Javadocs
 */
public class PlayFragment extends Fragment
    implements AdapterView.OnItemClickListener {

  private Tile[][] tiles;
  private boolean solved;
  private int size;
  private boolean animateSlides;
  private BitmapDrawable image;
  private boolean tileSlideAnimationInProgress;
  private boolean gridFadeAnimationInProgress;
  private boolean paused;
  private int tileSlideDuration;
  private int gridFadeDuration;
  private String noMoveMessage;
  private String startMessage;
  private String solvedMessage;
  private String moveCounterFormat;
  private String shortTimerFormat;
  private String longTimerFormat;

  private TextView title;
  private ImageView imageUnderlay;
  private GridView tileGrid;
  private ProgressBar progressDisplay;
  private CheckBox showOverlay;
  private TextView moveCounter;
  private TextView puzzleTimer;
  private ProgressBar loadingIndicator;
  private TextView numTiles;
  private PuzzleAdapter adapter;
  private PlayViewModel viewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_play, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    readResources();
    bind(view);
    setupViewModel();
    wireControls();
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.play, menu);
  }

  @Override
  public void onPrepareOptionsMenu(@NonNull Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(R.id.play).setVisible(paused && !solved);
    menu.findItem(R.id.pause).setVisible(!paused && !solved);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.play:
        viewModel.setPaused(false);
        break;
      case R.id.pause:
        viewModel.setPaused(true);
        break;
      case R.id.reset:
        viewModel.resetPuzzle();
        break;
      case R.id.create:
        viewModel.createPuzzle();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    int row = position / size;
    int col = position % size;
    if (animateSlides) {
      tileSlideAnimationInProgress = true;
    }
    List<Move> moves = viewModel.move(row, col);
    if (moves != null) {
      if (animateSlides) {
        animateSlide(moves);
      }
    } else {
      tileSlideAnimationInProgress = false;
      Toast.makeText(getContext(), noMoveMessage, Toast.LENGTH_SHORT).show();
    }
  }

  private void readResources() {
    Resources res = getResources();
    tileSlideDuration = res.getInteger(R.integer.tile_slide_duration);
    gridFadeDuration = res.getInteger(R.integer.grid_fade_duration);
    noMoveMessage = getString(R.string.no_move_message);
    startMessage = getString(R.string.start_message);
    solvedMessage = getString(R.string.solved_message);
    moveCounterFormat = getString(R.string.move_counter_format);
    shortTimerFormat = getString(R.string.short_timer_format);
    longTimerFormat = getString(R.string.long_timer_format);
  }

  private void bind(View root) {
    loadingIndicator = root.findViewById(R.id.loading_indicator);
    title = root.findViewById(R.id.title);
    imageUnderlay = root.findViewById(R.id.image_underlay);
    tileGrid = root.findViewById(R.id.tile_grid);
    progressDisplay = root.findViewById(R.id.progress_display);
    showOverlay = root.findViewById(R.id.show_overlay);
    moveCounter = root.findViewById(R.id.move_counter);
    puzzleTimer = root.findViewById(R.id.puzzle_timer);
    numTiles = root.findViewById(R.id.num_tiles);
  }

  private void setupViewModel() {
    LifecycleOwner owner = getViewLifecycleOwner();
    //noinspection ConstantConditions
    viewModel = new ViewModelProvider(getActivity()).get(PlayViewModel.class);
    getLifecycle().addObserver(viewModel);
    viewModel.getImage().observe(owner, this::loadImage);
    viewModel.getSolved().observe(owner, this::setSolved);
    viewModel.getProgress().observe(owner, this::setProgress);
    viewModel.getMoveCount().observe(owner, this::setMoveCount);
    viewModel.getElapsedTime().observe(owner, this::updateTimer);
    viewModel.getTitle().observe(owner, title::setText);
    viewModel.getAnimateSlides().observe(owner, this::setAnimateSlides);
    viewModel.getPaused().observe(owner, this::setPaused);
  }

  private void wireControls() {
    showOverlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (adapter != null) {
        adapter.setOverlayVisible(isChecked);
      }
    });
  }

  private void animateSlide(List<Move> moves) {
    AnimatorSet animationSet = new AnimatorSet();
    List<Animator> animations = new LinkedList<>();
    tileGrid.setOnItemClickListener(null);
    for (Move move : moves) {
      int fromRow = move.getFromRow();
      int fromCol = move.getFromCol();
      int toRow = move.getToRow();
      int toCol = move.getToCol();
      View view = tileGrid.getChildAt(fromRow * size + fromCol);
      animations.add((toRow == fromRow)
          ? ObjectAnimator.ofFloat(view, "translationX", (toCol - fromCol) * view.getWidth())
          : ObjectAnimator.ofFloat(view, "translationY", (toRow - fromRow) * view.getHeight()));
    }
    animationSet.playTogether(animations);
    animationSet.setDuration(tileSlideDuration);
    animationSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationCancel(Animator animation) {
        tileSlideAnimationInProgress = false;
        loadPuzzle();
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        tileSlideAnimationInProgress = false;
        loadPuzzle();
      }
    });
    animationSet.start();
  }

  private void loadImage(BitmapDrawable image) {
    this.image = image;
    viewModel.getTiles().observe(getViewLifecycleOwner(), (tiles) -> {
      this.tiles = tiles;
      if (!tileSlideAnimationInProgress) {
        loadPuzzle();
      }
    });
  }

  private void setSolved(boolean solved) {
    if (!this.solved && solved) {
      showSolution();
    }
    this.solved = solved;
    checkTileGridDisplay();
    //noinspection ConstantConditions
    getActivity().invalidateOptionsMenu();
  }

  private void setProgress(int progress) {
    progressDisplay.setProgress(progress, true);
  }

  private void setMoveCount(int count) {
    moveCounter.setText(String.format(moveCounterFormat, count));
  }

  private void setAnimateSlides(boolean animateSlides) {
    this.animateSlides = animateSlides;
  }

  private void setPaused(boolean paused) {
    this.paused = paused;
    checkTileGridDisplay();
    //noinspection ConstantConditions
    getActivity().invalidateOptionsMenu();
  }

  private void updateTimer(Long elapsedTime) {
    long seconds = Math.round(elapsedTime / 1000D);
    long minutes = seconds / 60;
    long hours = minutes / 60;
    seconds %= 60;
    minutes %= 60;
    if (hours > 0) {
      puzzleTimer.setText(String.format(longTimerFormat, hours, minutes, seconds));
    } else {
      puzzleTimer.setText(String.format(shortTimerFormat, minutes, seconds));
    }
  }

  private void showSolution() {
    Toast.makeText(PlayFragment.this.getContext(), solvedMessage, Toast.LENGTH_LONG)
        .show();
    gridFadeAnimationInProgress = true;
    tileGrid.setAlpha(1);
    tileGrid.animate()
        .alpha(0)
        .setDuration(gridFadeDuration)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationCancel(Animator animation) {
            gridFadeAnimationInProgress = false;
            checkTileGridDisplay();
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            gridFadeAnimationInProgress = false;
            checkTileGridDisplay();
          }
        });
  }

  private void loadPuzzle() {
    if (tiles != null && image != null) {
      imageUnderlay.setImageDrawable(image);
      size = tiles.length;
      numTiles.setText(String.valueOf(size * size - 1));
      //noinspection ConstantConditions
      adapter = new PuzzleAdapter(getContext(), tiles, image, showOverlay.isChecked());
      tileGrid.setNumColumns(tiles.length);
      tileGrid.setAdapter(adapter);
      progressDisplay.setMax(size * size - 1);
      checkTileGridDisplay();
      loadingIndicator.setVisibility(View.GONE);
      if (paused) {
        Toast.makeText(getContext(), startMessage, Toast.LENGTH_LONG).show();
      }
    }
  }

  private void checkTileGridDisplay() {
    if (!gridFadeAnimationInProgress) {
      tileGrid.setOnItemClickListener(!solved ? this : null);
      if (!solved && !paused) {
        tileGrid.setAlpha(1);
        tileGrid.setVisibility(View.VISIBLE);
      } else {
        tileGrid.setAlpha(0);
        tileGrid.setVisibility(View.GONE);
      }
    }
  }

}
