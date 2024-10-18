package edu.psu.ist;

import edu.psu.ist.immutableadts.Pair;
import edu.psu.ist.immutableadts.Result;
import io.vavr.collection.Vector;

import java.io.FileNotFoundException;
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

        var scanner = new Scanner(System.in);
    }

    private static void doExampleBoard(ExampleBoard ex) {
        System.out.println("Loading example board: " + ex.num + "...");
        var initialBoard = loadFromString(ex.exampleBoardText);
        switch (initialBoard) {
            case Result.Ok(var b) -> {
                System.out.println(b.toString());
                MinesweeperGame g = new MinesweeperGame(b);
            }
            case Result.Err(var err) -> System.out.println(err);
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
     * <em>syntactically</em> invalid
     */
    public static Result<SquareBoard, String> loadFromFile(Path p) {// not used atm
        try (var scan = new Scanner(p.toFile())) {
            return processBoardFromScanner(scan);
        } catch (FileNotFoundException e) {
            return Result.err(e.getMessage());
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

/*    private void doLoop(Scanner scan, SquareBoard b) {
        System.out.println("enter a row,col number (1-indexed, ex: 1,4) - type q to quit");
        var rawInput = scan.nextLine();
        var parsedInput = parseInputText(rawInput);

        switch (parsedInput) {
            // case 0a: the cell is valid, uncover it on the board
            case Validation.Success(Pair(var row, var col)) -> {
                // note: - 1 to adjust for the public-facing CLI indexing
                var updatedBoardVal = reveal(b, row - 1, col - 1);
                switch (updatedBoardVal) {
                    // case 1: the board is valid and all moves thus far have been valid,
                    //          so keep looping (revealing squares)
                    case Validation.Success(var nextBoard) -> doLoop(scan, nextBoard);

                    // case 2: board is in a winning configuration, so return
                    case Validation.SoftFailure(var finalBoard, var msgs) when isWin(msgs) ->
                            Validation.softFail(finalBoard, GameMessage.WinMessage.Instance);

                    // case 3: board is in a loss configuration
                    case Validation.SoftFailure(var finalBoard, var msgs) when isLoss(msgs) ->
                            Validation.softFail(finalBoard, GameMessage.LoseMessage.Instance);

                    // case 4: bad moves that resulted in failing boards
                    case Validation.SoftFailure(_, var msgs) -> Validation.hardFail(msgs);
                    case Validation.HardFailure(var errs) -> Validation.hardFail(errs);
                }
            }
            // case 0b: board is in a bad config report errors
            case Validation.SoftFailure(_, var errs) -> Validation.hardFail(errs);
            case Validation.HardFailure(var err) -> Validation.hardFail(err);
        }
    }*/

    private Result<Pair<Integer, Integer>, String> parseInputText(String inputText) {
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
