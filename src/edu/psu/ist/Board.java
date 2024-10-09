package edu.psu.ist;

import java.util.ArrayList;

public record Board(ArrayList<Row> rows) {

    public boolean isMine(int row, int col) {
        return rows.get(row).get(col).isMine();
    }
}
