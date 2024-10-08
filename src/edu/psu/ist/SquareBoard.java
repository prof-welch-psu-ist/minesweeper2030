package edu.psu.ist;

import java.util.ArrayList;

/**
 * A board is a collection of rows. Note: this class
 * as a precondition assumes each row
 * @param rows
 */
public record SquareBoard(ArrayList<Row> rows) {
}
