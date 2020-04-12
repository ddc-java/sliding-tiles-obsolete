package edu.cnm.deepdive.slidingtiles.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * <code>Tile</code> implements a simple tile in a sliding tile puzzle. Since,
 * regardless of the view, each tile has a fixed "home" position, it can be
 * represented by a number that unambiguously corresponds to that position. For
 * example, in a 15-puzzle (AKA 16-puzzle), the tiles can be numbered from
 * 1 to 15 (or 0 to 14), starting at the upper-left and moving across each row,
 * then moving down to each successive row. This implementations assumes such
 * a representation &ndash; or at least, that each tile (and its home position)
 * can be uniquely identified by an integer.
 */
public class Tile {

  private final int number;

  /**
   * Initialize the <code>Tile</code> with the stated numeric value.
   *
   * @param number  value of the tile, corresponding to its home position.
   */
  protected Tile(int number) {
    this.number = number;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    return number == ((Tile) obj).number;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number);
  }

  /**
   * Returns the numeric value (corresponding to the home position) of the tile.
   *
   * @return  value of the tile, corresponding to its home position.
   */
  public int getNumber() {
    return number;
  }

}
