# CLI Minesweeper-2030

In this warm up programming assignment you will practice test driven development (using jUnit) in addition to object 
modeling and capturing the design of your program using UML class diagrams.

You will be implementing a basic command-line-interface (CLI) minesweeper game. This is a single player game that's played on NxN grid, where some number of squares have an `X` denoting the position of a mine. On every turn, the player selects a square on the grid and one of two things happens:
* if the square they selected is a mine, they lose immediately
* if the square they selected is not a mine and at least one of the squares adjacent to it (including diagonally) is a mine, the square reveals the number of adjacent mines. This number remains displayed in that grid square until the end of the game.

The player wins when they have revealed all squares that are **not** mines.

## Step 1

Come up with a data structure to store the state of the game and print it to the console. In fact -- consider printing the board an initial unit test. 

Create a 4x4 board and place mines in the first two places in third column.

Print the board to the console, representing an empty square with a dash `-` and a mine with a `*`. Your output should look like the image shown.




