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
package edu.cnm.deepdive.slidingtiles.model;

import java.util.Objects;

/**
 * Implements a simple tile in a sliding tile puzzle. Since, regardless of the view, each tile has a
 * fixed "home" position, it can be represented by a number that unambiguously corresponds to that
 * position. For example, in a 15-puzzle (AKA 16-puzzle), the tiles can be numbered from 1 to 15 (or
 * 0 to 14), starting at the upper-left and moving across each row, then moving down to each
 * successive row. This implementations assumes such a representation&mdash;or at least, that each
 * tile (and its home position) can be uniquely identified by an integer.
 */
public class Tile {

  private final int number;

  /**
   * Initialize the {@code Tile} with the stated numeric value.
   *
   * @param number value of the tile, corresponding to its home position.
   */
  protected Tile(int number) {
    this.number = number;
  }

  @Override
  public boolean equals(Object obj) {
    boolean comparison = false;
    if (this == obj) {
      comparison = true;
    } else if (obj instanceof Tile) {
      comparison = (number == ((Tile) obj).number);
    }
    return comparison;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number);
  }

  /**
   * Returns the numeric value (corresponding to the home position) of the tile.
   */
  public int getNumber() {
    return number;
  }

}
