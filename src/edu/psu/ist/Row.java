package edu.psu.ist;

import io.vavr.collection.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public record Row(int rowNum, Vector<TileType> columns) {

    /**
     * Returns the column at index {@code col}.
     * @throws IllegalArgumentException if {@code col} is out of bounds.
     */
    public TileType get(int col) {
        validateCol(col);
        return columns.get(col);
    }

    /**
     * The update operator for a row replaces the tile type stored at
     * column, {@code col} with {@code tpe}.
     * <p>
     * Note: this method returns a copy of the (updated) row, leaving the
     * original row instance is unchanged.
     */
    public Row update(int col, TileType tpe) {
        validateCol(col);
        throw new UnsupportedOperationException("not done");
    }

    /** Precondition: 0 <= col <= {@code columns.size()}. */
    private void validateCol(int col) {
        if (col < 0 || col >= columns.size()) {
            throw new IndexOutOfBoundsException("col is out of bounds...");
        }
    }

    @Override public String toString() {
        var first = true;
        var sb = new StringBuilder();
        for (var tile : columns) {
            if (first) {
                sb.append(tile.cellAsString());
                first = false;
            } else {
                sb.append(" ").append(tile.cellAsString());
            }
        }
        return sb.toString();
    }
}
