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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import edu.cnm.deepdive.slidingtiles.R;

public class TileView extends AppCompatImageView {

  private Paint paint;
  private boolean solved;

  public TileView(Context context) {
    super(context);
    setWillNotDraw(false);
    setupPaint();
  }

  public TileView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setWillNotDraw(false);
    setupPaint();
  }

  public TileView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setWillNotDraw(false);
    setupPaint();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (!solved) {
      canvas.drawRect(0, 0, getRight(), getBottom(), paint);
    }
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }

  private void setupPaint() {
    paint = new Paint();
    paint.setColor(ContextCompat.getColor(getContext(), R.color.puzzleBackground));
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(getContext().getResources().getDimensionPixelSize(R.dimen.cell_spacing));
  }

}
