package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.Timestamp;

public class IterativeDeepening extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable = new HashMap<>();
    HashMap<Long, Integer[]> previousScores = new HashMap<>();

    int simulationLimit;
    long startTime;
    boolean isLimitTime;

    IterativeDeepening(int simulationLimit, boolean isLimitTime) {
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
    }

    public int move(GameEnvironment gameState) throws Exception {
        GameEnvironment game = gameState.copy();
        globalDepth = 1;

        HashMap<Integer, Integer> results = new HashMap<>();
        HashMap<Integer, Integer> previousResult = new HashMap<>();

        previousScores.clear();
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

        //System.out.printf("%-30s: %d time: %10d depth: %10d%n", "IterativeDeepening", game.getCurrentPlayer(), new Timestamp(System.currentTimeMillis()).getTime()-startTime,globalDepth - 1);
        
        transpositionTable.clear();
        return previousResult.get(2);
    }

    public HashMap<Integer, Integer> iterativeMove(GameEnvironment gameState, int depth) throws Exception {
        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int newScore;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (isLimitTime) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - simulationLimit + 10 > startTime) {
                results.put(3, 1);
                return results;
            }
        }

        ArrayList<Integer> legalMoves = getMoves(game);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            results = iterativeDeepMove(game, depth - 1, alpha, beta);
            if (results.containsKey(3)) {
                return results;
            }
            newScore = results.get(2);

            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if ((newScore == bestScore && currentPlayer == 1)
                    || (newScore == bestScore && currentPlayer == -1)) {
                bestMovePlace.add(moveIndex);
            }

            if (currentPlayer == 1) {
                alpha = Math.max(alpha, newScore);
            } else {
                beta = Math.min(beta, newScore);
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);
            //System.out.printf("%d %d%n", moveIndex, new Timestamp(System.currentTimeMillis()).getTime()-startTime);
        }

        results.put(2, bestMovePlace.get((int) (Math.random() * bestMovePlace.size())));

        Integer[] tempData = { bestScore, results.get(2) };
        previousScores.put(game.getHash(), tempData);

        return results;
    }

    public HashMap<Integer, Integer> iterativeDeepMove(GameEnvironment game, int depth, int alpha, int beta)
            throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int newScore = -1;

        long hash = game.getHash();

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                results.put(2, 0);
            } else {
                results.put(2, results.get(1) == 1 ? Integer.MAX_VALUE - (globalDepth - depth) * 10
                        : Integer.MIN_VALUE + (globalDepth - depth) * 10);
            }
            return results;
        }

        if (depth == 0) {
            results.put(2, game.evaluateBoard());
            return results;
        }

        if (isLimitTime) {
            if (new Timestamp(System.currentTimeMillis()).getTime() - simulationLimit > startTime) {
                results.put(3, 1);
                return results;
            }
        }

        ArrayList<Integer> legalMoves = getMoves(game);
        ArrayList<Integer> tempArray;
        int flag;
        boolean skipSimulation;
        boolean cutoff = false;
        for (int moveIndex : legalMoves) {
            skipSimulation = false;
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("IterativeDeepening: " + e);
            }

            hash = game.update(currentPlayer, moveIndex);

            if (transpositionTable.containsKey(hash)) {
                tempArray = transpositionTable.get(hash);
                flag = tempArray.get(1);
                newScore = tempArray.get(0);
                if (flag == 0) {
                    skipSimulation = true;
                }else if(flag == 1 && newScore >= beta){
                    skipSimulation = true;
                }else if(flag == 2 && newScore <= alpha){
                    skipSimulation = true;
                }else if(beta <= alpha){
                    skipSimulation = true;
                }
            }
            if (!skipSimulation) {
                results = iterativeDeepMove(game, depth - 1, alpha, beta);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
            }

            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if ((newScore == bestScore && currentPlayer == 1)
                    || (newScore == bestScore && currentPlayer == -1)) {
                bestMovePlace.add(moveIndex);
            }

            game.undoMove(moveIndex);

            flag = 0;
            if (currentPlayer == 1) {
                alpha = Math.max(alpha, newScore);
                if (newScore > beta) {
                    flag = 2;
                    cutoff = true;
                }
            } else {
                beta = Math.min(beta, newScore);
                if (newScore < alpha) {
                    flag = 1;
                    cutoff = true;
                }
            }
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(newScore, flag)));
            hash = game.update(currentPlayer, moveIndex);

            if (cutoff) {
                break;
            }
        }

        results.put(2, bestScore);
        Integer[] tempData = { bestScore, bestMovePlace.get((int) (Math.random() * bestMovePlace.size())) };
        previousScores.put(game.getHash(), tempData);

        return results;
    }

    public ArrayList<Integer> getMoves(GameEnvironment game) {
        if (!previousScores.containsKey(game.getHash())) {
            return game.getLegalMoves();
        }

        ArrayList<Integer> legalMoves = new ArrayList<>();

        legalMoves.add(previousScores.get(game.getHash())[1]);
        for (int moveIndex : game.getLegalMoves()) {
            if (legalMoves.get(0) != moveIndex) {
                legalMoves.add(moveIndex);
            }
        }

        return legalMoves;
    }

}
