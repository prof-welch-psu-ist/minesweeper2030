package edu.psu.ist;

import edu.psu.ist.TileType.Mine;
import edu.psu.ist.TileType.Hidden;
import edu.psu.ist.immutableadts.Pair;
import io.vavr.collection.Vector;

import static edu.psu.ist.TileType.*;

/**
 * A class that encapsulates a single game of minesweeper.
 * Class invariant: the rows and cols passed to each public
 * method of this class assume 0-based indexing.
 */
public final class MinesweeperGame {

    /**
     * The internal representation of the board on which the
     * game is played.
     */
    private SquareBoard board;

    /** The dimension for our square game board. */
    private final int dimension;

    /**
     * Initializes the game with a user-specified {@code startingBoard}.
     *
     * @throws IllegalArgumentException if the {@code startingBoard} is
     *                                  malformed.
     */
    public MinesweeperGame(SquareBoard startingBoard) {
        board = startingBoard;
        dimension = startingBoard.dimension();
    }

    /**
     * Default constructor just initializes the game to use the
     * board from steps 1-4.
     */
    public MinesweeperGame() {
        board = new SquareBoard.ValidatingBoardBuilder()
                .row(hidden(), hidden(), mine(), hidden())
                .row(hidden(), hidden(), mine(), hidden())
                .row(hidden(), hidden(), hidden(), hidden())
                .row(hidden(), hidden(), hidden(), hidden())
                .build().get();
        this.dimension = 4;
    }

    /** Returns the type of tile located at: row,col. */
    public TileType computeSquare(int row, int col) {
        if (row < 0 || row > 3 || col < 0 || col > 3) {
            //throw new IllegalArgumentException("row and column must be between 0-" + (board.size() - 1));
        }
        // first: query the board to see what tile type exists at (row, col)
        TileType tpe = board.tileAt(row, col);

        // match on whatever type of tile was selected:
        return switch (tpe) {
            case Mine.MineInst          -> mine();
            case TileType.Uncovered t   -> t;
            case Hidden.HiddenInst      -> {
                int adjacentMines = adjacentMineCount(row, col);
                yield new Uncovered(adjacentMines);
            }
        };
    }

    /**
     * Uncovers the square at {@code row,col} mutating the board
     * with the uncovered/computed {@link TileType}.
     */
    public void advanceGame(int row, int col) {
        // compute the tile selected by row,col
        var tpe = computeSquare(row, col);
        updateBoard(row, col, tpe);
    }

    public boolean shouldAdvanceGame(int row, int col) {
        return switch (computeSquare(row, col)) {
            case Mine _ -> false;
            default     -> true;
        };
    }

    /**
     * Returns a vector of pairs that are left, right, top, down, and diagonal
     * to {@code row,col)}.
     * <p>
     * Note: this, by design, will generate invalid row column pairs (should be
     * filtered if used).
     */
    private Vector<Pair<Integer, Integer>> adjacentLocs(int row, int col) {
        return Vector.of( //
                Pair.of(row - 1, col), // top
                Pair.of(row + 1, col), // bottom
                Pair.of(row, col + 1), // right
                Pair.of(row, col - 1), // left
                Pair.of(row + 1, col - 1), // bottom-left
                Pair.of(row + 1, col + 1), // bottom-right
                Pair.of(row - 1, col - 1), // top-left
                Pair.of(row - 1, col + 1)  // top-right
        );
    }

    // protected to facilitate more direct testing
    protected int adjacentMineCount(int row, int col) {
        // step 1: filter the collection of all adjacent cells to (row, col)
        //         for only those in bounds
        var filtered = adjacentLocs(row, col).filter((p) -> switch (p) {
            case Pair(var x, var y) -> (x >= 0 && x <= 3) && (y >= 0 && y <= 3);
        });
        // step 2: filter the adjacent cells for ones that are mines and compute the size of the
        //         resulting collection/vector
        return filtered.map(xy -> board.tileAt(xy.first(), xy.second())) //
                .filter(TileType::isMine) //
                .size();
    }

    public void updateBoard(int row, int col, TileType updateTpe) {
        // will throw a runtime exception if row, col is bad
        sanityCheckRowCol(row, col);
        board = board.withUpdatedTile(row, col, updateTpe);
    }

    private void sanityCheckRowCol(int row, int col) {
        if (row < 0 || row > 3 || col < 0 || col > 3) {
           // throw new IllegalArgumentException("row and column must be between 0-" + (board.size() - 1));
        }
    }
}
