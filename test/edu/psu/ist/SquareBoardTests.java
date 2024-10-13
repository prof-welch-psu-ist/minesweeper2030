package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SquareBoardTests {

    @Test public void testCantInitBadBoard() {
        // tests to see that the build() method returns failure
        // when given bad cells
        var board = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', 'a') //
                .row('_', '_').build();
        Assertions.assertTrue(board.isError());
        Assertions.assertEquals("Unrecognized cell type: 'a'", board.getError());
    }
}
