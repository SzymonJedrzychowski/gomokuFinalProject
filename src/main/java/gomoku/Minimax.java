package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

public class Minimax {
    int globalDepth;

    Minimax(int globalDepth) {
        this.globalDepth = globalDepth;
    }

    public int evaluationFunction(int[][] scores) {
        return scores[0][0] + scores[0][1] * 3 + scores[0][2] * 9 + 100 * scores[0][3]
                - (scores[1][0] + scores[1][1] * 3 + scores[1][2] * 9 + 100 * scores[1][3]);
    }

    public int evaluateBoard(GameEnvironment game) {
        int[][] scores = { { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };
        int currentScore;
        int currentPlayerScore;
        int boardSize = game.getBoardSize();
        int[][] gameBoard = game.getGameBoard();

        // HORIZONTAL
        for (int row = 0; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;
            for (int col = 0; col < boardSize; col++) {
                if (currentPlayerScore == gameBoard[row][col] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row][col];
                    currentScore += 1;
                    if (currentScore == 5 && col == boardSize - 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[row][col] && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                }
            }
        }

        // VERTICAL
        for (int col = 0; col < boardSize; col++) {
            currentScore = 0;
            currentPlayerScore = 0;

            for (int row = 0; row < boardSize; row++) {
                if (currentPlayerScore == gameBoard[row][col] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row][col];
                    currentScore += 1;
                    if (currentScore == 5 && row == boardSize - 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[row][col] && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row][col];
                    currentScore = 1;
                }
            }

        }

        // DIAGONAL (L-R)
        for (int col = 1; col < boardSize; col++) {
            currentScore = 0;
            currentPlayerScore = 0;

            if ((boardSize - col) - 5 < 0) {
                break;
            }
            for (int rowL = 0; rowL < boardSize - col; rowL++) {
                if (currentPlayerScore == gameBoard[rowL][col + rowL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore += 1;
                    if (currentScore == 5 && rowL == boardSize - col - 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[rowL][col + rowL] && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[rowL][col + rowL];
                    currentScore = 1;
                }
            }
        }

        for (int row = 0; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;

            if ((boardSize - row) - 5 < 0) {
                break;
            }
            for (int colL = 0; colL < boardSize - row; colL++) {
                if (currentPlayerScore == gameBoard[row + colL][colL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore += 1;
                    if (currentScore == 5 && colL == boardSize - row - 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[row + colL][colL] && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row + colL][colL];
                    currentScore = 1;
                }
            }

        }

        // DIAGONAL (R-L)
        for (int col = boardSize - 1; col >= 0; col--) {
            currentScore = 0;
            currentPlayerScore = 0;
            if (col - 4 < 0) {
                break;
            }
            for (int rowL = 0; rowL <= col; rowL++) {
                if (currentPlayerScore == gameBoard[rowL][col - rowL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore += 1;
                    if (currentScore == 5 && rowL == col - 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[rowL][col - rowL] && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[rowL][col - rowL];
                    currentScore = 1;
                }
            }
        }

        for (int row = 1; row < boardSize; row++) {
            currentScore = 0;
            currentPlayerScore = 0;
            if ((boardSize - row) - 5 < 0) {
                break;
            }
            for (int colL = boardSize - 1; colL >= row; colL--) {
                if (currentPlayerScore == gameBoard[row + boardSize - 1 - colL][colL] && currentPlayerScore != 0) {
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore += 1;
                    if (currentScore == 5 && colL == row) {
                        if (currentPlayerScore == 1) {
                            scores[0][3] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][3] += 1;
                        }
                        return evaluationFunction(scores);
                    }
                } else if (currentPlayerScore != gameBoard[row + boardSize - 1 - colL][colL]
                        && currentPlayerScore != 0) {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore = 1;
                } else {
                    if (currentScore < 6 && currentScore > 1) {
                        if (currentPlayerScore == 1) {
                            scores[0][currentScore - 2] += 1;
                        } else if (currentPlayerScore == -1) {
                            scores[1][currentScore - 2] += 1;
                        }
                    }
                    currentPlayerScore = gameBoard[row + boardSize - 1 - colL][colL];
                    currentScore = 1;
                }
            }
        }

        return evaluationFunction(scores);
    }

    public int move(GameEnvironment game) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -1000 * currentPlayer;
        int newScore;
        int bestMovePlace = 0;

        ArrayList<Integer> legalMoves = game.getLegalMoves();
        GameEnvironment stateCopy;

        for (int moveIndex : legalMoves) {
            stateCopy = game.copy();
            try {
                stateCopy.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            newScore = deepMove(stateCopy, globalDepth-1);
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }
        }
        return bestMovePlace;
    }

    public int deepMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -1000 * currentPlayer;
        int newScore;
        
        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            return results.get(1) * 1000;
        }

        if(depth == 0){
            return evaluateBoard(game);
        }

        ArrayList<Integer> legalMoves = game.getLegalMoves();
        GameEnvironment stateCopy;
        
        for (int moveIndex : legalMoves) {
            stateCopy = game.copy();
            try {
                stateCopy.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            newScore = deepMove(stateCopy, depth-1);
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }
        }

        return bestScore;
    }

}
