package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameEnvironment {

    private int boardSize;
    private ArrayList<ArrayList<Integer>> gameBoard;
    private int currentPlayer;
    private long[][][] hashArray;
    private long hash;
    private boolean useGraphicalInterface;
    GraphicsBoard graphicsBoard;
    int moveCount;

    GameEnvironment(int boardSize, boolean useGraphicalInterface) {
        this.boardSize = boardSize;
        this.gameBoard = new ArrayList<>();
        this.useGraphicalInterface = useGraphicalInterface;
        resetState();
        if (useGraphicalInterface) {
            graphicsBoard = new GraphicsBoard(boardSize);
        }
    }

    public void resetState() {
        for (int i = 0; i < boardSize; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < boardSize; j++) {
                temp.add(0);
            }
            this.gameBoard.add(temp);
        }
        moveCount = 0;
        currentPlayer = 1;
    }

    public void move(int move) throws Exception {
        if (move < 0 || move > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoard.get(move / boardSize).get(move % boardSize) != 0) {
            throw new Exception("Already occupied space.");
        }
        gameBoard.get(move / boardSize).set(move % boardSize, currentPlayer);
        if (useGraphicalInterface) {
            graphicsBoard.makeMove(gameBoard);
        }
        currentPlayer *= -1;
        moveCount += 1;
    }

    public void undoMove(int move) {
        currentPlayer *= -1;
        gameBoard.get(move / boardSize).set(move % boardSize, 0);
        moveCount -= 1;
    }

    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                if (gameBoard.get(row).get(column) == 0) {
                    result.add(row * boardSize + column);
                }
            }
        }
        return result;
    }

    public HashMap<Integer, Integer> ifTerminal() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);

        if (moveCount == boardSize * boardSize) {
            result.put(0, 1);
        } else if (moveCount < 9) {
            return result;
        }

        return checkBoards();
    }

    public int right(int row, int col, int checkPlayer) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row).get(col + i) == -checkPlayer) {
                return 0;
            } else if (gameBoard.get(row).get(col + i) == checkPlayer) {
                result += 1;
            }
        }
        if (col > 0) {
            if (gameBoard.get(row).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col+5 < boardSize) {
            if (gameBoard.get(row).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return result;
    }

    public int down(int row, int col, int checkPlayer) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row + i).get(col) == -checkPlayer) {
                return 0;
            } else if (gameBoard.get(row + i).get(col) == checkPlayer) {
                result += 1;
            }
        }
        if (row > 0) {
            if (gameBoard.get(row - 1).get(col) == checkPlayer) {
                return 0;
            }
        }
        if (row+5 < boardSize) {
            if (gameBoard.get(row + 5).get(col) == checkPlayer) {
                return 0;
            }
        }
        return result;
    }

    public int rightBottom(int row, int col, int checkPlayer) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row + i).get(col + i) == -checkPlayer) {
                return 0;
            } else if (gameBoard.get(row + i).get(col + i) == checkPlayer) {
                result += 1;
            }
        }
        if (col > 0 && row > 0) {
            if (gameBoard.get(row - 1).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col+5 < boardSize && row+5 < boardSize) {
            if (gameBoard.get(row + 5).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return result;
    }

    public int rightUpward(int row, int col, int checkPlayer) {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row - i).get(col + i) != checkPlayer) {
                return 0;
            } else if (gameBoard.get(row - i).get(col + i) != checkPlayer) {
                result = +1;
            }
        }
        if (col>0 && row+1 < boardSize) {
            if (gameBoard.get(row + 1).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col+5 < boardSize && row-5 > 0) {
            if (gameBoard.get(row - 5).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return result;
    }

    public HashMap<Integer, Integer> checkBoards() {
        int checkPlayer;
        HashMap<Integer, Integer> results = new HashMap<>();
        results.put(0, 0);
        results.put(1, 0);
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                checkPlayer = gameBoard.get(row).get(col);
                if (checkPlayer == 0) {
                    continue;
                }
                if (col + 4 < boardSize) {
                    if (right(row, col, checkPlayer) == 5) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (row + 4 < boardSize) {
                    if (down(row, col, checkPlayer) == 5) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (col + 4 < boardSize && row + 4 < boardSize) {
                    if (rightBottom(row, col, checkPlayer) == 5) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (col + 4 < boardSize && row - 4 >= 0) {
                    if (rightUpward(row, col, checkPlayer) == 5) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
            }
        }
        return results;
    }

    public int evaluateBoard() {
        int[] results = new int[2];
        int[] scoreTable = {0,0,1,10,50,0};
        int tempResult;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (col + 4 < boardSize) {
                    tempResult = right(row, col, 1);
                    results[0] += scoreTable[tempResult];
                    tempResult = right(row, col, -1);
                    results[1] += scoreTable[tempResult];
                }
                if (row + 4 < boardSize) {
                    tempResult = down(row, col, 1);
                    results[0] += scoreTable[tempResult];
                    tempResult = down(row, col, -1);
                    results[1] += scoreTable[tempResult];
                }
                if (col + 4 < boardSize && row + 4 < boardSize) {
                    tempResult = rightBottom(row, col, 1);
                    results[0] += scoreTable[tempResult];
                    tempResult = rightBottom(row, col, -1);
                    results[1] += scoreTable[tempResult];
                }
                if (col + 4 < boardSize && row - 4 >= 0) {
                    tempResult = rightUpward(row, col, 1);
                    results[0] += scoreTable[tempResult];
                    tempResult = rightUpward(row, col, -1);
                    results[1] += scoreTable[tempResult];
                }
            }
        }
        if (currentPlayer == 1) {
            return results[1] - results[0];
        } else {
            return results[0] - results[1];
        }
    }

    public void printBoard() {
        System.out.printf("%n");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                switch (gameBoard.get(row).get(col)) {
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

    public GameEnvironment copy() {
        GameEnvironment newGame = new GameEnvironment(boardSize, false);
        newGame.hashArray = hashArray;
        newGame.hash = hash;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                newGame.gameBoard.get(row).set(col, gameBoard.get(row).get(col));
            }
        }
        newGame.moveCount = moveCount;
        newGame.currentPlayer = currentPlayer;
        return newGame;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return gameBoard;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public long getHash() {
        return hash;
    }

    public void hashInit() {
        hashArray = new long[2][boardSize][boardSize];
        Random randomGenerator = new Random();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int k = 0; k < boardSize; k++) {
                    hashArray[i][j][k] = randomGenerator.nextLong();
                }
            }
        }
        hash = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (gameBoard.get(i).get(j) == 1) {
                    hash ^= hashArray[0][i][j];
                } else if (gameBoard.get(i).get(j) == -1) {
                    hash ^= hashArray[1][i][j];
                }
            }
        }
    }

    public long update(int player, int space) {
        if (player == 1) {
            return hash ^= hashArray[0][(int) (space / boardSize)][space % boardSize];
        } else {
            return hash ^= hashArray[1][(int) (space / boardSize)][space % boardSize];
        }
    }
}
