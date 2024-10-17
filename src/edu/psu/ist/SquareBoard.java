package edu.psu.ist;

import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.util.ArrayList;
import java.util.function.BiFunction;

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

    /**
     * Returns a new board with {@code row,col} updated to the provided
     * {@code tile} type.
     * <p>
     * precondition: 0 <= row, col <= {@link #dimension() - 1} and
     *          that if the provided tile is uncovered, it contains a
     *          positive number
     */
    public SquareBoard withUpdatedTile(int row, int col, TileType tile) {
        var updatedRow = rows.get(row).update(col, tile);
        return new SquareBoard(rows.update(row, updatedRow));
    }

    /**
     * Left-folds the rows of this board into a single value {@code A} using
     * the provided binary function {@code f}.
     * <p>
     * Can think of this operation as enabling users of this class to
     * issue arbitrary queries to this board -- like "how many mines are there"
     * via the function {@code f}. These queries essentially allow you to
     * "collapse" or 'fold' the board and its rows into a single value of type
     * {@code A}.
     * <p>
     * This way we don't need to offer a bunch of one off (basically samey) methods
     * like: mineCount(), safeCount(), howManyUncovered(), etc. as this compute
     * method generalizes/subsumes them all.
     */
    public <A> A compute(A start, BiFunction<TileType, A, A> f) {
        return rows.foldLeft(start, (a, row) ->
                        row.columns().foldLeft(a,
                                (a1, tile) -> f.apply(tile, a1)));
        //alternative (more familiar) imperative way:
        //var result = start;
        //for (var row : rows) {
        //  for (var tileTpe : row.columns()) {
        //      result = f.apply(tileTpe, result);
        //  }
        //}
        //return result;
    }

    @Override public String toString() {
        return rows.mkString("\n");
    }

    /**
     * A builder class constructing only valid {@link SquareBoard} objects.
     * i.e.: use {@link #build()} to obtain a validated SquareBoard
     * object that either wraps the validated board in a {@link Result.Ok} or
     * an error msg in a {@link Result.Err}.
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

        // nb: TileType... tpes is "syntactic" sugar for an array of
        // TileTypes: TileType[]
        public ValidatingBoardBuilder row(TileType... tpes) {

            var converted = Vector.of(tpes) //
                    .map(ValidatingBoardBuilder::validateTile);

            mutRows.add(converted);
            return this;
        }

        public ValidatingBoardBuilder row(String rowText) {
            return row(rowText.toCharArray());
        }

        public ValidatingBoardBuilder row(char ... cs) {
            var boxedCsArray = new Character[cs.length];
            // need this since java autoboxing doesn't work for arrays of
            // some primitive type. E.g.: int[] won't get auto-boxed to Integer[]
            // Has to do with differences in memory layouts
            // between primitively typed arrays vs those of some reference (boxed) type.
            // e.g.: byte[] is an array of primitively type byte values (so each value
            // is represented using 8 bits). But the boxed version, Byte[] would
            // be forced to store all 32-bit wide references to Byte objects
            for (int i = 0; i < boxedCsArray.length; i++) {
                boxedCsArray[i] = cs[i];
            }
            var converted = Vector.of(boxedCsArray) //
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
