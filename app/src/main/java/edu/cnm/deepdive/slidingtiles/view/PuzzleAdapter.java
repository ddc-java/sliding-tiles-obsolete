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
package edu.cnm.deepdive.slidingtiles.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import edu.cnm.deepdive.slidingtiles.R;
import edu.cnm.deepdive.slidingtiles.model.Tile;
import java.util.Collection;

public class
PuzzleAdapter extends ArrayAdapter<Tile> {

  private static final String DIRECT_MOD_NOT_ALLOWED =
      "Direct modificaton of PuzzleAdapter contents not allowed.";

  private final int size;
  private final Tile[] tiles;
  private final boolean solved;
  private boolean overlayVisible;
  private final String overlayFormat;
  private final int puzzleBackground;
  private Bitmap[] tileImages;
  private Bitmap noTileImage;

  /**
   * TODO Write Javadoc comment.
   *
   * @param context
   * @param image
   */
  public PuzzleAdapter(@NonNull Context context, @NonNull Tile[][] source,
      @NonNull BitmapDrawable image, boolean solved, boolean overlayVisible) {
    super(context, R.layout.item_tile);
    size = source.length;
    this.solved = solved;
    this.overlayVisible = overlayVisible;
    tiles = new Tile[size * size];
    copyModelTiles(source);
    super.addAll(this.tiles);
    overlayFormat = context.getString(R.string.overlay_format);
    puzzleBackground = ContextCompat.getColor(context, R.color.puzzleBackground);
    sliceBitmap(image);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param position
   * @param convertView
   * @param parent
   * @return
   */
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tile, parent, false);
    } else {
      convertView.setTranslationX(0);
      convertView.setTranslationY(0);
    }
    Tile tile = getItem(position);
    TileView tileView = convertView.findViewById(R.id.tile_image);
    TextView overlay = convertView.findViewById(R.id.number_overlay);
    overlay.setVisibility(overlayVisible ? View.VISIBLE : View.GONE);
    overlay.setText(null);
    convertView.setZ(Float.MAX_VALUE);
    if (tile != null) {
      tileView.setSolved(solved);
      tileView.setImageBitmap(tileImages[tile.getNumber()]);
      if (overlayVisible) {
        overlay.setText(String.format(overlayFormat, tile.getNumber() + 1));
      }
    } else if (solved) {
      tileView.setSolved(true);
      tileView.setImageBitmap(tileImages[tileImages.length - 1]);
      if (overlayVisible) {
        overlay.setText(String.format(overlayFormat, tileImages.length));
      }
    } else {
      tileView.setImageBitmap(noTileImage);
      convertView.setZ(0);
    }
    return convertView;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param object
   */
  @Override
  public void add(@Nullable Tile object) {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param collection
   */
  @Override
  public void addAll(@NonNull Collection<? extends Tile> collection) {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param items
   */
  @Override
  public void addAll(Tile... items) {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param object
   * @param index
   */
  @Override
  public void insert(@Nullable Tile object, int index) {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param object
   */
  @Override
  public void remove(@Nullable Tile object) {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   */
  @Override
  public void clear() {
    throw new UnsupportedOperationException(DIRECT_MOD_NOT_ALLOWED);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public boolean isOverlayVisible() {
    return overlayVisible;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param overlayVisible
   */
  public void setOverlayVisible(boolean overlayVisible) {
    boolean notify = (this.overlayVisible != overlayVisible);
    this.overlayVisible = overlayVisible;
    if (notify) {
      notifyDataSetChanged();
    }
  }

  private void copyModelTiles(Tile[][] source) {
    for (int row = 0; row < size; row++) {
      System.arraycopy(source[row], 0, tiles, row * size, size);
    }
  }

  private void sliceBitmap(BitmapDrawable drawable) {
    Bitmap bitmap = drawable.getBitmap();
    tileImages = new Bitmap[tiles.length];
    int imageWidth = bitmap.getWidth();
    int imageHeight = bitmap.getHeight();
    int imageSize = Math.min(imageWidth, imageHeight);
    int horizontalOffset = (imageWidth - imageSize) / 2;
    int verticalOffset = (imageHeight - imageSize) / 2;
    float tileSize = (float) imageSize / size;
    int roundedTileSize = Math.round(tileSize);
    for (int i = 0; i < tileImages.length; i++) {
      int row = i / size;
      int col = i % size;
      int left =
          Math.min(horizontalOffset + Math.round(col * tileSize), imageWidth - roundedTileSize);
      int top =
          Math.min(verticalOffset + Math.round(row * tileSize), imageHeight - roundedTileSize);
      tileImages[i] = Bitmap.createBitmap(bitmap, left, top, roundedTileSize, roundedTileSize);
    }
    noTileImage = Bitmap.createBitmap(tileImages[tileImages.length - 1]);
    noTileImage.eraseColor(puzzleBackground);
  }

}
