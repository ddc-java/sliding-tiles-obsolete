package edu.cnm.deepdive.slidingtiles.model.metric;

import edu.cnm.deepdive.slidingtiles.model.Puzzle;
import java.util.Comparator;

/**
 * TODO Write Javadoc comment.
 * @author Nicholas Bennett, Chris Hughes
 */
public interface Measure extends Comparator<Puzzle> {

  /**
   * TODO Write Javadoc comment.
   *
   * @param puzzle
   * @return
   */
  int getMeasure(Puzzle puzzle);

}
