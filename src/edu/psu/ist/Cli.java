package edu.psu.ist;

import edu.psu.ist.immutableadts.Pair;
import edu.psu.ist.immutableadts.Result;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public final class Cli {

    public static final String Banner =
            String.format("Minesweeper 2030 (version: %s)", Version.Current);

    public static final ExampleBoard ExBoard01 =
            new ExampleBoard(1, """
            __*_
            __*_
            ____
            ____
            """);

    public static void main(String[] args) {
        System.out.println(Cli.Banner);
        var scan = new Scanner(System.in);

        if (args.length == 0) {
            System.out.println("no file passed in, playing sample board...");
            var loadRes = loadFromString(ExBoard01.exampleBoardText);
            doStartGame(scan, loadRes);
        } else {
            var loadRes = loadFromFile(args[0]);
            doStartGame(scan, loadRes);
        }
    }

    /**
     * Given a scanner and a (potentially) loaded board, starts the game, or,
     * if an {@link Result.Err} is passed, prints the cause of the load failure
     * and exits.
     */
    private static void doStartGame(Scanner scan, Result<SquareBoard, String> loadRes) {
        switch (loadRes) {
            case Result.Ok(var b) -> {
                MinesweeperGame g = new MinesweeperGame(b);
                System.out.println(g.renderGameState());
                doLoop(scan, g);
            }
            case Result.Err(var err) -> System.err.println(err);
        }
    }

    private static void doLoop(Scanner scan, MinesweeperGame g) {
        final var sentinelText = "q";
        System.out.println("enter a row,col number (1-indexed, ex: 1,4) - type "
                + sentinelText + " to quit");
        var rawInput = scan.nextLine();
        if (rawInput.equalsIgnoreCase("q")) {
            System.out.println("quitting - good game");
        }
        var parsedInput = parseInputText(rawInput);

        switch (parsedInput) {
            case Result.Ok(_) when g.inWinState() ->
                    System.out.println("you win!");
            case Result.Ok(Pair(var row, var col)) -> {
                var row2 = row - 1;
                var col2 = col - 1;
                var tpe = g.revealSquare(row2, col2);
                switch (tpe) {
                    case TileType.Mine _ -> System.out.println("you lose");
                    default -> {
                        g.advanceGame(row2, col2);
                        System.out.println();
                        System.out.println(g.renderGameState());
                        System.out.println();
                        doLoop(scan, g);
                    }
                }
            }
            case Result.Err(var msg) -> {
                System.err.println(g);
                System.err.println("bad input: " + msg);
                doLoop(scan, g); // loop again
            }
        }
    }

    /**
     * Attempts to load the board from a text file; returns a string of error
     * messages if this fails (the board contained is bad,
     * the file fails to load, etc.). Otherwise, returns a result containing a
     * fairly well-formed board ... square, all tile types valid. That said:
     * the loaded board might be <em>semantically</em> nonsensical, e.g.:
     * <pre><code>
     *     0*0
     *     000
     *     000
     * </code></pre>
     * Here, the cells around the mine should have a mine count of 1. This method
     * will happily load this representation. Though it objects when there is a
     * <em>syntactically</em> invalid board in the file.
     */
    public static Result<SquareBoard, String> loadFromFile(String fileName) {// not used atm
        try (var scan = new Scanner(Path.of(fileName))) {
            if (!fileName.endsWith(".swp")) {
                return Result.err("file must end in a .swp extension");
            }
            return processBoardFromScanner(scan);
        } catch (IOException e) {
            return Result.err(e.getMessage());
        } catch (InvalidPathException e) {
            return Result.err("bad file path: " + fileName);
        }
    }

    public static Result<SquareBoard, String> loadFromString(String boardText) {
        try (var scan = new Scanner(boardText)) {
            return processBoardFromScanner(scan);
        } catch (Exception e) {
            return Result.err(e.getMessage());
        }
    }

    private static Result<SquareBoard, String> processBoardFromScanner(Scanner scan) {
        var builder = new SquareBoard.ValidatingBoardBuilder();
        while (scan.hasNextLine()) {
            builder.row(scan.nextLine());
        }
        return builder.build();
    }

    /**
     * Returns an {@link Result.Ok} instance containing two positive
     * integers (if {@code inputText} matches the sentinel value, then returns
     * a pair where one or both values are negative -- signaling halt/quit).
     * NOTE/update: would probably be better not doing this sentinel stuff --
     * just do it in the originating loop (this method does enough work as it is).
     * <p>
     * Returns an {@link Result.Err} instance in the event that
     * {@code inputText} is malformed.
     */
    private static Result<Pair<Integer, Integer>, String> parseInputText(
            String inputText) {
        var parts = inputText.trim().split(",");

        if (parts.length != 2) {
            return Result.err("input must be in the format 'row,col' (no spaces)");
        }
        try {
            var parsedFirst = Integer.parseInt(parts[0].trim());
            var parsedSecond = Integer.parseInt(parts[1].trim());
            return switch (Pair.of(parsedFirst, parsedSecond)) {
                case Pair(var row, var col) when inBounds(row, col) -> Result.ok(Pair.of(row, col));
                default -> Result.err("row and column must be between 1 and 4 (inclusive)");
            };
        } catch (NumberFormatException e) {
            return Result.err("row and column must be integers");
        }
    }

    public static boolean inBounds(int row, int col) {
        return row >= 1 && row <= 4 && col >= 1 && col <= 4;
    }

    /**
     * A small one off type to encapsulate the example number with board
     * that corresponds to that example number.
     */
    public record ExampleBoard(int num, String exampleBoardText) {
    }
}
