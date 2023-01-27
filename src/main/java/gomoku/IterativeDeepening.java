package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

public class IterativeDeepening extends Player {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<Long, Integer> transpositionTable = new HashMap<>();
    HashMap<Long, Integer[]> previousScores = new HashMap<>();

    int timeLimit;
    long startTime;

    IterativeDeepening(int timeLimit) {
        this.timeLimit = timeLimit;
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

            if (!results.containsKey(3) && globalDepth % 2 == 1) {
                previousResult = results;
            }

            globalDepth += 1;

            if (globalDepth == 30) {
                break;
            }
        } while (!results.containsKey(3));

        System.out.printf("%-30s: %d depth: %10d%n", "IterativeDeepening", game.getCurrentPlayer(), globalDepth - 1);
        System.out.println(previousScores.get(game.getHash())[0]);
        return previousResult.get(2);
    }

    public HashMap<Integer, Integer> iterativeMove(GameEnvironment gameState, int depth) throws Exception {
        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = -10000 * currentPlayer;
        int newScore;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (new Timestamp(System.currentTimeMillis()).getTime() - timeLimit + 10 > startTime) {
            results.put(3, 1);
            return results;
        }

        ArrayList<Integer> legalMoves = getMoves(game);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            results = iterativeDeepMove(game, depth - 1, -10000, 10000);
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

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);
        }

        results.put(2, bestMovePlace.get((int) (Math.random() * bestMovePlace.size())));
        
        Integer[] tempData = {bestScore, results.get(2)};
        previousScores.put(game.getHash(), tempData);

        return results;
    }

    public HashMap<Integer, Integer> iterativeDeepMove(GameEnvironment game, int depth, int alpha, int beta)
            throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = -10000 * currentPlayer;
        int newScore;

        long hash = game.getHash();

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            results.put(2, results.get(1) * 10000 - results.get(1) * 10 * (globalDepth - depth));
            return results;
        }

        if (depth == 0) {
            results.put(2, evaluator.calculateEvaluation(game));
            return results;
        }

        if (new Timestamp(System.currentTimeMillis()).getTime() - timeLimit + 10 > startTime) {
            results.put(3, 1);
            return results;
        }

        ArrayList<Integer> legalMoves = getMoves(game);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            hash = game.update(currentPlayer, moveIndex);

            if (transpositionTable.containsKey(hash)) {
                newScore = transpositionTable.get(hash);
            } else {
                results = iterativeDeepMove(game, depth - 1, alpha, beta);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
                transpositionTable.put(hash, newScore);
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
            hash = game.update(currentPlayer, moveIndex);

            if (game.getCurrentPlayer() == 1) {
                if (newScore >= beta) {
                    break;
                }
                alpha = Math.max(alpha, newScore);
            } else {
                if (newScore <= alpha) {
                    break;
                }
                beta = Math.min(beta, newScore);
            }
        }

        results.put(2, bestScore);
        Integer[] tempData = {bestScore, bestMovePlace.get((int) (Math.random() * bestMovePlace.size()))};
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
