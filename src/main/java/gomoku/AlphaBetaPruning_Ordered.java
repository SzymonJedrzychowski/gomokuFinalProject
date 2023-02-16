package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.Timestamp;

public class AlphaBetaPruning_Ordered extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable = new HashMap<>();
    int moveCount;
    boolean onlyCloseMoves;

    AlphaBetaPruning_Ordered(int globalDepth) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = false;
    }

    AlphaBetaPruning_Ordered(int globalDepth, boolean onlyCloseMoves) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public int move(GameEnvironment gameState) throws Exception {
        moveCount = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;

        game.hashInit();

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

        ArrayList<Integer> legalMoves;
        if (globalDepth > 1) {
            legalMoves = sortMoves(game);
        } else {
            legalMoves = game.getLegalMoves(onlyCloseMoves);
        }

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -beta, -alpha);
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
        //System.out.printf("%-30s: player %2d time: %8d moveCount: %10d%n", "AlphaBetaPruning_Ordered", currentPlayer,
        //        endTimestamp.getTime() - startTimestamp.getTime(), moveCount);

        transpositionTable.clear();
        return bestMovePlace;
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;

        long hash = game.getHash();
        ArrayList<Integer> tempArray;
        int flag;
        if (transpositionTable.containsKey(hash)) {
            tempArray = transpositionTable.get(hash);
            flag = tempArray.get(1);
            newScore = tempArray.get(0);
            if (flag == 0) {
                return newScore;
            } else if (flag == 1) {
                alpha = Math.max(alpha, newScore);
            } else if (flag == 2) {
                beta = Math.min(beta, newScore);
            }
            if (alpha >= beta) {
                return newScore;
            }
        }

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                return 0;
            }
            return Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10;
        }

        if (depth == 0) {
            return currentPlayer * game.evaluateBoard();
        }

        ArrayList<Integer> legalMoves;
        if (globalDepth - depth <= Math.min(2, globalDepth - 2)) {
            legalMoves = sortMoves(game);
        } else {
            legalMoves = game.getLegalMoves(onlyCloseMoves);
        }

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -beta, -alpha);
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore);

            if (alpha >= beta) {
                break;
            }
        }

        if (bestScore <= alpha) {
            flag = 2;
        } else if (bestScore >= beta) {
            flag = 1;
        } else {
            flag = 0;
        }
        transpositionTable.put(game.getHash(), new ArrayList<>(Arrays.asList(bestScore, flag)));

        return bestScore;
    }

    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception {
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        int bestScore = Integer.MIN_VALUE;
        int bestIndex = -1;

        int tempScore;
        for (int moveIndex : legalMoves) {
            game.move(moveIndex);
            tempScore = game.evaluateBoard();
            game.undoMove(moveIndex);
            if (game.getCurrentPlayer() * tempScore > bestScore) {
                bestScore = tempScore;
                bestIndex = moveIndex;
            }
        }
        sortedMoves.add(bestIndex);
        for (int moveIndex : legalMoves) {
            if (moveIndex != bestIndex) {
                sortedMoves.add(moveIndex);
            }
        }
        return sortedMoves;
    }
}