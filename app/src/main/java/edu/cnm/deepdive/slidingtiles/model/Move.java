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

/**
 * Encapsulates a single tile move from one location to an adjacent location, including the hash
 * value of the pre-move arrangement of tiles. The latter value may be used by solver/hint features,
 * to recognize moves that result in a previously reached arrangement.
 */
public class Move {

  private final int fromRow;
  private final int fromCol;
  private final int fromHashCode;
  private final int toRow;
  private final int toCol;

  /**
   * Initializes this {@code Move} instance with the specified field values.
   *
   * @param fromRow tile's starting location row.
   * @param fromCol tile's starting location column.
   * @param fromHashCode pre-move arrangement hash value.
   * @param toRow tile's ending location row.
   * @param toCol tile's ending location column.
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
   * Returns the row of the tile's starting location.
   */
  public int getFromRow() {
    return fromRow;
  }

  /**
   * Returns the column of the tile's starting location.
   */
  public int getFromCol() {
    return fromCol;
  }

  /**
   * Returns the hash value of the pre-move arrangement of tiles.
   */
  public int getFromHashCode() {
    return fromHashCode;
  }

  /**
   * Returns the row of the tile's ending location.
   */
  public int getToRow() {
    return toRow;
  }

  /**
   * Returns the column of the tile's ending location.
   */
  public int getToCol() {
    return toCol;
  }

}
