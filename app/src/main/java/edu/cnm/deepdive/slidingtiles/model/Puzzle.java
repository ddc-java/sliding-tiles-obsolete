package edu.cnm.deepdive.slidingtiles.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * <code>Puzzle</code> implements a simple sliding tile puzzle consisting of a
 * square arrangement of tiles. The puzzle implemented is the classic <a
 * href="https://en.wikipedia.org/wiki/15_puzzle">15-puzzle</a> (AKA 16-puzzle).
 * (The {@link #Puzzle(int, Random)} constructor is not limited to 4 X 4 &ndash;
 * i.e. 15 tiles &ndash; but may be used to create a puzzle of any reasonable
 * size.) The tiles, encapsulated as {@link Tile} instances, are conceptually
 * homogenous in size and shape &ndash; that is, each tile occupies a region of
 * the same size and shape in the puzzle frame, and has the same movement
 * possibilities.
 * <p/>
 * Methods are provided to support starting in a random (but solvable)
 * configuration, returning to that starting configuration at any time during
 * play, attempting to move a specified tile (which may fail, if that tile is
 * not adjacent to the open space), obtaining the number of moves made since the
 * start of play, and reading the current tile arrangement.
 *
 * @author  Nicholas Bennett, Chris Hughes
 * @see     <a href="https://en.wikipedia.org/wiki/15_puzzle">15-puzzle &ndash;
 *          Wikipedia</a>
 */
public class Puzzle {

  private final int size;
  private final Tile[][] tiles;
  private Tile[][] start; // May be null (e.g. in an instance used by a solver).
  private List<Move> moves;
  private Set<Integer> arrangements;
  private int hash;

  /**
   * Begin initializing the <code>Puzzle</code> by creating an array for the
   * {@link Tile} objects.
   *
   * @param size height and width of puzzle.
   */
  protected Puzzle(int size) {
    this.size = size;
    tiles = new Tile[size][size];
    clearHistory();
  }

  /**
   * Initializes the <code>Puzzle</code> as a copy of the specified instance.
   * Along with the current state of <code>other</code>, its starting position
   * will be copied.
   *
   * @param other instance from which this instance will be initialized.
   */
  public Puzzle(Puzzle other) {
    this(other, true);
  }

  /**
   * Initializes the <code>Puzzle</code> as a copy of the specified instance, for
   * use in solution/hinting algorithm implementations.
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
   * Initializes the <code>Puzzle</code> containing <code>(size</code><sup>2
   * </sup><code> - 1)</code> tiles, with tile locations scrambled using the
   * source of randomness specified in <code>rng</code>.
   *
   * @param size  size (height and width) of the square arrangement of tiles.
   * @param rng   source of randomness.
   */
  public Puzzle(int size, Random rng) {
    this(size);
    for (int i = 0; i < size * size - 1; i++) {
      tiles[i / size][i % size] = new Tile(i);
    }
    tiles[size - 1][size - 1] = null;
    scramble(rng);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Puzzle) || obj.getClass() != getClass()) {
      return false;
    }
    Puzzle puzzle = (Puzzle) obj;
    if (puzzle.hash != hash) {
      return false;
    }
    return Arrays.deepEquals(tiles, puzzle.tiles);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  @Override
  public int hashCode() {
    return hash;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param rng
   */
  public void scramble(Random rng) {
    shuffle(rng);
    if (!isParityEven()) {
      swapRandomPair(rng);
    }
    start = new Tile[size][size];
    copy(tiles, start);
    clearHistory();
    hash();
  }

  /**
   * Returns the tile arrangements to its initial, random layout, and resets the
   * count of sliding moveCount to 0.
   */
  public void reset() {
    if (start != null) {
      copy(start, tiles);
      clearHistory();
      hash();
    }
  }

  /**
   * Determines whether the specified location contains a tile adjacent to the
   * open space, and if so, slides that tile into the space.
   *
   * @param fromRow   vertical coordinate of the tile to be moved.
   * @param fromCol   horizontal coordinate of the tile to be moved.
   * @return          instance if the move was successful; <code>null</code>
   *                  otherwise.
   */
  public Move move(int fromRow, int fromCol) {
    Move move = null;
    boolean canMove = true;
    int toRow = fromRow;
    int toCol = fromCol;
    if (canMove(fromRow, fromCol, fromRow - 1, fromCol)) {
      toRow--;
    } else if (canMove(fromRow, fromCol, fromRow, fromCol + 1)) {
      toCol++;
    } else if (canMove(fromRow, fromCol, fromRow + 1, fromCol)) {
      toRow++;
    } else if (canMove(fromRow, fromCol, fromRow, fromCol - 1)) {
      toCol--;
    } else {
      canMove = false;
    }
    if (canMove) {
      move = move(fromRow, fromCol, toRow, toCol);
    }
    return move;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @return
   */
  public int getSize() {
    return size;
  }

  /**
   * Returns the current arrangement of {@link Tile} instances. The value
   * returned is safe, in the sense that changes to the contents of the array
   * returned have no affect on this instance's tiles.
   *
   * @return  current arrangement of tiles.
   */
  public Tile[][] getTiles() {
    Tile[][] copy = new Tile[size][size];
    copy(tiles, copy);
    return copy;
  }

  /**
   * Returns the number of sliding moveCount performed since the puzzle was
   * initialized using the {@link #Puzzle(int, Random)} constructor, or since the
   * last invocation of either the {@link #reset()} method or the {@link
   * #scramble(Random)} method.
   *
   * @return    count of moveCount performed.
   */
  public int getMoveCount() {
    return moves.size();
  }

  /**
   * Compares the current tile arrangement to the target ordered arrangement,
   * returning <code>true</code> if the current arrangemen is in-order, and
   * <code>false</code> otherwise.
   *
   * @return    flag indicating the in-order status of the current tile
   *            arrangement.
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
   * TODO Write Javadoc comments.
   */
  protected void hash() {
    hash = Arrays.deepHashCode(tiles);
  }

  /**
   *
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
   * TODO Write Javadoc comment.
   *
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   * @return
   */
  protected boolean canMove(int fromRow, int fromCol, int toRow, int toCol) {
    return
        // Verify (fromRow, fromCol) in bounds.
        fromRow >= 0
        && fromRow < size
        && fromCol >= 0
        && fromCol < size
        // Verify (toRow, toCol) in bounds.
        && toRow >= 0
        && toRow < size
        && toCol >= 0
        && toCol < size
        // Verify a difference of 1 in rows or columns, but not both.
        && ~(-Math.abs(toRow - fromRow)) == (-Math.abs(toCol - fromCol))
        // Verify (fromRow, fromCol) occupied.
        && tiles[fromRow][fromCol] != null
        // Verify (toRow, toCol) unoccupied.
        && tiles[toRow][toCol] == null;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   * @return
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
   * TODO Write Javadoc comment.
   *
   * @param rng
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
   * TODO Write Javadoc comment.
   *
   * @return
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
   * TODO Write Javadoc comment.
   *
   * @param rng
   */
  protected void swapRandomPair(Random rng) {
    int fromPosition = rng.nextInt(size * size);
    while (tiles[fromPosition / size][fromPosition % size] == null) {
      fromPosition = rng.nextInt(size * size);
    }
    int fromRow = fromPosition / size;
    int fromCol = fromPosition % size;
    int toPosition = rng.nextInt(size * size);
    while (toPosition == fromPosition
        || tiles[toPosition / size][toPosition % size] == null) {
      toPosition = rng.nextInt(size * size);
    }
    int toRow = toPosition / size;
    int toCol = toPosition % size;
    swap(tiles, fromRow, fromCol, toRow, toCol);
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param tiles
   * @param fromRow
   * @param fromCol
   * @param toRow
   * @param toCol
   */
  protected void swap(Tile[][] tiles, int fromRow, int fromCol, int toRow, int toCol) {
    Tile temp = tiles[toRow][toCol];
    tiles[toRow][toCol] = tiles[fromRow][fromCol];
    tiles[fromRow][fromCol] = temp;
  }

  /**
   * TODO Write Javadoc comment.
   *
   * @param source
   * @param dest
   */
  protected static void copy(Tile[][] source, Tile[][] dest) {
    for (int row = 0; row < source.length; row++) {
      System.arraycopy(source[row], 0, dest[row], 0, source.length);
    }
  }

}
