package edu.psu.ist;

import java.util.ArrayList;
import java.util.Arrays;

public record Row(int rowNum, ArrayList<TileType> columns) {

    /** Static factory method for constructing a row. */
    public static Row of(int rowNum, TileType... tiles) {
        return new Row(rowNum, new ArrayList<>(Arrays.asList(tiles)));
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
