package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static edu.psu.ist.TileType.*;

public final class SquareBoardTests {

    @Test public void testFold01() {

        var boardRes = new SquareBoard.ValidatingBoardBuilder()
                .row(mine(), un(2)) //
                .row(un(3), un(0)) //
                .build();
        Assertions.assertTrue(boardRes.isOk());
        var board = boardRes.get();
        // assert above means its safe here to unwrap the board

        // define a small function to count the number of uncovered tiles
        // note: local variable type inference ('var') doesn't
        // work here if initializing said variable to a lambda expression
        BiFunction<TileType, Integer, Integer> f = (tile, x) -> switch (tile) {
            case Uncovered _ -> x + 1;
            default         -> x;
        };
        var uncoveredCt = board.compute(0, f);
        Assertions.assertEquals(3, uncoveredCt);

        // now count the uncovered tiles containing mine counts == 3
        f = (tile, x) -> switch (tile) {
            case Uncovered(var ct) when ct == 3 -> x + 1;
            default -> x;
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
        BiFunction<TileType, Integer, Integer> f = (tile, x) -> switch (tile) {
            case TileType.Mine _ -> x + 1;
            default -> x;
        };
        var uncoveredCt = board.compute(0, f);
        Assertions.assertEquals(3, uncoveredCt);
        // --

        // how many hidden tiles?
        f = (tile, x) -> switch (tile) {
            case Hidden _ -> x + 1;
            default -> x;
        };
        var uncoveredCt2 = board.compute(0, f);
        Assertions.assertEquals(1, uncoveredCt2);
        // --
    }

    @Test public void testFold03() {

        // sum of all uncovered tiles?
        BiFunction<TileType, Integer, Integer> f = (tile, x) -> switch (tile) {
            case Uncovered(var ct) -> ct + x;
            default -> x;
        };
        var uncoveredCt3 = boardRes.get().compute(0, f);
        boardRes = boardRes.get().withUpdatedTile() Assertions.assertEquals(8, uncoveredCt3);
    }

}
