package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.Timestamp;

public class IterativeDeepening_PVS extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable = new HashMap<>();
    HashMap<Long, Integer[]> previousScores = new HashMap<>();
    int moveCount;
    boolean onlyCloseMoves;

    int simulationLimit;
    long startTime;
    boolean isLimitTime;

    IterativeDeepening_PVS(int simulationLimit, boolean isLimitTime) {
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
        this.onlyCloseMoves = false;
    }

    IterativeDeepening_PVS(int simulationLimit, boolean isLimitTime, boolean onlyCloseMoves) {
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public int move(GameEnvironment gameState) throws Exception {
        GameEnvironment game = gameState.copy();
        globalDepth = 1;

        HashMap<Integer, Integer> results = new HashMap<>();
        HashMap<Integer, Integer> previousResult = new HashMap<>();

        previousResult.put(2, -1);

        game.hashInit();

        startTime = new Timestamp(System.currentTimeMillis()).getTime();
        do {
            transpositionTable.clear();
            results = iterativeMove(game, globalDepth);

            if (!results.containsKey(3)) {
                previousResult = results;
            }

            globalDepth += 1;

            if (globalDepth == 30 || (!isLimitTime && globalDepth - 1 == simulationLimit)) {
                break;
            }
        } while (!results.containsKey(3));
        moveCount = previousResult.get(4);
        
        System.out.printf("%-30s: player %2d time: %8d moveCount: %10d depth: %10d %n", "IterativeDeepening_PVS",
                game.getCurrentPlayer(), new Timestamp(System.currentTimeMillis()).getTime() - startTime,
                moveCount, globalDepth - 1);
        previousScores.clear();

        transpositionTable.clear();
        return previousResult.get(2);
    }

    public HashMap<Integer, Integer> iterativeMove(GameEnvironment gameState, int depth) throws Exception {
        moveCount = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
        int b = beta;

        game.hashInit();

        HashMap<Integer, Integer> results = new HashMap<>();
        if (isLimitTime) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - simulationLimit + 10 > startTime) {
                results.put(3, 1);
                return results;
            }
        }

        ArrayList<Integer> legalMoves = getMoves(game);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            results = deepMove(game, globalDepth - 1, -b, -alpha);
            if (results.containsKey(3)) {
                return results;
            }
            newScore = -results.get(2);
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                results = deepMove(game, globalDepth - 1, -beta, -alpha);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = -results.get(2);
            }
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);
            b = alpha+1;

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }
        results.put(2, bestMovePlace);
        results.put(4, moveCount);


        Integer[] tempData = { bestScore, results.get(2) };
        previousScores.put(game.getHash(), tempData);

        transpositionTable.clear();
        return results;
    }

    public HashMap<Integer, Integer> deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;
        HashMap<Integer, Integer> results = new HashMap<>();
        int bestMovePlace = -1;
        int startAlpha = alpha;

        long hash = game.getHash();
        ArrayList<Integer> tempArray;
        int flag;
        if (transpositionTable.containsKey(hash)) {
            tempArray = transpositionTable.get(hash);
            flag = tempArray.get(1);
            newScore = tempArray.get(0);
            if (flag == 0) {
                results.put(2, newScore);
                return results;
            } else if (flag == 1) {
                alpha = Math.max(alpha, newScore);
            } else if (flag == 2) {
                beta = Math.min(beta, newScore);
            }
            if (alpha >= beta) {
                results.put(2, newScore);
                return results;
            }
        }

        results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                results.put(2, 0);
                return results;
            }
            results.put(2, Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10);
            return results;
        }

        if (depth == 0) {
            results.put(2, currentPlayer * game.evaluateBoard());
            return results;
        }

        if (isLimitTime) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - simulationLimit > startTime) {
                results.put(3, 1);
                return results;
            }
        }

        ArrayList<Integer> legalMoves = getMoves(game);
        int b = beta;

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            results = deepMove(game, depth - 1, -b, -alpha);
            if (results.containsKey(3)) {
                return results;
            }
            newScore = -results.get(2);
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                results = deepMove(game, depth - 1, -beta, -alpha);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = -results.get(2);
            }
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore);

            if (alpha >= beta) {
                break;
            }
            b = alpha+1;
        }

        if (bestScore <= startAlpha) {
            flag = 2;
        } else if (bestScore >= b) {
            flag = 1;
        } else {
            flag = 0;
        }
        transpositionTable.put(game.getHash(), new ArrayList<>(Arrays.asList(bestScore, flag)));
        results.put(2, bestScore);
        Integer[] tempData = { bestScore, bestMovePlace };
        previousScores.put(game.getHash(), tempData);

        return results;
    }

    public ArrayList<Integer> getMoves(GameEnvironment game) {
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        if (!previousScores.containsKey(game.getHash())) {
            return legalMoves;
        }

        ArrayList<Integer> sortedMoves = new ArrayList<>();
        sortedMoves.add(previousScores.get(game.getHash())[1]);
        for (int moveIndex : legalMoves) {
            if (sortedMoves.get(0) != moveIndex) {
                sortedMoves.add(moveIndex);
            }
        }

        return sortedMoves;
    }

}
