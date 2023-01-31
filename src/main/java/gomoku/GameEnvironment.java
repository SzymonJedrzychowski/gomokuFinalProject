package gomoku;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class GameEnvironment {
    private int boardSize;
    private BitSet gameBoardOne;
    private BitSet gameBoardTwo;
    private int currentPlayer;
    private long[][][] hashArray;
    private long hash;
    private boolean useGraphicalInterface;
    GraphicsBoard graphicsBoard;

    GameEnvironment(int boardSize, boolean useGraphicalInterface) {
        this.boardSize = boardSize;
        this.gameBoardOne = new BitSet(boardSize * boardSize);
        this.gameBoardTwo = new BitSet(boardSize * boardSize);
        this.useGraphicalInterface = useGraphicalInterface;
        resetState();
        if (useGraphicalInterface) {
            graphicsBoard = new GraphicsBoard(boardSize);
        }
    }

    public void resetState() {
        gameBoardOne.clear();
        gameBoardTwo.clear();
        currentPlayer = 1;
    }

    public void move(int move) throws Exception {
        if (move < 0 || move > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoardOne.get(move) || gameBoardTwo.get(move)) {
            throw new Exception("Already occupied space.");
        }
        if (currentPlayer == 1) {
            gameBoardOne.set(move);
        } else {
            gameBoardTwo.set(move);
        }
        if (useGraphicalInterface) {
            graphicsBoard.makeMove(gameBoardOne, gameBoardTwo);
        }
        currentPlayer *= -1;
    }

    public void undoMove(int move) {
        currentPlayer *= -1;
        if (currentPlayer == 1) {
            gameBoardOne.set(move, false);
        } else {
            gameBoardTwo.set(move, false);
        }
    }

    public ArrayList<Integer> getLegalMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        BitSet c = (BitSet) gameBoardOne.clone();
        c.or(gameBoardTwo);
        c.flip(0, boardSize * boardSize);
        for (int i = 0; i < boardSize * boardSize; i++) {
            if (c.get(i)) {
                result.add(i);
            }
        }
        return result;
    }

    public HashMap<Integer, Integer> ifTerminal() {
        HashMap<Integer, Integer> result = new HashMap<>();
        result.put(0, 0);
        result.put(1, 0);

        if (gameBoardOne.cardinality() + gameBoardTwo.cardinality() == boardSize * boardSize) {
            result.put(0, 1);
        } else if (gameBoardOne.cardinality() < 5) {
            return result;
        }

        if (currentPlayer == -1) {
            if (checkBoards(gameBoardOne)) {
                result.put(0, 1);
                result.put(1, 1);
            }
        } else {
            if (checkBoards(gameBoardTwo)) {
                result.put(0, 1);
                result.put(1, -1);
            }
        }

        return result;

    }

    public boolean checkBoards(BitSet gameBoard) {
        BitSet temp = new BitSet(5);
        boolean over;

        // HORIZONTAL
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize - 4; col++) {
                if (gameBoard.get(row * boardSize + col, row * boardSize + col + 5).cardinality() == 5) {
                    over = false;
                    if (col > 0) {
                        over = gameBoard.get(row * boardSize + col - 1);
                    }
                    if (col < boardSize - 5) {
                        over = over || gameBoard.get(row * boardSize + col + 5);
                    }
                    if (!over) {
                        return true;
                    }
                }
            }
        }

        // VERTICAL
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize - 4; row++) {
                temp.set(0, gameBoard.get(col + row * boardSize));
                temp.set(1, gameBoard.get(col + (row + 1) * boardSize));
                temp.set(2, gameBoard.get(col + (row + 2) * boardSize));
                temp.set(3, gameBoard.get(col + (row + 3) * boardSize));
                temp.set(4, gameBoard.get(col + (row + 4) * boardSize));
                if (temp.cardinality() == 5) {
                    over = false;
                    if (row > 0) {
                        over = gameBoard.get(col + (row - 1) * boardSize);
                    }
                    if (row < boardSize - 5) {
                        over = over || gameBoard.get(col + (row + 5) * boardSize);
                    }
                    if (!over) {
                        return true;
                    }
                }
            }
        }

        // DIAGONAL (L->R)
        for (int col = 0; col < boardSize; col++) {
            if ((boardSize - col) - 5 < 0) {
                break;
            }
            for (int row = 0; row < boardSize - col - 4; row++) {
                temp.set(0, gameBoard.get(col + row * boardSize + row));
                temp.set(1, gameBoard.get(col + (row + 1) * boardSize + 1 + row));
                temp.set(2, gameBoard.get(col + (row + 2) * boardSize + 2 + row));
                temp.set(3, gameBoard.get(col + (row + 3) * boardSize + 3 + row));
                temp.set(4, gameBoard.get(col + (row + 4) * boardSize + 4 + row));
                if (temp.cardinality() == 5) {
                    over = false;
                    if (row > 0) {
                        over = gameBoard.get(col + (row - 1) * boardSize - 1 + row);
                    }
                    if (row < boardSize - 5) {
                        over = over || gameBoard.get(col + (row + 5) * boardSize + 5 + row);
                    }
                    if (!over) {
                        return true;
                    }
                }
            }
        }
        for (int col = 0; col < boardSize - 5; col++) {
            for (int row = 1; row <= boardSize - 5 - col; row++) {
                temp.set(0, gameBoard.get((row) * boardSize + col * (boardSize + 1)));
                temp.set(1, gameBoard.get((row + 1) * boardSize + col * (boardSize + 1) + 1));
                temp.set(2, gameBoard.get((row + 2) * boardSize + col * (boardSize + 1) + 2));
                temp.set(3, gameBoard.get((row + 3) * boardSize + col * (boardSize + 1) + 3));
                temp.set(4, gameBoard.get((row + 4) * boardSize + col * (boardSize + 1) + 4));
                if (temp.cardinality() == 5) {
                    over = false;
                    if (col > 0) {
                        over = gameBoard.get((row - 1) * boardSize + col * (boardSize + 1) - 1);
                    }
                    if (row < boardSize - 5) {
                        over = over || gameBoard.get((row + 5) * boardSize + col * (boardSize + 1) + 5);
                    }
                    return !over;
                }
            }
        }

        // DIAGONAL (R->L)
        for (int col = boardSize - 1; col >= 4; col--) {
            for (int row = 0; row <= col - 4; row++) {
                temp.set(0, gameBoard.get(col + row * (boardSize - 1)));
                temp.set(1, gameBoard.get(col + (row + 1) * (boardSize - 1)));
                temp.set(2, gameBoard.get(col + (row + 2) * (boardSize - 1)));
                temp.set(3, gameBoard.get(col + (row + 3) * (boardSize - 1)));
                temp.set(4, gameBoard.get(col + (row + 4) * (boardSize - 1)));
                if (temp.cardinality() == 5) {
                    over = false;
                    if (row > 0) {
                        over = gameBoard.get(col + (row - 1) * (boardSize - 1));
                    }
                    if (row < boardSize - 5) {
                        over = over || gameBoard.get(col + (row + 5) * (boardSize - 1));
                    }
                    if (!over) {
                        return true;
                    }
                }
            }
        }
        for (int col = boardSize - 1; col >= 5; col--) {
            for (int row = 1; row <= col-4 ; row++) {
                temp.set(0, gameBoard.get((row+1)*boardSize+(boardSize-col-1)*(boardSize-1)-1));
                temp.set(1, gameBoard.get((row+2)*boardSize+(boardSize-col-1)*(boardSize-1)-2));
                temp.set(2, gameBoard.get((row+3)*boardSize+(boardSize-col-1)*(boardSize-1)-3));
                temp.set(3, gameBoard.get((row+4)*boardSize+(boardSize-col-1)*(boardSize-1)-4));
                temp.set(4, gameBoard.get((row+5)*boardSize+(boardSize-col-1)*(boardSize-1)-5));
                if (temp.cardinality() == 5) {
                    over = false;
                    if (row > 0) {
                        over = gameBoard.get(col + (row - 1) * (boardSize - 1) + boardSize + 1);
                    }
                    if (row < boardSize - 5) {
                        over = over || gameBoard.get(col + (row + 5) * (boardSize - 1) + boardSize + 1);
                    }
                    if (!over) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void printBoard() {
        System.out.printf("%n");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (gameBoardOne.get(row * boardSize + col)) {
                    System.out.printf("O ");
                } else if (gameBoardTwo.get(row * boardSize + col)) {
                    System.out.printf("X ");
                } else {
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
                if (gameBoardOne.get(row * boardSize + col)) {
                    newGame.gameBoardOne.set(row * boardSize + col);
                } else if (gameBoardTwo.get(row * boardSize + col)) {
                    newGame.gameBoardTwo.set(row * boardSize + col);
                }
            }
        }
        newGame.currentPlayer = currentPlayer;
        return newGame;
    }

    public BitSet getGameBoardOne() {
        return gameBoardOne;
    }

    public BitSet getGameBoardTwo() {
        return gameBoardTwo;
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
                if (gameBoardOne.get(i * boardSize + j)) {
                    hash ^= hashArray[0][i][j];
                } else if (gameBoardTwo.get(i * boardSize + j)) {
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
