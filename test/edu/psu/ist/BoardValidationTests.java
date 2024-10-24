package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static edu.psu.ist.TileType.un;
import static edu.psu.ist.TileType.hidden;
import static edu.psu.ist.TileType.mine;

public final class BoardValidationTests {

    @Test public void testCantInitBadBoard01() {
        // tests to see that the build() method returns failure
        // when given bad cells
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', 'a') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isError());
        Assertions.assertEquals("unrecognized cell: a", b1.getError());
    }

    @Test public void testCantInitBadBoard02() {
        // board is not square due to row count (all tiles are valid)
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isError());
        Assertions.assertEquals("board not square", b1.getError());
    }

    @Test public void testCantInitBadBoard03() {
        // board is not square due to bad col counts
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*', '_') //
                .row('_') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isError());
        Assertions.assertEquals("board not square", b1.getError());
    }

    @Test public void testCantInitBadBoard04() {
        // board is not square and has invalid cell types
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('$', '*', '+') //
                .row('_') //
                .row('&', '_').build();
        Assertions.assertTrue(b1.isError());
        Assertions.assertEquals("""
                unrecognized cell: $
                unrecognized cell: +
                unrecognized cell: &
                board not square
                """.trim(), b1.getError());
    }

    @Test public void testValidBoards01() {
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

    @Test public void testValidBoards02() {
        // test some valid boards ... note when we say valid
        // we do not mean: in a valid minesweeper *game* state..
        // just structurally valid w.r.t. tile types and dimension
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isOk());

        var b2 = new SquareBoard.ValidatingBoardBuilder() //
                .row('*', '*') //
                .row('8', '0').build();
        Assertions.assertTrue(b2.isOk());

        var b3 = new SquareBoard.ValidatingBoardBuilder() //
                .row('*').build();
        Assertions.assertTrue(b3.isOk());

        var b4 = new SquareBoard.ValidatingBoardBuilder() //
                .row('4').build();
        Assertions.assertTrue(b4.isOk());
    }

    @Test public void testValidBoards03() {

        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row(un(2)).build();
        Assertions.assertTrue(b1.isOk());
    }

    @Test public void testCantInitBadBoard05() {

        // using alternate row(..) method that accepts TileTypes,
        // not chars -- allows for more interesting failure tests
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row(un(2), un(2)).build();
        Assertions.assertTrue(b1.isError());
        Assertions.assertEquals("board not square", b1.getError());

        var b2 = new SquareBoard.ValidatingBoardBuilder() //
                .row(mine(), un(2)) //
                .row(un(-3)).build();
        Assertions.assertTrue(b2.isError());
        Assertions.assertEquals("""
                negative tile: -3
                board not square
                """.trim(), b2.getError());

        var b3 = new SquareBoard.ValidatingBoardBuilder() //
                .row(mine(), un(-12)) //
                .row(hidden(), un(-3)).build();
        Assertions.assertTrue(b3.isError());
        Assertions.assertEquals("""
                negative tile: -12
                negative tile: -3
                """.trim(), b3.getError());
    }
}
