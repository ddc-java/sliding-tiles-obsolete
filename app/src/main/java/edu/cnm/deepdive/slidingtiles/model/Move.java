package edu.cnm.deepdive.slidingtiles.model;

/**
 * TODO Write Javadoc comment.
 */
public class Move {

  private final int fromRow;
  private final int fromCol;
  private final int fromHashCode;
  private final int toRow;
  private final int toCol;

  /**
   * TODO Write Javadoc comment.
   *
   * @param fromRow
   * @param fromCol
   * @param fromHashCode
   * @param toRow
   * @param toCol
   */
  protected Move(int fromRow, int fromCol, int fromHashCode,
      int toRow, int toCol) {
    this.fromRow = fromRow;
    this.fromCol = fromCol;
    this.fromHashCode = fromHashCode;
    this.toRow = toRow;
    this.toCol = toCol;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getFromRow() {
    return fromRow;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getFromCol() {
    return fromCol;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getFromHashCode() {
    return fromHashCode;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getToRow() {
    return toRow;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getToCol() {
    return toCol;
  }

}
