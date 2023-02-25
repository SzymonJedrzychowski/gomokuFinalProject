package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class GameEnvironment {

    private final int boardSize;
    private int[][] gameBoard;
    private int currentPlayer;
    private long[][][] hashArray;
    private long hash;
    int moveCount;
    int[] scoreTable = { 0, 0, 1, 10, 50, 10000 };

    GameEnvironment(int boardSize) throws Exception {
        this.boardSize = boardSize;
        if (boardSize % 2 == 0) {
            throw new Exception("BoardSize needs to be odd.");
        }
        this.gameBoard = new int[boardSize][boardSize];
        moveCount = 0;
        currentPlayer = 1;
    }

    public void resetState() {
        this.gameBoard = new int[boardSize][boardSize];
        moveCount = 0;
        currentPlayer = 1;
    }

    public void move(int move) throws Exception {
        if (move < 0 || move > boardSize * boardSize - 1) {
            throw new Exception("Space out of bound.");
        } else if (gameBoard[move / boardSize][move % boardSize] != 0) {
            throw new Exception("Already occupied space.");
        }
        gameBoard[move / boardSize][move % boardSize] = currentPlayer;
        currentPlayer *= -1;
        moveCount += 1;
    }

    public void undoMove(int move) {
        currentPlayer *= -1;
        gameBoard[move / boardSize][move % boardSize] = 0;
        moveCount -= 1;
    }

    public ArrayList<Integer> getLegalMoves(boolean getCloseMoves) {
        if (getCloseMoves == false) {
            ArrayList<Integer> legalMoves = new ArrayList<>();
            for (int row = 0; row < boardSize; row++) {
                for (int column = 0; column < boardSize; column++) {
                    if (gameBoard[row][column] == 0) {
                        legalMoves.add(row * boardSize + column);
                    }
                }
            }
            Collections.shuffle(legalMoves);
            return legalMoves;
        } else {
            return getCloseMoves();
        }
    }

    public ArrayList<Integer> getCloseMoves() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                if (gameBoard[row][column] != 0)
                    continue;

                if (row > 0) {
                    if (column > 0) {
                        if (gameBoard[row - 1][column - 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (column < boardSize - 1) {
                        if (gameBoard[row - 1][column + 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (gameBoard[row - 1][column] != 0) {
                        result.add(row * boardSize + column);
                        continue;
                    }
                }
                if (row < boardSize - 1) {
                    if (column > 0) {
                        if (gameBoard[row + 1][column - 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (column < boardSize - 1) {
                        if (gameBoard[row + 1][column + 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (gameBoard[row + 1][column] != 0) {
                        result.add(row * boardSize + column);
                        continue;
                    }
                }
                if (column > 0) {
                    if (row > 0) {
                        if (gameBoard[row - 1][column - 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (row < boardSize - 1) {
                        if (gameBoard[row + 1][column - 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (gameBoard[row][column - 1] != 0) {
                        result.add(row * boardSize + column);
                        continue;
                    }

                }
                if (column < boardSize - 1) {
                    if (row > 0) {
                        if (gameBoard[row - 1][column + 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (row < boardSize - 1) {
                        if (gameBoard[row + 1][column + 1] != 0) {
                            result.add(row * boardSize + column);
                            continue;
                        }
                    }
                    if (gameBoard[row][column + 1] != 0)
                        result.add(row * boardSize + column);

                }
            }
        }

        if (result.isEmpty()) {
            for (int row = 0; row <= boardSize / 2; row++) {
                for (int column = 0; column <= row; column++) {
                    result.add(row * boardSize + column);
                }
            }
        }
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
        boolean checkResult = checkHorizontal() || checkVertical() || checkDiagonals();
        if (checkResult) {
            result.put(0, 1);
            result.put(1, -currentPlayer);
            return result;
        } else if (moveCount == boardSize * boardSize) {
            result.put(0, 1);
        }
        return result;
    }

    private boolean checkHorizontal() {
        int currentResult = 0;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (currentResult == 5 && gameBoard[row][col] != -currentPlayer)
                    return true;

                if (gameBoard[row][col] == -currentPlayer)
                    currentResult += 1;
                else
                    currentResult = 0;

                if (currentResult + boardSize - col - 1 < 5)
                    break;
            }
            if (currentResult == 5)
                return true;
            currentResult = 0;
        }
        return false;
    }

    private boolean checkVertical() {
        int currentResult = 0;
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                if (currentResult == 5 && gameBoard[row][col] != -currentPlayer)
                    return true;

                if (gameBoard[row][col] == -currentPlayer)
                    currentResult += 1;
                else
                    currentResult = 0;

                if (currentResult + boardSize - row - 1 < 5)
                    break;
            }
            if (currentResult == 5)
                return true;
            currentResult = 0;
        }
        return false;
    }

    private boolean checkDiagonals() {
        int currentResult = 0;
        int startingPoint = boardSize * (boardSize - 5);
        int maxLength = 5;

        // Left-Up to Right-Down
        while (true) {
            for (int currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                int row = startingPoint / boardSize + currentPlace;
                int col = startingPoint % boardSize + currentPlace;
                if (currentResult == 5 & gameBoard[row][col] != -currentPlayer)
                    return true;

                if (gameBoard[row][col] == -currentPlayer)
                    currentResult += 1;
                else
                    currentResult = 0;

                if (maxLength - currentPlace - 1 + currentResult < 5)
                    break;
            }
            if (currentResult == 5)
                return true;
            currentResult = 0;
            if (startingPoint == boardSize - 5)
                break;

            if (startingPoint >= boardSize) {
                startingPoint -= boardSize;
                maxLength += 1;
            } else {
                startingPoint += 1;
                maxLength -= 1;
            }

        }

        startingPoint = 4 * boardSize;
        maxLength = 5;

        // Left-Down to Right-Up
        while (true) {
            for (int currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                int row = startingPoint / boardSize - currentPlace;
                int col = startingPoint % boardSize + currentPlace;
                if (currentResult == 5 & gameBoard[row][col] != -currentPlayer)
                    return true;

                if (gameBoard[row][col] == -currentPlayer)
                    currentResult += 1;
                else
                    currentResult = 0;

                if (maxLength - currentPlace - 1 + currentResult < 5) {
                    break;
                }
            }
            if (currentResult == 5)
                return true;
            currentResult = 0;
            if (startingPoint == boardSize * boardSize - 5)
                return false;

            if (startingPoint < boardSize * (boardSize - 1)) {
                startingPoint += boardSize;
                maxLength += 1;
            } else {
                startingPoint += 1;
                maxLength -= 1;
            }
        }
    }

    private int evaluateHorizontal() {
        int evaluationResult = 0;
        int bestResult;
        int result;
        for (int row = 0; row < boardSize; row++) {
            int firstStones = 0;
            int secondStones = 0;
            for (int col = 0; col < boardSize; col++) {
                if (col > 4) {
                    if (firstStones > 1 && secondStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = col - 5; i < col; i++) {
                            if (gameBoard[row][i] == 1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (col > 5 && gameBoard[row][col - 6] == 1) {
                        } else if (gameBoard[row][col] == 1) {
                        } else {
                            evaluationResult += scoreTable[bestResult];
                        }
                    } else if (secondStones > 1 && firstStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = col - 5; i < col; i++) {
                            if (gameBoard[row][i] == -1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (col > 5 && gameBoard[row][col - 6] == -1) {
                        } else if (gameBoard[row][col] == -1) {
                        } else {
                            evaluationResult -= scoreTable[bestResult];
                        }
                    }

                    if (gameBoard[row][col - 5] == 1)
                        firstStones--;
                    else if (gameBoard[row][col - 5] == -1)
                        secondStones--;
                }
                if (gameBoard[row][col] == 1)
                    firstStones++;
                else if (gameBoard[row][col] == -1)
                    secondStones++;
            }
            if (firstStones > 1 && secondStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = boardSize - 5; i < boardSize; i++) {
                    if (gameBoard[row][i] == 1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (gameBoard[row][boardSize - 6] != 1) {
                    evaluationResult += scoreTable[bestResult];
                }
            } else if (secondStones > 1 && firstStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = boardSize - 5; i < boardSize; i++) {
                    if (gameBoard[row][i] == -1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (gameBoard[row][boardSize - 6] != -1) {
                    evaluationResult -= scoreTable[bestResult];
                }
            }
        }

        return evaluationResult;
    }

    private int evaluateVertical() {
        int evaluationResult = 0;
        int bestResult;
        int result;
        for (int col = 0; col < boardSize; col++) {
            int firstStones = 0;
            int secondStones = 0;
            for (int row = 0; row < boardSize; row++) {
                if (row > 4) {
                    if (firstStones > 1 && secondStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = row - 5; i < row; i++) {
                            if (gameBoard[i][col] == 1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (row > 5 && gameBoard[row - 6][col] == 1) {
                        } else if (gameBoard[row][col] == 1) {
                        } else {
                            evaluationResult += scoreTable[bestResult];
                        }
                    } else if (secondStones > 1 && firstStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = row - 5; i < row; i++) {
                            if (gameBoard[i][col] == -1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (row > 5 && gameBoard[row - 6][col] == -1) {
                        } else if (gameBoard[row][col] == -1) {
                        } else {
                            evaluationResult -= scoreTable[bestResult];
                        }
                    }

                    if (gameBoard[row - 5][col] == 1)
                        firstStones--;
                    else if (gameBoard[row - 5][col] == -1)
                        secondStones--;
                }
                if (gameBoard[row][col] == 1)
                    firstStones++;
                else if (gameBoard[row][col] == -1)
                    secondStones++;
            }
            if (firstStones > 1 && secondStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = boardSize - 5; i < boardSize; i++) {
                    if (gameBoard[i][col] == 1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (gameBoard[boardSize - 6][col] != 1) {
                    evaluationResult += scoreTable[bestResult];
                }
            } else if (secondStones > 1 && firstStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = boardSize - 5; i < boardSize; i++) {
                    if (gameBoard[i][col] == -1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (gameBoard[boardSize - 6][col] != -1) {
                    evaluationResult -= scoreTable[bestResult];
                }
            }
        }

        return evaluationResult;
    }

    private int evaluateDiagonal() {
        int evaluationResult = 0;
        int bestResult;
        int result;
        int startingPoint = boardSize * (boardSize - 5);
        int maxLength = 5;
        while (true) {
            int firstStones = 0;
            int secondStones = 0;
            int row = -1;
            int col = -1;
            int currentPlace = 0;
            for (currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                row = startingPoint / boardSize + currentPlace;
                col = startingPoint % boardSize + currentPlace;
                if (currentPlace > 4) {
                    if (firstStones > 1 && secondStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = 0; i < 5; i++) {
                            if (gameBoard[row - 5 + i][col - 5 + i] == 1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (currentPlace > 5 && gameBoard[row - 6][col - 6] == 1) {
                        } else if (gameBoard[row][col] == 1) {
                        } else {
                            evaluationResult += scoreTable[bestResult];
                        }
                    } else if (secondStones > 1 && firstStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = 0; i < 5; i++) {
                            if (gameBoard[row - 5 + i][col - 5 + i] == -1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (currentPlace > 5 && gameBoard[row - 6][col - 6] == -1) {
                        } else if (gameBoard[row][col] == -1) {
                        } else {
                            evaluationResult -= scoreTable[bestResult];
                        }
                    }

                    if (gameBoard[row - 5][col - 5] == 1)
                        firstStones--;
                    else if (gameBoard[row - 5][col - 5] == -1)
                        secondStones--;
                }
                if (gameBoard[row][col] == 1)
                    firstStones++;
                else if (gameBoard[row][col] == -1)
                    secondStones++;
            }
            row = startingPoint / boardSize + currentPlace;
            col = startingPoint % boardSize + currentPlace;
            if (firstStones > 1 && secondStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = 0; i < 5; i++) {
                    if (gameBoard[row - 5 + i][col - 5 + i] == 1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (maxLength > 5 && gameBoard[row - 6][col - 6] == 1) {
                } else {
                    evaluationResult += scoreTable[bestResult];
                }
            } else if (secondStones > 1 && firstStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = 0; i < 5; i++) {
                    if (gameBoard[row - 5 + i][col - 5 + i] == -1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (maxLength > 5 && gameBoard[row - 6][col - 6] == -1) {
                } else {
                    evaluationResult -= scoreTable[bestResult];
                }
            }
            if (startingPoint == boardSize - 5)
                break;

            if (startingPoint >= boardSize) {
                startingPoint -= boardSize;
                maxLength += 1;
            } else {
                startingPoint += 1;
                maxLength -= 1;
            }
        }

        startingPoint = 4 * boardSize;
        maxLength = 5;
        result = 0;
        bestResult = 0;
        while (true) {
            int firstStones = 0;
            int secondStones = 0;
            int row = -1;
            int col = -1;
            int currentPlace = 0;
            for (currentPlace = 0; currentPlace < maxLength; currentPlace++) {
                row = startingPoint / boardSize - currentPlace;
                col = startingPoint % boardSize + currentPlace;
                // System.out.printf("r%d c%d%n", row, col);
                if (currentPlace > 4) {
                    // System.out.printf("%d %d%n", firstStones, secondStones);
                    if (firstStones > 1 && secondStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = 0; i < 5; i++) {
                            if (gameBoard[row + 5 - i][col - 5 + i] == 1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (currentPlace > 5 && gameBoard[row + 6][col - 6] == 1) {
                        } else if (gameBoard[row][col] == 1) {
                        } else {
                            evaluationResult += scoreTable[bestResult];
                        }
                    } else if (secondStones > 1 && firstStones == 0) {
                        result = 0;
                        bestResult = 0;
                        for (int i = 0; i < 5; i++) {
                            if (gameBoard[row + 5 - i][col - 5 + i] == -1) {
                                result++;
                                bestResult = Math.max(result, bestResult);
                            } else {
                                result = 0;
                            }
                        }
                        if (currentPlace > 5 && gameBoard[row + 6][col - 6] == -1) {
                        } else if (gameBoard[row][col] == -1) {
                        } else {
                            evaluationResult -= scoreTable[bestResult];
                        }
                    }

                    if (gameBoard[row + 5][col - 5] == 1)
                        firstStones--;
                    else if (gameBoard[row + 5][col - 5] == -1)
                        secondStones--;
                }
                if (gameBoard[row][col] == 1)
                    firstStones++;
                else if (gameBoard[row][col] == -1)
                    secondStones++;
            }
            row = startingPoint / boardSize - currentPlace;
            col = startingPoint % boardSize + currentPlace;
            if (firstStones > 1 && secondStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = 0; i < 5; i++) {
                    if (gameBoard[row + 5 - i][col - 5 + i] == 1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (maxLength > 5 && gameBoard[row + 6][col - 6] == 1) {
                } else {
                    evaluationResult += scoreTable[bestResult];
                }
            } else if (secondStones > 1 && firstStones == 0) {
                result = 0;
                bestResult = 0;
                for (int i = 0; i < 5; i++) {
                    if (gameBoard[row + 5 - i][col - 5 + i] == -1) {
                        result++;
                        bestResult = Math.max(result, bestResult);
                    } else {
                        result = 0;
                    }
                }
                if (maxLength > 5 && gameBoard[row + 6][col - 6] == -1) {
                } else {
                    evaluationResult -= scoreTable[bestResult];
                }
            }
            if (startingPoint == boardSize * boardSize - 5)
                break;

            if (startingPoint < boardSize * (boardSize - 1)) {
                startingPoint += boardSize;
                maxLength += 1;
            } else {
                startingPoint += 1;
                maxLength -= 1;
            }
        }

        return evaluationResult;
    }

    private int right(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard[row][col + i];
            } else {
                if (gameBoard[row][col + i] == -checkPlayer) {
                    return 0;
                } else if (gameBoard[row][col + i] == checkPlayer) {
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
            if (gameBoard[row][col - 1] == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize) {
            if (gameBoard[row][col + 5] == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    private int down(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard[row + i][col];
            } else {
                if (gameBoard[row + i][col] == -checkPlayer) {
                    return 0;
                } else if (gameBoard[row + i][col] == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (row > 0) {
            if (gameBoard[row - 1][col] == checkPlayer) {
                return 0;
            }
        }
        if (row + 5 < boardSize) {
            if (gameBoard[row + 5][col] == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    private int rightBottom(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard[row + i][col + i];
            } else {
                if (gameBoard[row + i][col + i] == -checkPlayer) {
                    return 0;
                } else if (gameBoard[row + i][col + i] == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (col > 0 && row > 0) {
            if (gameBoard[row - 1][col - 1] == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize && row + 5 < boardSize) {
            if (gameBoard[row + 5][col + 5] == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    private int rightUpward(int row, int col) {
        int result = 1;
        int maxResult = 1;
        int checkPlayer = 0;
        for (int i = 0; i < 5; i++) {
            if (checkPlayer == 0) {
                checkPlayer = gameBoard[row - i][col + i];
            } else {
                if (gameBoard[row - i][col + i] == -checkPlayer) {
                    return 0;
                } else if (gameBoard[row - i][col + i] == checkPlayer) {
                    result += 1;
                    maxResult = Math.max(result, maxResult);
                } else {
                    result = 0;
                }
            }
        }
        if (col > 0 && row + 1 < boardSize) {
            if (gameBoard[row + 1][col - 1] == checkPlayer) {
                return 0;
            }
        }
        if (col + 5 < boardSize && row - 5 >= 0) {
            if (gameBoard[row - 5][col + 5] == checkPlayer) {
                return 0;
            }
        }
        return checkPlayer * scoreTable[maxResult];
    }

    public int evaluateBoard() {
        return evaluateHorizontal() + evaluateVertical() + evaluateDiagonal();
    }

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

    public GameEnvironment copy() throws Exception {
        GameEnvironment newGame = new GameEnvironment(boardSize);
        newGame.hashArray = hashArray;
        newGame.hash = hash;
        System.arraycopy(gameBoard, 0, newGame.gameBoard, 0, boardSize);
        newGame.moveCount = moveCount;
        newGame.currentPlayer = currentPlayer;
        return newGame;
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
                if (gameBoard[i][j] == 1) {
                    hash ^= hashArray[0][i][j];
                } else if (gameBoard[i][j] == -1) {
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
