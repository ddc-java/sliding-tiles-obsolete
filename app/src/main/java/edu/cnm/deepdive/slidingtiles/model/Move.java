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
