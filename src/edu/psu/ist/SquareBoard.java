package edu.psu.ist;

import java.util.ArrayList;

/**
 * A board is a collection of rows. Note: this class
 * as a precondition assumes each row is the same length
 * (i.e.: the board is "square")
 */
public record SquareBoard(ArrayList<Row> rows) {
}
