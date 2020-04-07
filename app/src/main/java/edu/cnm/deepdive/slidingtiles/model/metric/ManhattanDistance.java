package edu.cnm.deepdive.slidingtiles.model.metric;

import edu.cnm.deepdive.slidingtiles.model.Puzzle;
import edu.cnm.deepdive.slidingtiles.model.Tile;

/**
 * TODO Write Javadoc comment.
 *
 * @author Nicholas Bennett, Chris Hughes
 */
public class ManhattanDistance extends BaseMeasure {

  /**
   * TODO Write Javadoc comment.
   *
   * @param puzzle
   * @return
   */
  @Override
  public int getMeasure(Puzzle puzzle) {
    Tile[][] tiles = puzzle.getTiles();
    int size = puzzle.getSize();
    int measure = 0;
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        Tile tile = tiles[row][col];
        if (tile != null) {
          int tileNumber = tiles[row][col].getNumber();
          int homeRow = tileNumber / size;
          int homeCol = tileNumber % size;
          measure += Math.abs(row - homeRow) + Math.abs(col - homeCol);
        }
      }
    }
    return measure;
  }

}
