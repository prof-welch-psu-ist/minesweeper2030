package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.util.ArrayList;

/**
 * A fully immutable board type for the game. Clients should
 * use the mutable {@link BoardBuilder} class to obtain a validated
 * (immutable) instance of an {@link SquareBoard}.
 * <p>
 * <b>Note:</b> this class is fully immutable since {@link Vector} is
 * a fully immutable vector type (don't have to worry about leaking
 * references).
 */
public final class SquareBoard {

    private final Vector<Row> rows;

    private SquareBoard(BoardBuilder builder) {
        rows = null ;

    }

    public int dimension() {
        return rows.size();
    }

    public TileType tileAt(int row, int col) {
        return rows.get(row).get(col);
    }

    /**
     * A builder class to facilitate construction of well-formed
     * {@code Board} objects.
     */
    public static class BoardBuilder {

        private final ArrayList<Row> mutableRows = new ArrayList<>();
        private int rowNum = 0, lastRowLen = 0;

        public BoardBuilder row(TileType ... tpes) {
            var row = new Row(rowNum, Vector.of(tpes));
            mutableRows.add(row);
            rowNum = rowNum + 1;
            lastRowLen = row.columns().length();
            return this;
        }

        public BoardBuilder row(String ... ts) {
            var converted = Vector.of(ts).map(cell -> switch (cell) {
                case "_" -> TileType.Safe.SafeInst;
                case "*" -> TileType.Mine.MineInst;
                case String s -> {

                }
            });
            var row = new Row(rowNum, Vector.of(ts))

        }

        public SquareBoard build() {
            return
        }

        /**
         * Returns the {@link edu.psu.ist.TileType} for {@code s}.
         * @throws IllegalArgumentException if it's invalid.
         */
        private TileType validateCellText(String s) {
            return switch (s) {
                case "_" -> TileType.Safe.SafeInst;
                case "*" -> TileType.Mine.MineInst;
                case String str when Utils.isInt(str) ->
                    new TileType.Uncovered(Integer.parseInt(str));
                default -> throw new IllegalArgumentException(
                        "bad cell type: " + s);
            };
        }


    }
}
