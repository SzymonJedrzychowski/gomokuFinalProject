package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    int[] scoreTable = { 0, 0, 1, 10, 50, 0 };

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
        this.gameBoard = new ArrayList<>();
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

    public ArrayList<Integer> getLegalMoves(boolean getCloseMoves) {
        ArrayList<Integer> legalMoves = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                if (gameBoard.get(row).get(column) == 0) {
                    legalMoves.add(row * boardSize + column);
                }
            }
        }

        if(getCloseMoves && legalMoves.size() != boardSize*boardSize){
            return getCloseMoves();
        }
        Collections.shuffle(legalMoves);
        return legalMoves;
    }

    public ArrayList<Integer> getCloseMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        HashSet<Integer> closeMoves = new HashSet<>();
        HashSet<Integer> playedMoves = new HashSet<>();
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                if (gameBoard.get(row).get(column) != 0) {
                    playedMoves.add(row * boardSize + column);
                }
            }
        }
        
        for(int move:playedMoves){
            //N
            if(move >= boardSize  && !playedMoves.contains(move-boardSize)) closeMoves.add(move-boardSize);
            //NE
            if(move >= boardSize && (move+1)%boardSize != 0 && !playedMoves.contains(move-boardSize+1)) closeMoves.add(move-boardSize+1);
            //E
            if((move+1)%boardSize != 0 && !playedMoves.contains(move+1)) closeMoves.add(move+1);
            //SE
            if((move+1)%boardSize != 0 && (move+boardSize+1)<boardSize*boardSize && !playedMoves.contains(move+boardSize+1)) closeMoves.add(move+boardSize+1);
            //S
            if(move+boardSize+1 < boardSize*boardSize && !playedMoves.contains(move+boardSize)) closeMoves.add(move+boardSize);
            //SW
            if(move+boardSize+1 < boardSize*boardSize && move%boardSize != 0 && !playedMoves.contains(move+boardSize-1)) closeMoves.add(move+boardSize-1);
            //W
            if(move%boardSize != 0 && !playedMoves.contains(move-1)) closeMoves.add(move-1);
            //NW
            if(move%boardSize != 0 && move >= boardSize && !playedMoves.contains(move-boardSize-1)) closeMoves.add(move-boardSize-1);
        }
        result.addAll(closeMoves);
        Collections.shuffle(result);
        return result;
    }

    public HashMap<Integer, Integer> ifTerminal() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);
        if (moveCount < 9) {
            return result;
        }
        result = checkBoards();
        if (result.get(0) == 1) {
            return result;
        } else if (moveCount == boardSize * boardSize) {
            result.put(0, 1);
        }
        return result;
    }

    public boolean checkRight(int row, int col, int checkPlayer) {
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row).get(col + i) != checkPlayer) {
                return false;
            }
        }
        if (col > 0) {
            if (gameBoard.get(row).get(col - 1) == checkPlayer) {
                return false;
            }
        }
        if (col + 5 < boardSize) {
            if (gameBoard.get(row).get(col + 5) == checkPlayer) {
                return false;
            }
        }
        return true;
    }

    public boolean checkDown(int row, int col, int checkPlayer) {
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row + i).get(col) != checkPlayer) {
                return false;
            }
        }
        if (row > 0) {
            if (gameBoard.get(row - 1).get(col) == checkPlayer) {
                return false;
            }
        }
        if (row + 5 < boardSize) {
            if (gameBoard.get(row + 5).get(col) == checkPlayer) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRightBottom(int row, int col, int checkPlayer) {
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row + i).get(col + i) != checkPlayer) {
                return false;
            }
        }
        if (col > 0 && row > 0) {
            if (gameBoard.get(row - 1).get(col - 1) == checkPlayer) {
                return false;
            }
        }
        if (col + 5 < boardSize && row + 5 < boardSize) {
            if (gameBoard.get(row + 5).get(col + 5) == checkPlayer) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRightUpward(int row, int col, int checkPlayer) {
        for (int i = 0; i < 5; i++) {
            if (gameBoard.get(row - i).get(col + i) != checkPlayer) {
                return false;
            }
        }
        if (col > 0 && row + 1 < boardSize) {
            if (gameBoard.get(row + 1).get(col - 1) == checkPlayer) {
                return false;
            }
        }
        if (col + 5 < boardSize && row - 5 > 0) {
            if (gameBoard.get(row - 5).get(col + 5) == checkPlayer) {
                return false;
            }
        }
        return true;
    }

    public int right(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard.get(row).get(col + i);
            } else {
                if (gameBoard.get(row).get(col + i) == -checkPlayer) {
                    return 0;
                } else if (gameBoard.get(row).get(col + i) == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (checkPlayer == 0)
            return 0;
        if (col > 0) {
            if (gameBoard.get(row).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize) {
            if (gameBoard.get(row).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    public int down(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard.get(row + i).get(col);
            } else {
                if (gameBoard.get(row + i).get(col) == -checkPlayer) {
                    return 0;
                } else if (gameBoard.get(row + i).get(col) == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (row > 0) {
            if (gameBoard.get(row - 1).get(col) == checkPlayer) {
                return 0;
            }
        }
        if (row + 5 < boardSize) {
            if (gameBoard.get(row + 5).get(col) == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    public int rightBottom(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard.get(row + i).get(col + i);
            } else {
                if (gameBoard.get(row + i).get(col + i) == -checkPlayer) {
                    return 0;
                } else if (gameBoard.get(row + i).get(col + i) == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (col > 0 && row > 0) {
            if (gameBoard.get(row - 1).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize && row + 5 < boardSize) {
            if (gameBoard.get(row + 5).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    public int rightUpward(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard.get(row - i).get(col + i);
            } else {
                if (gameBoard.get(row - i).get(col + i) == -checkPlayer) {
                    return 0;
                } else if (gameBoard.get(row - i).get(col + i) == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (col > 0 && row + 1 < boardSize) {
            if (gameBoard.get(row + 1).get(col - 1) == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize && row - 5 > 0) {
            if (gameBoard.get(row - 5).get(col + 5) == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
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
                    if (checkRight(row, col, checkPlayer)) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (row + 4 < boardSize) {
                    if (checkDown(row, col, checkPlayer)) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (col + 4 < boardSize && row + 4 < boardSize) {
                    if (checkRightBottom(row, col, checkPlayer)) {
                        results.put(0, 1);
                        results.put(1, checkPlayer);
                        return results;
                    }
                }
                if (col + 4 < boardSize && row - 4 >= 0) {
                    if (checkRightUpward(row, col, checkPlayer)) {
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
        int result = 0;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (col + 4 < boardSize) {
                    result += right(row, col);
                }
                if (row + 4 < boardSize) {
                    result += down(row, col);
                }
                if (col + 4 < boardSize && row + 4 < boardSize) {
                    result += rightBottom(row, col);
                }
                if (col + 4 < boardSize && row - 4 >= 0) {
                    result += rightUpward(row, col);
                }
            }
        }
        return result;
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
