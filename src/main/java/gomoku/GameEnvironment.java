package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Class responsible for managing the game Gomoku.
 * 
 * @author Szymon Jedrzychowski
 */
public class GameEnvironment {

    private final int boardSize; // Size of one board side
    private int[][] gameBoard; // Array of stones on board
    private int currentPlayer; // Player to move
    private int moveCount; // Number of moves played in the game

    private long[][][] hashArray; // Array with hash values for both players
    private long hash; // Current hash

    private final int[] scoreTable = { 0, 0, 1, 10, 50, 50000 }; // Table of scores for evaluation

    /**
     * @param boardSize size of the board to create
     * @throws Exception if incorrect board size was provided
     */
    GameEnvironment(int boardSize) throws Exception {
        this.boardSize = boardSize;

        if (boardSize % 2 == 0) {
            throw new Exception("BoardSize needs to be odd.");
        } else if (boardSize < 7) {
            throw new Exception("Size of the board needs to be at least 7.");
        }

        this.gameBoard = new int[boardSize][boardSize];
        moveCount = 0;
        currentPlayer = 1;
    }

    /**
     * Method used to reset the board and variables so that new game can be played.
     */
    public void resetState() {
        this.gameBoard = new int[boardSize][boardSize];
        moveCount = 0;
        currentPlayer = 1;
    }

    /**
     * Method used to place a stone on the board.
     * 
     * @param boardIndex space on the board that new move is played
     * @throws Exception if the move is out of board or played on already taken
     *                   space
     */
    public void move(int boardIndex) throws Exception {
        if (boardIndex < 0 || boardIndex > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoard[boardIndex / boardSize][boardIndex % boardSize] != 0) {
            throw new Exception("Already occupied space.");
        }

        gameBoard[boardIndex / boardSize][boardIndex % boardSize] = currentPlayer;
        currentPlayer *= -1; // Change the player to the opponent
        moveCount += 1;
    }

    /**
     * Method used to remove a stone from the board.
     * 
     * @param boardIndex space on the board that is cleared of stones
     * @throws Exception if the move is out of board or empty space
     */
    public void undoMove(int boardIndex) throws Exception {
        if (boardIndex < 0 || boardIndex > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoard[boardIndex / boardSize][boardIndex % boardSize] == 0) {
            throw new Exception("This space is empty.");
        }

        currentPlayer *= -1;
        moveCount -= 1;
        gameBoard[boardIndex / boardSize][boardIndex % boardSize] = 0;
    }

    /**
     * Method used to get possible moves for play.
     * 
     * @param closeMovesOnly true if only adjacent moves should be returned.
     * @return possible moves
     */
    public ArrayList<Integer> getLegalMoves(boolean closeMovesOnly) {
        if (closeMovesOnly == false) {
            ArrayList<Integer> legalMoves = new ArrayList<>();
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    if (gameBoard[row][column] == 0) {
                        legalMoves.add(row * boardSize + column);
                    }
                }
            }

            Collections.shuffle(legalMoves); // Shuffle to introduce randomness
            return legalMoves;
        } else {
            return getCloseMoves(); // Run different method to return adjacent moves only
        }
    }

    /**
     * Method used to get only adjacent moves.
     * 
     * @return possible close moves
     */
    private ArrayList<Integer> getCloseMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] != 0)
                    continue;

                // Check west
                if (col > 0) {
                    if (gameBoard[row][col - 1] != 0) {
                        result.add(row * boardSize + col);
                        continue;
                    }
                }

                // Check east
                if (col < boardSize - 1) {
                    if (gameBoard[row][col + 1] != 0) {
                        result.add(row * boardSize + col);
                        continue;
                    }
                }

                if (row > 0) {
                    // Check north-west
                    if (col > 0) {
                        if (gameBoard[row - 1][col - 1] != 0) {
                            result.add(row * boardSize + col);
                            continue;
                        }
                    }
                    // Check north-east
                    if (col < boardSize - 1) {
                        if (gameBoard[row - 1][col + 1] != 0) {
                            result.add(row * boardSize + col);
                            continue;
                        }
                    }
                    // Check north
                    if (gameBoard[row - 1][col] != 0) {
                        result.add(row * boardSize + col);
                        continue;
                    }
                }
                if (row < boardSize - 1) {
                    // Check south-west
                    if (col > 0) {
                        if (gameBoard[row + 1][col - 1] != 0) {
                            result.add(row * boardSize + col);
                            continue;
                        }
                    }
                    // Check south-east
                    if (col < boardSize - 1) {
                        if (gameBoard[row + 1][col + 1] != 0) {
                            result.add(row * boardSize + col);
                            continue;
                        }
                    }
                    // Check south
                    if (gameBoard[row + 1][col] != 0) {
                        result.add(row * boardSize + col);
                    }
                }
            }
        }

        // Add only moves that are not mirrored if board is empty
        if (result.isEmpty() && gameBoard[0][0] == 0) {
            for (int row = 0; row <= boardSize / 2; row++) {
                for (int col = 0; col <= row; col++) {
                    result.add(row * boardSize + col);
                }
            }
        }

        Collections.shuffle(result);
        return result;
    }

    /**
     * Method used to check if game is finished
     * 
     * @return game results HashMap
     *         0 - if game finished (0 - no, 1 - yes)
     *         1 - game winner (or 0 for draw)
     */
    public HashMap<Integer, Integer> ifTerminal() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);

        // Game can only end after 9 moves
        if (moveCount < 9) {
            return result;
        }

        boolean checkResult = checkHorizontal() || checkVertical() || checkDiagonals();

        if (checkResult) {
            result.put(0, 1);
            result.put(1, -currentPlayer); // Set winner if game is finished
            return result;
        } else if (moveCount == boardSize * boardSize) {
            result.put(0, 1); // Set draw if all moves were played
        }

        return result;
    }

    /**
     * Method used to check if 5 stones are connected horizontally.
     * 
     * @return if winning combination was found
     */
    private boolean checkHorizontal() {
        int currentResult = 0; // Store number of stones in a row
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                // If number of stones in a row is 5 and next stone is not the same, return win
                if (currentResult == 5 && gameBoard[row][col] != -currentPlayer) {
                    return true;
                }

                if (gameBoard[row][col] == -currentPlayer) {
                    currentResult += 1;
                } else {
                    currentResult = 0;
                }

                // Stop search if there is not enough spaces to get 5 in a row
                if (currentResult + boardSize - col - 1 < 5) {
                    break;
                }
            }

            // Check last result
            if (currentResult == 5) {
                return true;
            }
            currentResult = 0; // Reset when moving to next row
        }
        return false;
    }

    /**
     * Method used to check if 5 stones are connected vertically.
     * 
     * @return if winning combination was found
     */
    private boolean checkVertical() {
        int currentResult = 0; // Store number of stones in a row
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                // If number of stones in a row is 5 and next stone is not the same, return win
                if (currentResult == 5 && gameBoard[row][col] != -currentPlayer) {
                    return true;
                }

                if (gameBoard[row][col] == -currentPlayer) {
                    currentResult += 1;
                } else {
                    currentResult = 0;
                }

                // Stop search if there is not enough spaces to get 5 in a row
                if (currentResult + boardSize - row - 1 < 5) {
                    break;
                }
            }

            // Check last result
            if (currentResult == 5) {
                return true;
            }
            currentResult = 0; // Reset when moving to next column
        }
        return false;
    }

    /**
     * Method used to check if 5 stones are connected diagonally.
     * 
     * @return if winning combination was found
     */
    private boolean checkDiagonals() {
        int currentResult = 0; // Store number of stones in a row
        int spaceToCheck = boardSize * (boardSize - 5); // Calculate first point to check
        int maxLength = 5; // First diagonal always has length 5 (shortest diagonals are checked first)

        // Left-Up to Right-Down
        while (true) {
            for (int currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                // Calculate row and column of space to check
                int row = spaceToCheck / boardSize + currentPlace;
                int col = spaceToCheck % boardSize + currentPlace;

                // If number of stones in a row is 5 and next stone is not the same, return win
                if (currentResult == 5 & gameBoard[row][col] != -currentPlayer) {
                    return true;
                }

                if (gameBoard[row][col] == -currentPlayer) {
                    currentResult += 1;
                } else {
                    currentResult = 0;
                }

                // Stop search if there is not enough spaces to get 5 in a row
                if (maxLength - currentPlace - 1 + currentResult < 5) {
                    break;
                }
            }

            // Check last result
            if (currentResult == 5) {
                return true;
            }
            currentResult = 0; // Reset when moving to next diagonal

            // Break if the last diagonal was checked
            if (spaceToCheck == boardSize - 5) {
                break;
            }

            // Calculate new point to check
            if (spaceToCheck >= boardSize) {
                spaceToCheck -= boardSize;
                maxLength += 1;
            } else {
                spaceToCheck += 1;
                maxLength -= 1;
            }
        }

        spaceToCheck = 4 * boardSize; // Calculate second checking point
        maxLength = 5; // Reset maxLength

        // Left-Down to Right-Up
        while (true) {
            for (int currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                // Calculate row and column of space to check
                int row = spaceToCheck / boardSize - currentPlace;
                int col = spaceToCheck % boardSize + currentPlace;

                // If number of stones in a row is 5 and next stone is not the same, return win
                if (currentResult == 5 & gameBoard[row][col] != -currentPlayer) {
                    return true;
                }

                if (gameBoard[row][col] == -currentPlayer) {
                    currentResult += 1;
                } else {
                    currentResult = 0;
                }

                // Stop search if there is not enough spaces to get 5 in a row
                if (maxLength - currentPlace - 1 + currentResult < 5) {
                    break;
                }
            }

            // Check last result
            if (currentResult == 5) {
                return true;
            }
            currentResult = 0; // Reset when moving to next diagonal

            // Break if the last diagonal was checked
            if (spaceToCheck == boardSize * boardSize - 5) {
                return false;
            }

            // Calculate new point to check
            if (spaceToCheck < boardSize * (boardSize - 1)) {
                spaceToCheck += boardSize;
                maxLength += 1;
            } else {
                spaceToCheck += 1;
                maxLength -= 1;
            }
        }
    }

    /**
     * Method used to evaluate the board state.
     * 
     * @return evaluation result HashMap
     *         0 - if game finished (0 - no, 1 - yes)
     *         1 - game winner (or 0 for draw)
     *         2 - evaluation score number
     */
    public HashMap<Integer, Integer> evaluateBoard() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);

        int evaluation = evaluateHorizontally() + evaluateVertically() + evaluateDiagonallyNW()
                + evaluateDiagonallySW();
        result.put(2, evaluation);

        // If evaluation score is larger than 25000, game is over (50000 is given for
        // ending combination but 'buffer' is added in case opponent has large
        // evaluation score)
        if (Math.abs(evaluation) > 25000) {
            result.put(0, 1);
            result.put(1, -currentPlayer);
        } else if (moveCount == boardSize * boardSize) {
            result.put(0, 1);
        }
        return result;
    }

    /**
     * Method used to evaluate horizontally.
     * 
     * @return evaluation score
     */
    private int evaluateHorizontally() {
        int evaluationScore = 0;
        for (int row = 0; row < boardSize; row++) {
            int col = 0;
            while (col < boardSize - 4) {
                int result = 1;
                int maxResult = 1;
                int checkPlayer = 0;
                boolean breakLoop = false;
                int lastFilled = -1;

                for (int i = 0; i < 5; i++) {
                    if (checkPlayer == 0) {
                        checkPlayer = gameBoard[row][col + i];
                        lastFilled = i;
                    } else {
                        if (gameBoard[row][col + i] == -checkPlayer) {
                            breakLoop = true; // Break loop if stone is of opposite colour
                            col = col + lastFilled; // Skip columns that would not add points
                            break;
                        } else if (gameBoard[row][col + i] == checkPlayer) {
                            result++;
                            maxResult = Math.max(result, maxResult); // Keep largest number of stones in a row
                            lastFilled = i;
                        } else {
                            result = 0; // Reset score on empty space
                        }
                    }
                }

                if (breakLoop || (col > 0 && gameBoard[row][col - 1] == checkPlayer)
                        || (col + 5 < boardSize && gameBoard[row][col + 5] == checkPlayer)) {
                    col++;
                    continue;
                }

                col++;
                evaluationScore += checkPlayer * scoreTable[maxResult];
            }
        }
        return evaluationScore;
    }

    /**
     * Method used to evaluate vertically.
     * 
     * @return evaluation score
     */
    private int evaluateVertically() {
        int evaluationScore = 0;
        for (int col = 0; col < boardSize; col++) {
            int row = 0;
            while (row < boardSize - 4) {
                int result = 1;
                int maxResult = 1;
                int checkPlayer = 0;
                boolean breakLoop = false;
                int lastFilled = -1;
                for (int i = 0; i < 5; i++) {
                    if (checkPlayer == 0) {
                        checkPlayer = gameBoard[row + i][col];
                        lastFilled = i;
                    } else {
                        if (gameBoard[row + i][col] == -checkPlayer) {
                            breakLoop = true; // Break loop if stone is of opposite colour
                            row = row + lastFilled; // Skip rows that would not add points
                            break;
                        } else if (gameBoard[row + i][col] == checkPlayer) {
                            result++;
                            maxResult = Math.max(result, maxResult); // Keep largest number of stones in a row
                            lastFilled = i;
                        } else {
                            result = 0; // Reset score on empty space
                        }
                    }
                }

                // If space in front or after 5 checked spaces is the same, do not calculate evaluation
                if (breakLoop || (row > 0 && gameBoard[row - 1][col] == checkPlayer)
                        || (row + 5 < boardSize && gameBoard[row + 5][col] == checkPlayer)) {
                    row++;
                    continue;
                }

                row++;
                evaluationScore += checkPlayer * scoreTable[maxResult];
            }
        }
        return evaluationScore;
    }

    /**
     * Method used to evaluate diagonally (north-west to south-east).
     * 
     * @return evaluation score
     */
    private int evaluateDiagonallyNW() {
        int evaluationScore = 0;
        for (int row = 0; row < boardSize; row++) {
            int col = 0;
            while (col < boardSize - 4 && row < boardSize - 4) {
                int result = 1;
                int maxResult = 1;
                int checkPlayer = 0;
                boolean breakLoop = false;
                for (int i = 0; i < 5; i++) {
                    if (checkPlayer == 0) {
                        checkPlayer = gameBoard[row + i][col + i];
                    } else {
                        if (gameBoard[row + i][col + i] == -checkPlayer) {
                            breakLoop = true; // Break loop if stone is of opposite colour
                            break;
                        } else if (gameBoard[row + i][col + i] == checkPlayer) {
                            result += 1;
                            maxResult = Math.max(result, maxResult); // Keep largest number of stones in a row
                        } else {
                            result = 0; // Reset score on empty space
                        }
                    }
                }

                // If space in front or after 5 checked spaces is the same, do not calculate evaluation
                if (breakLoop || ((col > 0 && row > 0) && gameBoard[row - 1][col - 1] == checkPlayer)
                        || ((col + 5 < boardSize && row + 5 < boardSize)
                                && gameBoard[row + 5][col + 5] == checkPlayer)) {
                    col++;
                    continue;
                }

                col++;
                evaluationScore += checkPlayer * scoreTable[maxResult];
            }
        }
        return evaluationScore;
    }

    /**
     * Method used to evaluate diagonally (south-west to north-east).
     * 
     * @return evaluation score
     */
    private int evaluateDiagonallySW() {
        int evaluationScore = 0;
        for (int row = 0; row < boardSize; row++) {
            int col = 0;
            while (col < boardSize - 4 && row > 3) {
                int result = 1;
                int maxResult = 1;
                int checkPlayer = 0;
                boolean breakLoop = false;
                for (int i = 0; i < 5; i++) {
                    if (checkPlayer == 0) {
                        checkPlayer = gameBoard[row - i][col + i];
                    } else {
                        if (gameBoard[row - i][col + i] == -checkPlayer) {
                            breakLoop = true; // Break loop if stone is of opposite colour
                            break;
                        } else if (gameBoard[row - i][col + i] == checkPlayer) {
                            result += 1;
                            maxResult = Math.max(result, maxResult); // Keep largest number of stones in a row
                        } else {
                            result = 0; // Reset score on empty space
                        }
                    }
                }

                // If space in front or after 5 checked spaces is the same, do not calculate evaluation
                if (breakLoop || ((col > 0 && row + 1 < boardSize) && gameBoard[row + 1][col - 1] == checkPlayer)
                        || ((col + 5 < boardSize && row - 5 >= 0) && gameBoard[row - 5][col + 5] == checkPlayer)) {
                    col++;
                    continue;
                }

                col++;
                evaluationScore += checkPlayer * scoreTable[maxResult];
            }
        }
        return evaluationScore;
    }

    /**
     * Method used to print the board in terminal.
     */
    public void printBoard() {
        System.out.printf("%n");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                switch (gameBoard[row][col]) {
                    case 1 ->
                        System.out.printf("O ");
                    case -1 ->
                        System.out.printf("X ");
                    default ->
                        System.out.printf("_ ");
                }

            }
            System.out.printf("%n");
        }
        System.out.printf("%n");
    }

    /**
     * Method used to copy the game environment.
     * 
     * @return copy of game environment
     * @throws Exception if there was problem with creating GameEnvironment
     */
    public GameEnvironment copy() throws Exception {
        GameEnvironment newGame = new GameEnvironment(boardSize);
        newGame.hashArray = hashArray;
        newGame.hash = hash;

        // Deep copy the board
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(gameBoard[i], 0, newGame.gameBoard[i], 0, boardSize);
        }

        newGame.moveCount = moveCount;
        newGame.currentPlayer = currentPlayer;

        return newGame;
    }

    /**
     * Method used to initialise the Zobrist hash.
     */
    public void hashInit() {
        hashArray = new long[2][boardSize][boardSize];
        Random randomGenerator = new Random();

        // Create long values for each space on the board (for both players)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int k = 0; k < boardSize; k++) {
                    hashArray[i][j][k] = randomGenerator.nextLong();
                }
            }
        }
        hash = 0;

        // Based on current state of the board, calculate new hash
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (gameBoard[i][j] == 1) {
                    hash ^= hashArray[0][i][j];
                } else if (gameBoard[i][j] == -1) {
                    hash ^= hashArray[1][i][j];
                }
            }
        }
    }

    /**
     * Method used to updateHash the game state hash.
     * 
     * @param player player that player the move
     * @param space  space on board on which stone was played
     * @return game state hash
     */
    public long updateHash(int player, int space) {
        if (player == 1) {
            return hash ^= hashArray[0][(int) (space / boardSize)][space % boardSize];
        } else {
            return hash ^= hashArray[1][(int) (space / boardSize)][space % boardSize];
        }
    }

    /**
     * Method used to get player to move.
     * 
     * @return player to move
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Method used to get current hash of game state.
     * 
     * @return game state hash
     */
    public long getHash() {
        return hash;
    }

    /**
     * Method used to get the maximum number of spaces on board.
     * 
     * @return maximum number of spaces on board
     */
    public int getBoardSpace() {
        return boardSize * boardSize;
    }
}
