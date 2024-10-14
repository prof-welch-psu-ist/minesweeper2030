package edu.psu.ist;

import java.util.Scanner;

public final class Cli {

    public static final String Banner = String.format("Minesweeper 2030 (version: %s)", Version.Current);
    public static void main(String[] args) {

        var scanner = new Scanner(System.in);
        var builder = new SquareBoard.ValidatingBoardBuilder();

        while (true) {
            System.out.println("enter a square board (no spaces) one row at a time");

            //builder.row(scanner.nextLine());
            break;
        }
    }

}
