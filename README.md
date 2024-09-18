# CLI Minesweeper-2030

In this assignment you will practice test driven development (using jUnit) in addition to object 
modeling and capturing the design of your program using UML class diagrams.

You will be implementing a basic command-line-interface (CLI) minesweeper game. 
This is a single player game that's played on NxN grid, where some number of squares within the  
grid are marked as "mines."

On every turn, the player selects a square on the grid and one of two things should happen:
* if the square the player selected is a mine, they lose immediately
* if the square the player selected is not a mine and at least one of the squares adjacent to it (including diagonally) is a mine, the square reveals the number of adjacent mines. This number remains displayed in that grid square until the end of the game.

In our version, we will restrict ourselves to 4x4 grids only. Normally too, the mines are 
kept secret from the player, though in this assignment, we'll leave them revealed for simplicity.

The player wins when they have revealed all squares that are **not** mines.

> **CHALLENGE:** Do as much of this as you can without LLM assistance (get to the highest step you can)
> 
> **CHALLENGE++:** Do this without LLM assistance in 25 mins max, stop when you hit the limit 
> (start a timer) -- which step did you get to?

## Step 1: object modeling

Come up with a data structure to store the state of the game (the board).

Create a 4x4 board and place mines in the first two places in third column.

Have the ability to render the board in string form. In the string, 
represent an empty square with a dash `-` and a mine with a `*`. 
Your output should look like the image shown.

<img src="img/step1.png" alt="step1grid" width="250"/>

A good initial unit test would be to ensure that your board rendering logic is working 
before proceeding.

## Step 2: revealing squares

Now create a function to reveal a square on the board. 

This function should take two inputs, a row and a column, and reveal the
selected square using the rules of Minesweeper:
- **if the selection is a mine**, print the board followed by the text "you lose".
- **if the selection is not a mine**, change that square to display the number of 
mines adjacent to it (including diagonally).

Test your function by revealing some number of squares. The picture below has revealed
the first, third, and fourth squares in the second column (your output should 
look like the image shown):

<img src="img/step2.png" alt="step2grid" width="250"/>

## Step 3: user input

Now make it such that your game can take user input (use the good ol' `Scanner` type for this).

Prompt the user to enter a (1-indexed) row and column, separated by a comma
(e.g., `2,3` for the second row and third column).

If the user's input is invalid, prompt them for a new one.

Otherwise, reveal the square using the function from the previous step, print the board,
and either tell the player they lost or prompt them for another input.

Here's a useful test case that makes the following selections:
* 1,1
* 2,4
* 2,3

Your tests (among other that you think up) should check that 
your board matches the picture below:

<img src="img/step3.png" alt="step3grid" width="250"/>

## Step 4: adding a win check

After a player's move, check whether every square that is *not* a 
mine has been revealed. If so, print "you win" after the player's
move and do not prompt them for another move.

Write additional tests that cover the board with mines except two 
squares `(2,3)` and `(1,1)`, then select those two squares by entering
`2,3` and `1,1`. Your board should match the image below:

<img src="img/step4.png" alt="step4grid" width="250"/>

## Step 5: reveal

When a player reveals a square with a `0` (that is, with no adjacent mines),
automatically reveal all squares adjacent to the original square. Repeat this 
process for any of the adjacent squares that also have no adjacent mines, until
all `0`s in a contiguous region (and all cells adjacent to one of those `0`s) 
are revealed.

> note: this process should never uncover a mine, so it should never result in a player losing

Write unit tests for this. One potential good one involves returning to the 
original board (4x4 with mines in the first two squares and third column), 
then entering `4,1` should cause your board to look as it does below:

<img src="img/step5.png" alt="step5grid" width="250"/>

## UML + 1/2-3/4pg reflection

Accompany your submission with a UML class diagram documenting the design of your 
system. 

The UML can actually be included in your reflection -- which should document your 
thought process in coming with the data structure for the board, design considerations,
and generally on the maintainability and extensibility of the code you ended up with. 
If you attempted the challenges listed at the top of this readme, say which step you 
got to within the limit and the biggest bottleneck. 

# Handin

When you are ready to submit (or simply want to 'checkin' your work for the day), open the terminal, 
cd to the project directory, then make a commit by typing:

> git commit -am "message goes here"

then follow this up with a

> git push origin main
