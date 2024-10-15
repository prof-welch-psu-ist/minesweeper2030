package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SquareBoardTests {

    @Test public void testCantInitBadBoard01() {
        // tests to see that the build() method returns failure
        // when given bad cells
        var board = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', 'a') //
                .row('_', '_').build();
        Assertions.assertTrue(board.isError());
        Assertions.assertEquals("unrecognized cell: a", board.getError());
    }

    @Test public void testCantInitBadBoard02() {
        // board is not square due to row count (all tiles are valid)
        var board = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_') //
                .row('_', '_').build();
        Assertions.assertTrue(board.isError());
        Assertions.assertEquals("board not square", board.getError());
    }

    @Test public void testCantInitBadBoard03() {
        // board is not square due to bad col counts
        var board = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*', '_') //
                .row('_') //
                .row('_', '_').build();
        Assertions.assertTrue(board.isError());
        Assertions.assertEquals("board not square", board.getError());
    }

    @Test public void testCantInitBadBoard04() {
        // board is not square and has invalid cell types
        var board = new SquareBoard.ValidatingBoardBuilder() //
                .row('$', '*', '+') //
                .row('_') //
                .row('&', '_').build();
        Assertions.assertTrue(board.isError());
        Assertions.assertEquals(
                """
                unrecognized cell: $
                unrecognized cell: +
                unrecognized cell: &
                board not square
                """.trim(), board.getError());
    }

    @Test public void testValidBoard01() {
        // test some valid boards ... note when we say valid
        // we do not mean: in a valid minesweeper *game* state
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isOk());

        var b2 = new SquareBoard.ValidatingBoardBuilder() //
                .row('*', '*') //
                .row('0', '0').build();
        Assertions.assertTrue(b2.isOk());
    }

    @Test public void testAltTileRow() {
        // test some valid boards ... note when we say valid
        // we do not mean: in a valid minesweeper *game* state
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isOk());

        var b2 = new SquareBoard.ValidatingBoardBuilder() //
                .row('*', '*') //
                .row('0', '0').build();
        Assertions.assertTrue(b2.isOk());

        var b3 = new SquareBoard.ValidatingBoardBuilder() //
                .row('*').build();
        Assertions.assertTrue(b3.isOk());

        var b4 = new SquareBoard.ValidatingBoardBuilder() //
                .row('4').build();
        Assertions.assertTrue(b4.isOk());
    }
}
