package edu.cnm.deepdive.slidingtiles.model.metric;

import edu.cnm.deepdive.slidingtiles.model.Puzzle;

/**
 * TODO Write Javadoc comment.
 *
 * @author Nicholas Bennett, Chris Hughes
 */
public abstract class BaseMeasure implements Measure {

  /**
   * TODO Write Javadoc comment.
   *
   * @param p1
   * @param p2
   * @return
   */
  @Override
  public int compare(Puzzle p1, Puzzle p2) {
    return Integer.compare(getMeasure(p1), getMeasure(p2));
  }

}
