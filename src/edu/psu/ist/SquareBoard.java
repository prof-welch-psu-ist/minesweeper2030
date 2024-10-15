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
     * an error msg in a {@link Result.Err} instance.
     */
    public static class ValidatingBoardBuilder {

        /**
         * I know, I know: a pretty ugly type here... Read as:
         * <pre>
         * "mutRows is an arraylist that stores (immutable) vectors
         * that each contain Result object instances ...
         * (where each result instance can be either a well-formed {@link TileType} instance
         * or an error msg)."</pre>
         */
        private final ArrayList<Vector<Result<TileType, String>>> mutRows = new ArrayList<>();

        public ValidatingBoardBuilder row(TileType... tpes) {

            var converted = Vector.of(tpes) //
                    .map(ValidatingBoardBuilder::validateTile);

            mutRows.add(converted);
            return this;
        }

        public ValidatingBoardBuilder row(Character... ts) {
            var converted = Vector.of(ts) //
                    .map(Object::toString) //
                    .map(ValidatingBoardBuilder::tryToConvertCellText);
            mutRows.add(converted);
            return this;
        }

        /**
         * Builds a validated {@link SquareBoard} focusing strictly on well-formedness
         * (e.g., rows are the same length, tiles are valid, and the board is square).
         * <p>
         * Game-specific checks (e.g., uncovered square logic) are handled in
         * {@link MinesweeperGame}. This method is narrowly focused on structural
         * checks mentioned.
         */
        public Result<SquareBoard, String> build() {
            // does some validation checking on the board
            var n = mutRows.size();

            var rows = Vector.<Row>empty();
            var rowNum = 0;

            // constructing the final Vector<Row> objects from Vector<Result<Tile>..>
            for (var row : mutRows) {
                if (row.filter(Result::isError).isEmpty()) {
                    var tiles = row.map(Result::get);
                    rows = rows.append(new Row(rowNum, tiles));
                    rowNum++;
                }
            }

            var accumulatedErrs = Vector.empty();
            for (var rawRow : mutRows) {
                var converted = rawRow.filter(Result::isError).map(r -> switch (r) {
                    case Result.Err(var msg) -> msg;
                    default -> "";
                });
                accumulatedErrs = accumulatedErrs.appendAll(converted);
            }

            if (mutRows.stream().anyMatch(r -> r.length() != n)) {
                accumulatedErrs = accumulatedErrs.append("board not square");
            }

            if (!accumulatedErrs.isEmpty()) {
                return Result.err(accumulatedErrs.mkString("\n"));
            } else {
                return Result.ok(new SquareBoard(rows));
            }
        }

        private static Result<TileType, String> validateTile(TileType tpe) {
            return switch (tpe) {
                case TileType.Uncovered(var ct) when ct < 0 -> Result.err("negative tile: " + ct);
                default -> Result.ok(tpe);
            };
        }

        private static Result<TileType, String> tryToConvertCellText(String s) {
            return switch (s) {
                case "_" -> Result.ok(TileType.hidden());
                case "*" -> Result.ok(TileType.mine());
                case String str when isInt(str) -> Result.ok(new TileType.Uncovered(Integer.parseInt(str)));
                default -> Result.err("unrecognized cell: " + s);
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
