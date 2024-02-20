# Chest_JAVA

# Chess Game in Java

This project is a console-based chess game implemented in Java. It allows two players to play chess on a single computer, with moves entered through the console. The game supports all the basic rules of chess, including castling, pawn promotion, and checks.

## Features

- Full chess game logic including special moves like castling and pawn promotion.
- Check and checkmate detection.
- A simple console-based UI to play the game.
- Highlights possible moves for a selected piece.

## Common Exceptions

During gameplay, you may encounter several exceptions designed to guide you through the correct use of the game. Here are some examples:

- **Invalid Position Exception**: If you try to move a piece to a position that is not on the board, the game will throw an exception with the message: "Error reading ChessPosition. Valid values are from a1 to h8".
- **Check Exception**: If a move puts or leaves your king in check, the game will prevent it and display: "You can't put yourself in check".

These exceptions are in place to ensure the game follows the official rules of chess.

## Game UI Examples
### Exceptions:
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/38c94311-2d10-4c2c-a303-297410a51cfd)
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/09a80cd1-575b-469c-86bc-1bfe8157bd24)

### GAMEPLAY:
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/355b1a5e-c64a-41ef-8637-8e05e2d9cbc0)
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/f0c998b9-3494-47d4-9f46-81d772ca39dd)
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/851f6a58-2b11-4efa-beb2-221e436db675)

### Check-Mate:
![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/1bcd7faa-9fa8-4136-bf49-1cccdf1fbfd9)

-------
### Starting the Game

When you start the game, the console will display the chessboard with all pieces in their initial positions:

![image](https://github.com/BrunoLopes24/Chess_JAVA/assets/117863700/ce33452b-2afe-4de4-8c8c-62a0541bb64e)

## How to Run

To run the game, you will need Java installed on your computer. Follow these steps:

1. Clone the repository to your local machine.
2. Navigate to the project directory in your terminal.
3. Compile the project using the Java compiler:
   ```shell
   javac Application/Main.java
Run the compiled game using Java:
java Application/Main
How to Play
The game starts automatically after running the Main class.
Players take turns to input their moves in the format [letter][number], for example, e2 to e4.
The game will highlight possible moves for the selected piece.
To perform special moves like pawn promotion, follow the on-screen instructions.
Contributions
Contributions to this project are welcome. Please feel free to fork the repository, make changes, and submit a pull request.

License
This project is open-source and available under the MIT License.
