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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * {@code Puzzle} implements a simple sliding tile puzzle consisting of a square arrangement of
 * tiles. The puzzle implemented is the classic <a href="https://en.wikipedia.org/wiki/15_puzzle">15-puzzle</a>
 * (AKA 16-puzzle). (The {@link #Puzzle(int, Random)} constructor is not limited to 4 X 4 &ndash;
 * i.e. 15 tiles &ndash; but may be used to create a puzzle of any reasonable size.) The tiles,
 * encapsulated as {@link Tile} instances, are conceptually homogenous in size and shape &ndash;
 * that is, each tile occupies a region of the same size and shape in the puzzle frame, and has the
 * same movement possibilities.
 * <p/>
 * Methods are provided to support starting in a random (but solvable) configuration; returning to
 * the starting configuration at any time during play; attempting to move a specified tile (which
 * will fail if that tile is not in the same row or column as the open space), obtaining the number
 * of moves made since the start of play, and reading the current tile arrangement.
 *
 * @author Nicholas Bennett, Chris Hughes
 * @see <a href="https://en.wikipedia.org/wiki/15_puzzle">15-puzzle&mdash;Wikipedia</a>
 */
@SuppressWarnings("unused")
public class Puzzle {

  private final int size;
  private final Tile[][] tiles;
  private Tile[][] start; // May be null (e.g. in an instance used by a solver).
  private List<Move> moves;
  private Set<Integer> arrangements;
  private int hash;

  /**
   * Begin initializing the {@code Puzzle} by creating an array for the {@link Tile} objects.
   *
   * @param size height and width of puzzle.
   */
  protected Puzzle(int size) {
    this.size = size;
    tiles = new Tile[size][size];
    clearHistory();
  }

  /**
   * Initializes the {@code Puzzle} as a copy of the specified instance. Along with the current
   * state of {@code other}, its starting position will be copied.
   *
   * @param other instance from which this instance will be initialized.
   */
  public Puzzle(Puzzle other) {
    this(other, true);
  }

  /**
   * Initializes the {@code Puzzle} as a copy of the specified instance, for use in solution/hinting
   * algorithm implementations.
   *
   * @param other instance from which this instance will be initialized.
   */
  public Puzzle(Puzzle other, boolean copyStartingPosition) {
    this(other.size);
    if (copyStartingPosition) {
      start = new Tile[size][size];
      copy(other.start, start);
    }
    moves.addAll(other.moves);
    arrangements.addAll(other.arrangements);
    hash = other.hash;
  }

  /**
   * Initializes the {@code Puzzle} containing (size}<sup>2 </sup> - 1) tiles, with tile locations
   * scrambled using the source of randomness specified in {@code rng}.
   *
   * @param size size (height and width) of the square arrangement of tiles.
   * @param rng  source of randomness.
   */
  public Puzzle(int size, Random rng) {
    this(size);
    for (int i = 0; i < size * size - 1; i++) {
      tiles[i / size][i % size] = new Tile(i);
    }
    tiles[size - 1][size - 1] = null;
    scramble(rng);
  }

  @Override
  public boolean equals(Object obj) {
    boolean comparison = false;
    if (this == obj) {
      comparison = true;
    } else if (obj instanceof Puzzle) {
      Puzzle puzzle = (Puzzle) obj;
      comparison = puzzle.hash == hash && Arrays.deepEquals(tiles, puzzle.tiles);
    }
    return comparison;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  /**
   * Shuffles the tiles, ensuring that the resulting arrangement is solvable.
   *
   * @param rng source of randomness.
   */
  public void scramble(Random rng) {
    shuffle(rng);
    if (!isParityEven()) {
      swapTopRowPair();
    }
    start = new Tile[size][size];
    copy(tiles, start);
    clearHistory();
    hash();
  }

  /**
   * Returns the tile arrangements to its initial, random layout, and resets the count of sliding
   * moveCount to 0.
   */
  public void reset() {
    if (start != null) {
      copy(start, tiles);
      clearHistory();
      hash();
    }
  }

  /**
   * Determines whether the specified location contains a tile in the same row or column as the open
   * space, and if so, moves one or more tiles in order, so that the specified location becomes the
   * empty space.
   *
   * @param fromRow vertical coordinate of the tile to be moved.
   * @param fromCol horizontal coordinate of the tile to be moved.
   * @return moves (in one row or one column) resulting in moving the tile from the specified
   * location; {@code null} if such a move is not possible.
   */
  public List<Move> move(int fromRow, int fromCol) {
    if (tiles[fromRow][fromCol] != null) {
      for (int col = 0; col < size; col++) {
        if (tiles[fromRow][col] == null) {
          List<Move> moves = new LinkedList<>();
          int step = (int) Math.signum(fromCol - col);
          do {
            moves.add(move(fromRow, col + step, fromRow, col));
            col += step;
          } while (col != fromCol);
          return moves;
        }
      }
      for (int row = 0; row < size; row++) {
        if (tiles[row][fromCol] == null) {
          List<Move> moves = new LinkedList<>();
          int step = (int) Math.signum(fromRow - row);
          do {
            moves.add(move(row + step, fromCol, row, fromCol));
            row += step;
          } while (row != fromRow);
          return moves;
        }
      }
    }
    return null;
  }

  /**
   * Returns the size (height and width) of this {@code Puzzle} instance.
   */
  public int getSize() {
    return size;
  }

  /**
   * Returns the current arrangement of {@link Tile} instances. The value returned is safe, in the
   * sense that changes to the contents of the array returned have no affect on this instance's
   * tiles.
   *
   * @return current arrangement of tiles.
   */
  public Tile[][] getTiles() {
    Tile[][] copy = new Tile[size][size];
    copy(tiles, copy);
    return copy;
  }

  /**
   * Returns the number of single tile moves performed since the puzzle was initialized using the
   * {@link #Puzzle(int, Random)} constructor, or since the last invocation of either the {@link
   * #reset()} method or the {@link #scramble(Random)} method.
   *
   * @return number of tile moves.
   */
  public int getMoveCount() {
    return moves.size();
  }

  /**
   * Compares the current tile arrangement to the target ordered arrangement, returning {@code true}
   * if the current arrangement is in-order, and {@code false} otherwise.
   *
   * @return flag indicating the in-order status of the current tile arrangement.
   */
  public boolean isSolved() {
    for (int i = 0; i < size * size - 1; i++) {
      Tile tile = tiles[i / size][i % size];
      if (tile == null || tile.getNumber() != i) {
        return false;
      }
    }
    return true;
  }

  /**
   * Computes the hash code of the current arrangement.
   */
  protected void hash() {
    hash = Arrays.deepHashCode(tiles);
  }

  /**
   * Clears the history of tile moves and intermediate arrangements.
   */
  protected void clearHistory() {
    if (moves == null) {
      moves = new LinkedList<>();
    } else {
      moves.clear();
    }
    if (arrangements == null) {
      arrangements = new HashSet<>();
    } else {
      arrangements.clear();
    }
  }

  /**
   * Moves a single tile to an adjacent (empty) space, updating the hash code, history of moves, and
   * history of arrangements.
   *
   * @param fromRow starting row position of tile to move.
   * @param fromCol starting column position of tile to move.
   * @param toRow   ending row position of tile to move.
   * @param toCol   ending column position of tile to move.
   * @return {@link Move} instance encapsulating tile move.
   */
  protected Move move(int fromRow, int fromCol, int toRow, int toCol) {
    Move move = new Move(fromRow, fromCol, hash, toRow, toCol);
    arrangements.add(hash);
    swap(tiles, fromRow, fromCol, toRow, toCol);
    hash();
    moves.add(move);
    return move;
  }

  /**
   * Randomly shuffles all of the tiles, without regard to the solvability of the resulting
   * arrangement.
   *
   * @param rng source of randomness.
   */
  protected void shuffle(Random rng) {
    for (int toPosition = size * size - 1; toPosition >= 0; toPosition--) {
      int toRow = toPosition / size;
      int toCol = toPosition % size;
      int fromPosition = rng.nextInt(toPosition + 1);
      if (fromPosition != toPosition) {
        int fromRow = fromPosition / size;
        int fromCol = fromPosition % size;
        swap(tiles, fromRow, fromCol, toRow, toCol);
      }
    }
  }

  /**
   * Computes and returns a parity flag of the current arrangement. A solved (in-order) arrangement
   * has <em>even</em> parity; all solvable arrangements have even parity as well, while unsolvable
   * arrangements have odd parity. Thus, the current arrangement is solvable if and only if its
   * parity is even&mdash;that is, if this method returns {@code true}.
   *
   * @return parity flag, indicating even ({@code true}) or odd ({@code false}) parity.
   */
  protected boolean isParityEven() {
    int sum = 0;
    Tile[][] work = new Tile[size][size];
    copy(tiles, work);
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (tiles[row][col] == null) {
          sum += Math.abs(row - size + 1) + Math.abs(col - size + 1);
          break;
        }
      }
    }
    for (int fromRow = 0; fromRow < size; fromRow++) {
      for (int fromCol = 0; fromCol < size; fromCol++) {
        int fromPosition = fromRow * size + fromCol;
        int toPosition = (work[fromRow][fromCol] != null)
            ? work[fromRow][fromCol].getNumber() : size * size - 1;
        while (toPosition != fromPosition) {
          int toRow = toPosition / size;
          int toCol = toPosition % size;
          swap(work, fromRow, fromCol, toRow, toCol);
          sum++;
          toPosition = (work[fromRow][fromCol] != null)
              ? work[fromRow][fromCol].getNumber() : size * size - 1;
        }
      }
    }
    return (sum & 1) == 0;
  }

  /**
   * Swaps a pair of adjacent tiles in the top row. This has the effect of switch from odd to even
   * parity (or vice versa).
   */
  protected void swapTopRowPair() {
    for (int col = 0; col < size - 1; col++) {
      if (tiles[0][col] != null && tiles[0][col + 1] != null) {
        swap(tiles, 0, col, 0, col + 1);
        break;
      }
    }
  }

  /**
   * Swaps the pair of tiles in the specified locations.
   */
  protected void swap(Tile[][] tiles, int fromRow, int fromCol, int toRow, int toCol) {
    Tile temp = tiles[toRow][toCol];
    tiles[toRow][toCol] = tiles[fromRow][fromCol];
    tiles[fromRow][fromCol] = temp;
  }

  /**
   * Copies an entire {@link Tile Tile[][]} source arrangement to a destination {@link Tile
   * Tile[][]}.
   */
  protected static void copy(Tile[][] source, Tile[][] dest) {
    for (int row = 0; row < source.length; row++) {
      System.arraycopy(source[row], 0, dest[row], 0, source.length);
    }
  }

}
