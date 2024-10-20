package edu.psu.ist;

import edu.psu.ist.TileType.Mine;
import edu.psu.ist.TileType.Hidden;
import edu.psu.ist.immutableadts.Pair;
import io.vavr.collection.Vector;

import java.util.function.Predicate;

import static edu.psu.ist.TileType.*;

/**
 * A class that encapsulates methods that carry out the minesweeper game
 * logic.
 * <p>
 * The rows and cols passed to each public method of this class
 * assume 0-based indexing. The class is mutable -- note that various
 * methods assign-to/update the {@link this#board} field.
 */
public final class MinesweeperGame {

    /**
     * The internal representation of the board on which the
     * game is played.
     */
    private SquareBoard board;

    /**
     * Initializes the game with a user-specified {@code startingBoard}.
     *
     * @throws IllegalArgumentException if the {@code startingBoard} is
     *                                  malformed.
     */
    public MinesweeperGame(SquareBoard startingBoard) {
        board = startingBoard;
    }

    /**
     * Default constructor just initializes the game to use the
     * board from steps 1-4.
     */
    public MinesweeperGame() {
        board = new SquareBoard.ValidatingBoardBuilder() //
                .row(hidden(), hidden(), mine(), hidden()) //
                .row(hidden(), hidden(), mine(), hidden()) //
                .row(hidden(), hidden(), hidden(), hidden()) //
                .row(hidden(), hidden(), hidden(), hidden()).build().get();
    }

    /** Returns the type of tile located at: row,col. */
    public TileType revealSquare(int row, int col) {
        // first: query the board to see what tile type exists at (row, col)
        TileType tpe = board.tileAt(row, col);

        // match on whatever type of tile was selected:
        return switch (tpe) {
            case Mine.MineInst -> mine();
            case TileType.Uncovered t -> t;
            case Hidden.HiddenInst -> {
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
        var tpe = revealSquare(row, col);
        updateBoard(row, col, tpe);
    }

    public boolean inWinState() {
        var hiddenCount = board.compute(0, (t, acc) -> switch (t) {
            case Hidden _ -> acc + 1;
            default -> acc;
        });
        return hiddenCount == 0;
    }

    /**
     * Returns a vector of coordinates for all (in-bound) cells that are left,
     * right, top, down, and diagonal to {@code row,col)}.
     */
    private Vector<Pair<Integer, Integer>> adjacentLocs(int row, int col) {
        var dim = board.dimension();

        var adjacentLocs = Vector.of( //
                Pair.of(row - 1, col), // top
                Pair.of(row + 1, col), // bottom
                Pair.of(row, col + 1), // right
                Pair.of(row, col - 1), // left
                Pair.of(row + 1, col - 1), // bottom-left
                Pair.of(row + 1, col + 1), // bottom-right
                Pair.of(row - 1, col - 1), // top-left
                Pair.of(row - 1, col + 1)); // top-right

        return adjacentLocs.filter((p) -> switch (p) {
            case Pair(var x, var y) -> (x >= 0 && x < dim) && (y >= 0 && y < dim);
        });
    }

    // protected to facilitate more direct testing
    protected int adjacentMineCount(int row, int col) {
        var dim = board.dimension();

        // step 1: get xy coordinates for all adjacent cells to (row, col)
        //         (only those in bounds)
        var filtered = adjacentLocs(row, col);

        // step 2: filter the adjacent cells for ones that are mines and compute the size of the
        //         resulting collection/vector
        return filtered.map(xy -> board.tileAt(xy.first(), xy.second())) //
                .filter(TileType::isMine) //
                .size();
    }

    /*private TileType tile(int row, int col) {

    }

    private SquareBoard floodFill(int row, int col) {
        return switch (revealSquare(row, col)) {
            // the mine at row,col is 0 (so not adjacent to any mines)
            case TileType.Uncovered(var ct) when ct == 0 -> {
                var neighbors = adjacentLocs(row, col);
                neighbors.filter(xy -> board.tileAt(xy.first(), xy.second()) == Hidden.HiddenInst)
                // process potentially all 8 neighboring positions and:
                // a) reveal the cell at pos (i, j) if it is hidden (ignoring mines)
                // b) if the revealed cell is also uncovered, recurse
            }
            // otherwise do nothing
            default -> board;
        };
    }*/

    /**
     * <pre><code>
     * precondition:  0 <= row, col <= dimension()
     * postcondition: updateBoard = board[row][col]
     * </code></pre>
     */
    public void updateBoard(int row, int col, TileType updateTpe) {
        board = board.withUpdatedTile(row, col, updateTpe);
    }

    public String renderGameState() {
        // queries the board to see how many mines
        var mineCount = board.compute(0, (tile, acc) -> switch (tile) {
            case Mine _ -> acc + 1;
            default -> acc;
        });
        // how many remaining hidden squares?
        var hiddenCt = board.compute(0, (tile, acc) -> switch (tile) {
            case Hidden _ -> acc + 1;
            default -> acc;
        });
        var boardStr = this.toString();

        return String.format("""
                %s
                mine ct: %s
                hidden ct: %s
                """.trim(), boardStr, mineCount, hiddenCt);
    }

    // just renders the board
    @Override public String toString() {
        return board.toString();
    }
}
