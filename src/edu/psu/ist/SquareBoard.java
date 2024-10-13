package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.util.ArrayList;

/**
 * A fully immutable board type for the game. Clients should
 * use the mutable {@link ValidatingBoardBuilder} class to obtain a validated
 * (immutable) instance of an {@link SquareBoard}.
 * <p>
 * <b>Note:</b> this class is fully immutable since {@link Vector} is
 * a fully immutable vector type (don't have to worry about leaking
 * references).
 */
public final class SquareBoard {

    private final Vector<Row> rows;

    private SquareBoard(ValidatingBoardBuilder builder) {
        rows = Vector.ofAll(builder.mutableRows);
    }

    // an internal copy constructor (for use on row vectors that have
    // already gone through the validation process).
    private SquareBoard(Vector<Row> rows) {
        this(new ValidatingBoardBuilder().rows(rows));
    }

    public int dimension() {
        return rows.size();
    }

    public TileType tileAt(int row, int col) {
        return rows.get(row).get(col);
    }

    public SquareBoard withUpdatedTile(int row, int col, TileType tile) {
        var updatedRow = rows.get(row).update(col, tile);
        var updatedVector = rows.update(row, updatedRow);
        return new SquareBoard(updatedVector);
    }

    /**
     * A builder class constructing only valid {@link SquareBoard} objects.
     * i.e.: the {@link #build()} call used to obtain the final SquareBoard object
     * will either wrap the validated board in a {@link Result#ok} instance or
     * in a {@link Result#err} instance.
     */
    public static class ValidatingBoardBuilder {

        private final ArrayList<Row> mutableRows = new ArrayList<>();
        private int rowNum = 0;

        public ValidatingBoardBuilder row(Row r) {
            mutableRows.add(r);
            rowNum = rowNum + 1;
            return this;
        }

        public ValidatingBoardBuilder row(TileType ... tpes) {
            return row(new Row(rowNum, Vector.of(tpes)));
        }

        public ValidatingBoardBuilder row(Character ... ts) {
            var converted = Vector.of(ts) //
                    .map(Object::toString) //
                    .map(ValidatingBoardBuilder::tryToConvertCellText);

            if (converted.forAll(Result::isOk)) {
                var tiles = converted.map(Result::get);
                row(new Row(rowNum, tiles));
            }

            return this;
        }

        public ValidatingBoardBuilder rows(Iterable<Row> rows) {
            for (var r : rows) {
                row(r);
            }
            return this;
        }

        /**
         * Returns a Result object that will contain either a successfully
         * built board or a bunch of loading error messages in a string.
         * (this type could be made richer -- e.g., return a list of actual
         * error objects containing line and col info.
         */
        public Result<SquareBoard, String> build() {
            // does some validation checking on the board
            var n = mutableRows.size();
            var errorMsg = "";
            var badCells = mutableRows.stream()
                    .anyMatch(Row::isMalformed);
            if (badCells) {
                errorMsg = errorMsg + "board contains unrecognized cell types\n";
            }
            var notSquare = mutableRows.stream()
                    .anyMatch(r -> r.length() == n);
            if (notSquare) {
                errorMsg = errorMsg + "board not square.";
            }
            if (badCells || notSquare) {
                return Result.err(errorMsg);
            } else {
                return Result.ok(new SquareBoard(this));
            }
        }

        private static Result<TileType, String> tryToConvertCellText(String s) {
            return switch (s) {
                case "_" -> Result.ok(TileType.hidden());
                case "*" -> Result.ok(TileType.mine());
                case String str when Utils.isInt(str) ->
                    Result.ok(new TileType.Uncovered(Integer.parseInt(str)));
                default -> Result.err("Unrecognized cell: " + s);
            };
        }
    }
}
