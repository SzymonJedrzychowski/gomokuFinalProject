package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

public class GameEnvironment {
    private int boardSize;
    private int[][] gameBoard;
    private int currentPlayer;

    GameEnvironment(int boardSize) {
        this.boardSize = boardSize;
        this.gameBoard = new int[boardSize][boardSize];
        resetState();
    }

    public void resetState() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                gameBoard[row][col] = 0;
            }
        }

        currentPlayer = 1;
    }

    public void move(int move) throws Exception {
        if (move < 0 || move > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoard[(int) move / boardSize][(int) move % boardSize] != 0) {
            throw new Exception("Already occupied space.");
        }
        gameBoard[(int) move / boardSize][(int) move % boardSize] = currentPlayer;
        currentPlayer *= -1;
    }

    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] == 0) {
                    result.add(row * boardSize + col);
                }
            }
        }
        return result;
    }

    public HashMap<Integer, Integer> ifTerminal() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);
        int currentScore;
        int currentPlayerScore;
        int maxScore = 0;
        int maxScorePlayer = 0;

        // HORIZONTAL
        for (int row = 0; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            for (int col = 0; col < boardSize; col++) {
                if (currentPlayerScore == gameBoard[row][col] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row][col];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[row][col] && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        // VERTICAL
        for (int col = 0; col < boardSize; col++) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            for (int row = 0; row < boardSize; row++) {
                if (currentPlayerScore == gameBoard[row][col] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row][col];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[row][col] && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        // DIAGONAL (L-R)
        for (int col = 1; col < boardSize; col++) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            if ((boardSize - col) - 5 < 0) {
                break;
            }
            for (int rowL = 0; rowL < boardSize - col; rowL++) {
                if (currentPlayerScore == gameBoard[rowL][col + rowL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[rowL][col + rowL] && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        for (int row = 0; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            if ((boardSize - row) - 5 < 0) {
                break;
            }
            for (int colL = 0; colL < boardSize - row; colL++) {
                if (currentPlayerScore == gameBoard[row + colL][colL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[row + colL][colL] && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        // DIAGONAL (R-L)
        for (int col = boardSize - 1; col >= 0; col--) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            if (col - 4 < 0) {
                break;
            }
            for (int rowL = 0; rowL <= col; rowL++) {
                if (currentPlayerScore == gameBoard[rowL][col - rowL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[rowL][col - rowL] && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        for (int row = 1; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;
            maxScore = 0;
            maxScorePlayer = 0;
            if ((boardSize - row) - 5 < 0) {
                break;
            }
            for (int colL = boardSize - 1; colL >= row; colL--) {
                if (currentPlayerScore == gameBoard[row + boardSize - 1 - colL][colL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore += 1;
                } else if (currentPlayerScore != gameBoard[row + boardSize - 1 - colL][colL]
                        && currentPlayerScore != 0) {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore = 1;
                } else {
                    if (currentScore > maxScore) {
                        maxScorePlayer = currentPlayerScore;
                        maxScore = currentScore;
                    }
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore = 1;
                }
            }

            if (currentScore == 5){
                result.put(0, 1);
                result.put(1, currentPlayerScore);
                return result;
            }
            else if (maxScore == 5) {
                result.put(0, 1);
                result.put(1, maxScorePlayer);
                return result;
            }
        }

        boolean isZero = false;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] == 0) {
                    return result;
                }
            }
        }
        if (!isZero) {
            result.put(0, 1);
        }

        return result;

    }

    public void printBoard() {
        System.out.printf("%n");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoard[row][col] == 0) {
                    System.out.printf("_ ");
                } else if (gameBoard[row][col] == 1) {
                    System.out.printf("O ");
                } else {
                    System.out.printf("X ");
                }
            }
            System.out.printf("%n");
        }
        System.out.printf("%n");
    }

    public GameEnvironment copy() {
        GameEnvironment newGame = new GameEnvironment(boardSize);
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                newGame.gameBoard[row][col] = gameBoard[row][col];
            }
        }
        newGame.currentPlayer = currentPlayer;
        return newGame;
    }

    public int[][] getGameBoard() {
        return gameBoard;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
}
