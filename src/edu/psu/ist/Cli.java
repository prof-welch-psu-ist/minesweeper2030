package edu.psu.ist;

import java.util.Scanner;

public final class Cli {

    public static final String Banner = String.format("Minesweeper 2030 (version: %s)", Version.Current);
    public static void main(String[] args) {

        System.out.println(Cli.Banner);

        var scanner = new Scanner(System.in);
        var builder = new SquareBoard.ValidatingBoardBuilder();

        while (true) {
            System.out.println("enter a square board (no spaces) one row at a time");
            var line = scanner.nextLine();
            //builder.row(line.chars().mapToObj(c -> (char) c).toList());
            //builder.row(scanner.nextLine());
            break;
        }
    }

}
