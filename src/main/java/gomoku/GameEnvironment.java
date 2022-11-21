package gomoku;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class GameEnvironment {
    private int boardSize;
    private BitSet gameBoardOne;
    private BitSet gameBoardTwo;
    private int currentPlayer;

    GameEnvironment(int boardSize) {
        this.boardSize = boardSize;
        this.gameBoardOne = new BitSet(boardSize * boardSize);
        this.gameBoardTwo = new BitSet(boardSize * boardSize);
        resetState();
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
        currentPlayer *= -1;
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

        // HORIZONTAL
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize - 4; col++) {
                if (gameBoard.get(row * boardSize + col, row * boardSize + col + 5).cardinality() == 5) {
                    return true;
                }
            }
        }

        // VERTICAL
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize - 4; row++) {
                temp.set(0, gameBoard.get(col + row * boardSize));
                temp.set(1, gameBoard.get(col + row * boardSize + boardSize));
                temp.set(2, gameBoard.get(col + row * boardSize + 2 * boardSize));
                temp.set(3, gameBoard.get(col + row * boardSize + 3 * boardSize));
                temp.set(4, gameBoard.get(col + row * boardSize + 4 * boardSize));
                if (temp.cardinality() == 5) {
                    return true;
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
                temp.set(1, gameBoard.get(col + row * boardSize + boardSize + 1 + row));
                temp.set(2, gameBoard.get(col + row * boardSize + 2 * boardSize + 2 + row));
                temp.set(3, gameBoard.get(col + row * boardSize + 3 * boardSize + 3 + row));
                temp.set(4, gameBoard.get(col + row * boardSize + 4 * boardSize + 4 + row));
                if (temp.cardinality() == 5) {
                    return true;
                }
                if (col > 0) {
                    temp.set(0, gameBoard.get(col + row * boardSize + row + boardSize - 1));
                    temp.set(1, gameBoard.get(col + row * boardSize + boardSize + 1 + row + boardSize - 1));
                    temp.set(2, gameBoard.get(col + row * boardSize + 2 * boardSize + 2 + row + boardSize - 1));
                    temp.set(3, gameBoard.get(col + row * boardSize + 3 * boardSize + 3 + row + boardSize - 1));
                    temp.set(4, gameBoard.get(col + row * boardSize + 4 * boardSize + 4 + row + boardSize - 1));
                    if (temp.cardinality() == 5) {
                        return true;
                    }
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
                    return true;
                }
                if (col < boardSize - 1) {
                    temp.set(0, gameBoard.get(col + row * (boardSize - 1) + boardSize + 1));
                    temp.set(1, gameBoard.get(col + (row + 1) * (boardSize - 1) + boardSize + 1));
                    temp.set(2, gameBoard.get(col + (row + 2) * (boardSize - 1) + boardSize + 1));
                    temp.set(3, gameBoard.get(col + (row + 3) * (boardSize - 1) + boardSize + 1));
                    temp.set(4, gameBoard.get(col + (row + 4) * (boardSize - 1) + boardSize + 1));
                    if (temp.cardinality() == 5) {
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
        GameEnvironment newGame = new GameEnvironment(boardSize);
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

    public String newHash(){
        return Integer.toString(currentPlayer)+gameBoardOne.toString()+gameBoardTwo.toString();
    }
}
