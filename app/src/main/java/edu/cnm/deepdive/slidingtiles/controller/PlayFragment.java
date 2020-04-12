package edu.cnm.deepdive.slidingtiles.controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Move;
import edu.cnm.deepdive.slidingtiles.model.Tile;
import edu.cnm.deepdive.slidingtiles.view.PuzzleAdapter;
import edu.cnm.deepdive.slidingtiles.viewmodel.PlayViewModel;

/**
 * TODO Complete Javadocs
 */
public class PlayFragment extends Fragment
    implements AdapterView.OnItemClickListener, Animator.AnimatorListener {

  private static final int TILE_ANIMATION_DURATION = 200;

  private Tile[][] tiles;
  private boolean solved;
  private int size;
  private boolean animateSlides;
  private BitmapDrawable image;

  //region UI view references
  private TextView title;
  private GridView tileGrid;
  private ProgressBar progressDisplay;
  private CheckBox showOverlay;
  private TextView moveCounter;
  private TextView puzzleTimer;
  private ProgressBar loadingIndicator;
  private PuzzleAdapter adapter;
  private PlayViewModel viewModel;
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
    setupViewModel();
    setupGameControls(view);
  }
  //endregion

  //region AdapterView.OnClickListener implementation
  @Override
  public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    int row = position / size;
    int col = position % size;
    Move move = viewModel.move(row, col);
    if (move != null) {
      if (animateSlides) {
//        animate(view, move); //TODO Fix animation.
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
  }

  @Override
  public void onAnimationCancel(Animator animator) {
    tileGrid.setOnItemClickListener(this);
  }

  @Override
  public void onAnimationRepeat(Animator animator) {
  }
  //endregion

  private void setupViewModel() {
    //noinspection ConstantConditions
    viewModel = new ViewModelProvider(getActivity()).get(PlayViewModel.class);
    getLifecycle().addObserver(viewModel);
    viewModel.getTiles().observe(getViewLifecycleOwner(), (tiles) -> {
      this.tiles = tiles;
      loadPuzzle();
    });
    viewModel.getSolved().observe(getViewLifecycleOwner(), (solved) -> {
      if (!this.solved && solved) {
        Toast.makeText(getContext(), R.string.solved_message, Toast.LENGTH_LONG).show();
      }
      if (adapter != null) {
        adapter.setSolved(solved);
      }
      tileGrid.setOnItemClickListener(!solved ? this : null);
      this.solved = solved;
    });
    viewModel.getElapsedTime().observe(getViewLifecycleOwner(), (elapsedTime) -> {
      long seconds = Math.round(elapsedTime / 1000D);
      long minutes = seconds / 60;
      seconds %= 60;
      puzzleTimer.setText(getString(R.string.puzzle_timer, minutes, seconds));
    });
    viewModel.getImage().observe(getViewLifecycleOwner(), (image) -> {
      this.image = image;
      loadPuzzle();
    });
    viewModel.getAnimateSlides().observe(getViewLifecycleOwner(), (animateSlides) -> {
      //TODO Finish this?
    });
    viewModel.getProgress().observe(getViewLifecycleOwner(), (progress) -> {
      progressDisplay.setProgress(progress);
    });
    viewModel.getTitle().observe(getViewLifecycleOwner(), (title) -> {
      this.title.setText(title);
    });
    viewModel.getMoveCount().observe(getViewLifecycleOwner(), (moveCount) -> {
      this.moveCounter.setText(getString(R.string.move_counter, moveCount));
    });
  }

  private void setupGameControls(View root) {
    loadingIndicator = root.findViewById(R.id.loading_indicator);
    loadingIndicator.setVisibility(View.VISIBLE);
    title = root.findViewById(R.id.title);
    tileGrid = root.findViewById(R.id.tile_grid);
    progressDisplay = root.findViewById(R.id.progress_display);
    showOverlay = root.findViewById(R.id.show_overlay);
    showOverlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (adapter != null) {
        adapter.setOverlayVisible(isChecked);
      }
    });
    moveCounter = root.findViewById(R.id.move_counter);
    puzzleTimer = root.findViewById(R.id.puzzle_timer);
    root.findViewById(R.id.new_puzzle).setOnClickListener((v) -> viewModel.createPuzzle());
    root.findViewById(R.id.reset_puzzle).setOnClickListener((v) -> viewModel.reset());
  }

  private void loadPuzzle() {
    if (tiles != null && image != null) {
      size = tiles.length;
      //noinspection ConstantConditions
      adapter = new PuzzleAdapter(getContext(), tiles, image);
      adapter.setOverlayVisible(showOverlay.isChecked());
      adapter.setSolved(solved);
      tileGrid.setNumColumns(tiles.length);
      tileGrid.setAdapter(adapter);
      tileGrid.setOnItemClickListener(!solved ? this : null);
      progressDisplay.setMax(size * size - 1);
      loadingIndicator.setVisibility(View.GONE);
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

}
