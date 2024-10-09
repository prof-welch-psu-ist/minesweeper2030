package edu.psu.ist;

import edu.psu.ist.TileType.Mine;
import edu.psu.ist.TileType.Safe;
import edu.psu.ist.immutableadts.Pair;
import edu.psu.ist.immutableadts.Result;

import java.util.ArrayList;

/**
 * A class that encapsulates a single game of minesweeper.
 * Class invariant: the rows and cols passed to each public
 * method of this class assume 0-based indexing.
 */
public final class MinesweeperGame {

    /**
     * The internal representation of the board on which the game is played.
     * <p>
     * Note: this is {@code final} to prevent reassignment
     * (outside the class constructors).
     */
    private final ArrayList<Row> board;

    /**
     * The dimension for our square game board.
     */
    private final int dimension;

    /**
     * Initializes the game with a user-specified {@code startingBoard}.
     *
     * @throws IllegalArgumentException if the {@code startingBoard} is
     *                                  malformed.
     */
    public MinesweeperGame(ArrayList<> startingBoard) {
        board = startingBoard;
        // returns (N, mine-count)
        var p = sanityCheckStartBoard(board);
        // init the dimension here (only executes if the sanity check
        // call above doesn't raise an illegal arg. exception)
        this.dimension = p.first();
    }

    /**
     * Default constructor just initializes the game to use the
     * board from steps 1-4.
     */
    public MinesweeperGame() {
        board = new ArrayList<>();
        board.add(Row.of(0, Safe.SafeInst, Safe.SafeInst, Mine.MineInst, Safe.SafeInst),
        board.add(Row.of(1, Safe.SafeInst, Safe.SafeInst, Mine.MineInst, Safe.SafeInst));
        board.add(asList("_", "_", "_", "_"));
        board.add(asList("_", "_", "_", "_"));
        this.dimension = 4;
    }


    public boolean uncoverSquare(int row, int col) {
        if (row < 0 || row > 3 || col < 0 || col > 3) {
            throw new IllegalArgumentException("row and column must be between 0-" + (board.size() - 1));
        }
        // first: query the board to see tile string exists at (row, col)
        String type = board.get(row).get(col);

        // next, if type is a mine, it's game over (return false)
        if (type.equals("*")) {
            return false;
        } else if (type.equals("_")) {
            // if the type is a safe square then compute how many mines
            // it's adjacent to.
            int adjacentMineCt = 0;

            // now
        }
    }

    public void updateBoard(int row, int col, String updatedTileType) {
        // will throw a runtime exception if row, col is bad
        sanityCheckRowCol(row, col);

    }

    private void sanityCheckRowCol(int row, int col) {
        if (row < 0 || row > 3 || col < 0 || col > 3) {
            throw new IllegalArgumentException("row and column must be between 0-" + (board.size() - 1));
        }
    }

    private ArrayList<String> asList(String... contents) {
        var result = new ArrayList<String>();
        for (String s : contents) {
            result.add(s);
        }
        return result;
    }

    /**
     * Validates a user provided starting board. Throws an {@link IllegalArgumentException}
     * under two conditions:
     * <ul>
     *     <li>the board is not NxN (square);</li>
     *     <li>the board contains an unrecognized/invalid cell that isn't
     *     {@code '_'} (safe) or {@link '*'} (mined)</li>
     * </ul>
     * Returns a pair object that
     * encapsulates/"bundles together" the dimension followed by the
     * mine count {@code (dimension, observedMineCt)}
     */
    private Result<ArrayList<Row>, Integer> sanityCheckStartBoard(ArrayList<ArrayList<String>> board) {
        // first, ensure it's an NxN board (we'll require this)
        int n = board.size();
        int mineCt = 0;
        for (var row : board) {
            if (row.size() != n) {
                throw new IllegalArgumentException("board must be square (NxN), " + "offending row with too many columns: " + row);
            }
        }

        // OK: so if we're here, it means the board is at least square.
        // next, examine each cell to ensure it's either a: "_" or a "*".
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                String cell = board.get(r).get(c);
                var isValidTile = cell.equals("*") || cell.equals("_");
                if (!isValidTile) {
                    throw new IllegalArgumentException("unrecognized cell type: " + cell);
                }
                if (cell.equals("*")) {
                    mineCt = mineCt + 1;
                }
            }
        }
        return Pair.of(n, mineCt);
    }
}
