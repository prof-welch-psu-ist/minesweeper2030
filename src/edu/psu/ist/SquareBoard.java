package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.util.ArrayList;

/**
 * A fully immutable board type for the game. Clients should use an instance
 * of the {@link ValidatingBoardBuilder} class to obtain a validated
 * (immutable) {@link SquareBoard}.
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

        private final ArrayList<Vector<Result<TileType, String>>> mutableRows2 = new ArrayList<>();
        private Vector<String> errors = Vector.empty();

        public ValidatingBoardBuilder row(TileType... tpes) {
            var converted = Vector.of(tpes) //
                    .map(Result::<TileType, String>ok);
            mutableRows2.add(converted);
            return this;
        }

        public ValidatingBoardBuilder row(Character... ts) {
            var converted = Vector.of(ts) //
                    .map(Object::toString) //
                    .map(ValidatingBoardBuilder::tryToConvertCellText);
            var errMsgs = converted.filter(Result::isError) //
                    .map(r -> switch (r) {
                        case Result.Err(var msg) -> msg + "\n";
                        default -> "";
                    });
            errors = errors.appendAll(errMsgs);
            return this;
        }

        /**
         * Returns an {@link Result} containing either a successfully
         * validated board or a bunch of loading error messages in a string.
         */
        public Result<SquareBoard, String> build() {
            // does some validation checking on the board
            var n = mutableRows2.size();

            Vector<Row> rows = Vector.empty();
            int rowNum = 0;

            // constructing the final Vector<Row> objects from Vector<Result<Tile>..>
            for (var row : mutableRows2) {
                if (row.filter(Result::isError).isEmpty()) {
                    var tiles = row.map(Result::get);
                    rows = rows.append(new Row(rowNum, tiles));
                    rowNum++;
                }
            }

            if (mutableRows2.stream().anyMatch(r -> r.length() != n)) {
                errors = errors.append("board not square");
            }

            if (!errors.isEmpty()) {
                return Result.err(errors.mkString("\n"));
            } else {
                return Result.ok(new SquareBoard(rows));
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
