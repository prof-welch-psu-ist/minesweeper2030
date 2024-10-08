package edu.psu.ist;

import java.util.ArrayList;

public record Row(int rowNum, ArrayList<TileType> columns) {

    /** Static factory method for constructing a row. */
    public static Row of(int rowNum, TileType ... tiles) {
        for (var t : tiles) {
            
        }
        return new Row(rowNum, );
    }
}
