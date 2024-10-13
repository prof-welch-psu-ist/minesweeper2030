package edu.psu.ist;

import io.vavr.collection.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public record Row(int rowNum, Vector<TileType> columns) {

    /** Returns the column at index {@code col}. */
    public TileType get(int col) {
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
        return new Row(rowNum, columns.update(col, tpe));
    }

    public int length() { return columns.length(); }

    @Override public String toString() {
        // can also do (since vavr collections offer a nice string delimiter method)
        return columns.map(TileType::cellAsString)
                      .mkString(" ");
        // more traditional way:
        /*var first = true;
        var sb = new StringBuilder();
        for (var tile : columns) {
            if (first) {
                sb.append(tile.cellAsString());
                first = false;
            } else {
                sb.append(" ").append(tile.cellAsString());
            }
        }
        return sb.toString();*/
    }
}