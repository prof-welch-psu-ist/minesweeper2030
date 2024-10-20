package edu.psu.ist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class MinesweeperGameTests {

    @Test public void test01() {
        // testing mine detection logic
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('0', '*') //
                .row('1', '_').build();
        Assertions.assertTrue(b1.isOk());
        var g = new MinesweeperGame(b1.get());
        Assertions.assertEquals(1, g.adjacentMineCount(1, 0));
    }

    @Test public void test02() {
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('*', '*').build();
        Assertions.assertTrue(b1.isOk());
        var g = new MinesweeperGame(b1.get());

        Assertions.assertEquals(3, g.adjacentMineCount(0, 0));
        g.updateBoard(1, 1, TileType.un(0));
        Assertions.assertEquals("""
                _ *
                * 0
                mine ct: 2
                hidden ct: 1
                """.trim(), g.renderGameState());
        Assertions.assertEquals(2, g.adjacentMineCount(1, 1));
        Assertions.assertEquals(2, g.adjacentMineCount(0, 0));
    }

    @Test public void test03() {
        // testing a small game to the end
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isOk());

        var g = new MinesweeperGame(b1.get());
        g.advanceGame(0, 0);
        Assertions.assertEquals("""
                1 *
                _ _
                mine ct: 1
                hidden ct: 2
                """.trim(), g.renderGameState());

        g.advanceGame(1, 1);
        Assertions.assertEquals("""
                1 *
                _ 1
                mine ct: 1
                hidden ct: 1
                """.trim(), g.renderGameState());

        g.advanceGame(1, 0);
        Assertions.assertEquals("""
                1 *
                1 1
                mine ct: 1
                hidden ct: 0
                """.trim(), g.renderGameState());
        Assertions.assertTrue(g.inWinState());
    }

    @Test public void test04() {
        // testing a (very) small 1x1 board
        // (make sure adjacency checking logic works)
        var b1 = new SquareBoard.ValidatingBoardBuilder() //
                .row('_', '*') //
                .row('_', '_').build();
        Assertions.assertTrue(b1.isOk());

        var g = new MinesweeperGame(b1.get());
        g.advanceGame(0, 0);
        Assertions.assertEquals("""
                1 *
                _ _
                mine ct: 1
                hidden ct: 2
                """.trim(), g.renderGameState());

        g.advanceGame(1, 1);
        Assertions.assertEquals("""
                1 *
                _ 1
                mine ct: 1
                hidden ct: 1
                """.trim(), g.renderGameState());

        g.advanceGame(1, 0);
        Assertions.assertEquals("""
                1 *
                1 1
                mine ct: 1
                hidden ct: 0
                """.trim(), g.renderGameState());
        Assertions.assertTrue(g.inWinState());
    }
}
