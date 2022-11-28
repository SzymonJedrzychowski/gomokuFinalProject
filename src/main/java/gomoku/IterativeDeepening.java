package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

public class IterativeDeepening {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<String, Integer> transpositionTable = new HashMap<>();
    HashMap<Integer, Integer> previousMovesScore = new HashMap<>();
    int timeLimit;
    long startTime;

    IterativeDeepening(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int move(GameEnvironment game) throws Exception {
        startTime = new Timestamp(System.currentTimeMillis()).getTime();
        globalDepth = 1;
        HashMap<Integer, Integer> results = new HashMap<>();
        HashMap<Integer, Integer> previousResult = new HashMap<>();
        previousMovesScore.clear();
        previousResult.put(2, -1);
        do {
            transpositionTable.clear();
            results = iterativeMove(game, globalDepth);
            globalDepth += 1;
            if (!results.containsKey(3)) {
                previousResult = results;
            }
            if (globalDepth == 30) {
                break;
            }
        } while (!results.containsKey(3));
        System.out.printf("%-30s: %d depth: %10d%n", "IterativeDeepening", game.getCurrentPlayer(), globalDepth - 1);
        return previousResult.get(2);
    }

    public HashMap<Integer, Integer> iterativeMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        String hs;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            results.put(2, results.get(1) * 1000 - results.get(1) * 10 * (globalDepth - depth));
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

        ArrayList<Integer> legalMoves;
        if (previousMovesScore.size() > 0) {
            legalMoves = sortByValue(previousMovesScore);
            if (game.getCurrentPlayer() == 1) {
                Collections.reverse(legalMoves);
            }
            previousMovesScore.clear();
        } else {
            legalMoves = game.getLegalMoves();
        }
        GameEnvironment stateCopy;

        for (int moveIndex : legalMoves) {
            stateCopy = game.copy();
            try {
                stateCopy.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            hs = stateCopy.newHash();
            if (transpositionTable.containsKey(hs)) {
                newScore = transpositionTable.get(hs);
            } else {
                results = iterativeDeepMove(stateCopy, depth - 1, -10000, 10000);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
                transpositionTable.put(hs, newScore);
            }
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if (newScore == bestScore) {
                bestMovePlace.add(moveIndex);
            }
            previousMovesScore.put(moveIndex, newScore);
        }
        results.put(2, bestMovePlace.get((int) (Math.random() * bestMovePlace.size())));
        return results;
    }

    public HashMap<Integer, Integer> iterativeDeepMove(GameEnvironment game, int depth, int alpha, int beta)
            throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        String hs;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            results.put(2, results.get(1) * 1000 - results.get(1) * 10 * (globalDepth - depth));
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

        ArrayList<Integer> legalMoves = game.getLegalMoves();
        GameEnvironment stateCopy;

        for (int moveIndex : legalMoves) {
            stateCopy = game.copy();
            try {
                stateCopy.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            hs = stateCopy.newHash();
            if (transpositionTable.containsKey(hs)) {
                newScore = transpositionTable.get(hs);
            } else {
                results = iterativeDeepMove(stateCopy, depth - 1, alpha, beta);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
                transpositionTable.put(hs, newScore);
            }
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }
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
        return results;
    }

    public static ArrayList<Integer> sortByValue(HashMap<Integer, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                    Map.Entry<Integer, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        ArrayList<Integer> temp = new ArrayList<>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.add(aa.getKey());
        }

        // put data from sorted list to hashmap
        return temp;
    }

}
