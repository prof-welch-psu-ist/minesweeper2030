package edu.psu.ist;

import io.vavr.collection.List;
import io.vavr.collection.Vector;

import java.util.ArrayList;

/** A fully immutable {@link Vector} type. Uses the vavr library. */
public record Board(Vector<Row> rows) {

    public boolean isMine(int row, int col) {
        return rows.get(row).get(col).isMine();
    }

    /**
     * A class to facilitate construction of well-formed {@code Board} objects.
     * <p>
     * Here, this is not quite equivalent to Bloch's notion of "Builder" as we're
     * building instances of <em>records</em>... Here we can't mark the constructor
     * of a record private (as we could with a normal class via Bloch's builder pattern).
     * <p>
     * It is unfortunate that records always must have a public constructor. Now
     * someone can conceivably construct a non-square board object like:
     * <pre><code>
     *     new Board(Vector.of(Row.of(TileType.SafeInst, TypeType.SafeInst))) // only two columns, one row...
     * </code></pre>
     * With normal (non-record) classes C, we can mark the constructor private and only
     * allow clients to obtain an instance of C by going through it's designated builder
     * class. Records short-circuit this by their definition
     * (they always expose their fields).
     */
    public static class BoardBuilder {

        /**
         * A flag that holds {@code true} if two rows stored by this board builder
         * have unequal lengths; {@code false} if there are rows of varying length.
         */
        private boolean rowsOk = false;
        private ArrayList<Row> rows = new ArrayList<>();
        public BoardBuilder row(TileType ... tpes) {

        }
    }
}
