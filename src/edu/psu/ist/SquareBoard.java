package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.util.ArrayList;

/**
 * A fully immutable board type for the game. Clients should
 * use an instance of the {@link ValidatingBoardBuilder} class to
 * obtain a validated (immutable) {@link SquareBoard}.
 */
public final class SquareBoard {

    private final Vector<Row> rows;

    // private to enforce that only a validated board can exist
    private SquareBoard(Vector<Row> rows) {
        this.rows = rows;
    }

    public int dimension() {
        return rows.size();
    }

    public TileType tileAt(int row, int col) {
        return rows.get(row).get(col);
    }

    public SquareBoard withUpdatedTile(int row, int col, TileType tile) {
        var updatedRow = rows.get(row).update(col, tile);
        return new SquareBoard(rows.update(row, updatedRow));
    }

    /**
     * A builder class constructing only valid {@link SquareBoard} objects.
     * i.e.: use {@link #build()} to obtain a validated SquareBoard
     * object either wraps the validated board in a {@link Result.Ok} or
     * in a {@link Result.Err} instance.
     */
    public static class ValidatingBoardBuilder {

        private final ArrayList<Row> mutableRows = new ArrayList<>();
        private int rowNum = 0;
        private final StringBuilder errorMsg = new StringBuilder();

        public ValidatingBoardBuilder row(Row r) {
            mutableRows.add(r);
            rowNum = rowNum + 1;
            return this;
        }

        public ValidatingBoardBuilder row(TileType... tpes) {
            return row(new Row(rowNum, Vector.of(tpes)));
        }

        public ValidatingBoardBuilder row(Character... ts) {
            var converted = Vector.of(ts) //
                    .map(Object::toString) //
                    .map(ValidatingBoardBuilder::tryToConvertCellText);

            if (converted.forAll(Result::isOk)) {
                var tiles = converted.map(Result::get);
                row(new Row(rowNum, tiles));
            } else {
                converted.filter(Result::isError).forEach(err -> {
                    switch (err) {
                        case Result.Err(var msg) -> errorMsg.append(msg).append("\n");
                        default -> {}
                    }
                });
            }
            return this;
        }

        /**
         * Returns an {@link Result} containing either a successfully
         * validated board or a bunch of loading error messages in a string.
         */
        public Result<SquareBoard, String> build() {
            // does some validation checking on the board
            var n = mutableRows.size();

            if (mutableRows.stream().anyMatch(r -> r.length() != n)) {
                errorMsg.append("board not square\n");
            }

            if (!errorMsg.isEmpty()) {
                return Result.err(errorMsg.toString());
            } else {
                return Result.ok(new SquareBoard(Vector.ofAll(mutableRows)));
            }
        }

        private static Result<TileType, String> tryToConvertCellText(String s) {
            return switch (s) {
                case "_" -> Result.ok(TileType.hidden());
                case "*" -> Result.ok(TileType.mine());
                case String str when isInt(str) -> Result.ok(new TileType.Uncovered(Integer.parseInt(str)));
                default -> Result.err("Unrecognized cell: " + s);
            };
        }

        /**
         * Returns true only if text {@code s} contains a valid number
         * (positive or negative).
         */
        public static boolean isInt(String s) {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException _) {
                return false;
            }
        }
    }
}
