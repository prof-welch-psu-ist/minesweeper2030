package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static edu.psu.ist.TileType.*;

public final class BoardComputeTests {

    @Test public void testFold01() {

        var boardRes = new SquareBoard.ValidatingBoardBuilder() //
                .row(mine(), un(2)) //
                .row(un(3), un(0)) //
                .build();
        Assertions.assertTrue(boardRes.isOk());
        var board = boardRes.get();
        // assert above means its safe here to unwrap the board

        // define a small function to count the number of uncovered tiles
        // note: local variable type inference ('var') doesn't
        // work here if initializing said variable to a lambda expression
        BiFunction<TileType, Integer, Integer> f = (tile, acc) -> switch (tile) {
            case Uncovered _ -> acc + 1; // acc = short for: accumulator
            default -> acc;
        };
        var uncoveredCt = board.compute(0, f);
        Assertions.assertEquals(3, uncoveredCt);

        // now count the uncovered tiles containing mine counts == 3
        f = (tile, acc) -> switch (tile) {
            case Uncovered(var ct) when ct == 3 -> acc + 1;
            default -> acc;
        };
        var uncoveredCt2 = board.compute(0, f);
        Assertions.assertEquals(1, uncoveredCt2);
    }

    @Test public void testFold02() {
        var boardRes = new SquareBoard.ValidatingBoardBuilder() //
                .row(mine(), un(2), hidden()) //
                .row(un(3), un(0), mine()) //
                .row(un(3), mine(), un(0)) //
                .build();
        Assertions.assertTrue(boardRes.isOk());
        var board = boardRes.get(); // assert above means its safe here to unwrap the board

        // how many mines?
        BiFunction<TileType, Integer, Integer> f = (tile, acc) -> switch (tile) {
            case TileType.Mine _ -> acc + 1;
            default -> acc;
        };
        Assertions.assertEquals(3, board.compute(0, f));

        // how many hidden tiles?
        f = (tile, acc) -> switch (tile) {
            case Hidden _ -> acc + 1;
            default -> acc;
        };
        Assertions.assertEquals(1, board.compute(0, f));
    }

    @Test public void testFold03() {

        var boardRes = new SquareBoard.ValidatingBoardBuilder() //
                .row(mine(), un(2), hidden()) //
                .row(un(3), un(0), mine()) //
                .row(un(3), mine(), un(0)) //
                .build();
        Assertions.assertTrue(boardRes.isOk());
        var board = boardRes.get();

        // sum of all uncovered tiles?
        BiFunction<TileType, Integer, Integer> f = (tile, acc) -> switch (tile) {
            case Uncovered(var ct) -> ct + acc;
            default -> acc;
        };
        // assert sum of all uncovered squares is 8
        Assertions.assertEquals(8, board.compute(0, f));
        Assertions.assertEquals("""
                * 2 _
                3 0 *
                3 * 0
                """.trim(), board.toString());

        // now decrement one of the uncovered cells by one
        board = board.withUpdatedTile(2, 0, un(2));
        Assertions.assertEquals("""
                * 2 _
                3 0 *
                2 * 0
                """.trim(), board.toString());

        // now the sum of all uncovered squares is 7...
        Assertions.assertEquals(7, board.compute(0, f));

        board = board.withUpdatedTile(2, 0, un(0));
        Assertions.assertEquals("""
                * 2 _
                3 0 *
                0 * 0
                """.trim(), board.toString());

        // now count should drop from 7 to 5
        Assertions.assertEquals(5, board.compute(0, f));
    }
}