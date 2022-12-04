package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

public class IterativeDeepening extends Player {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<Long, Integer> transpositionTable = new HashMap<>();
    HashMap<Long, HashMap<Integer, GameEnvironment>> previousStates = new HashMap<>();
    HashMap<Long, HashMap<Integer, Integer>> previousScores = new HashMap<>();

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
        game.hashInit();
        previousStates.clear();
        previousScores.clear();
        previousResult.put(2, -1);
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
        return previousResult.get(2);
    }

    public HashMap<Integer, Integer> iterativeMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        long hash = game.getHash();
        GameEnvironment stateCopy;

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
        LinkedHashMap<Integer, GameEnvironment> states = getStates(game, hash);
        for (int moveIndex : states.keySet()) {
            stateCopy = states.get(moveIndex);

            hash = game.update(currentPlayer, moveIndex);
            if (transpositionTable.containsKey(hash)) {
                newScore = transpositionTable.get(hash);
            } else {
                results = iterativeDeepMove(stateCopy, depth - 1, -10000, 10000);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
                transpositionTable.put(hash, newScore);
            }
            hash = game.update(currentPlayer, moveIndex);
            previousScores.get(hash).put(moveIndex, newScore);
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if (newScore == bestScore) {
                bestMovePlace.add(moveIndex);
            }
        }
        results.put(2, bestMovePlace.get((int) (Math.random() * bestMovePlace.size())));
        return results;
    }

    public HashMap<Integer, Integer> iterativeDeepMove(GameEnvironment game, int depth, int alpha, int beta)
            throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        long hash = game.getHash();
        GameEnvironment stateCopy;

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

        LinkedHashMap<Integer, GameEnvironment> states = getStates(game, hash);

        for (int moveIndex : states.keySet()) {
            stateCopy = states.get(moveIndex);

            hash = game.update(currentPlayer, moveIndex);
            if (transpositionTable.containsKey(hash)) {
                newScore = transpositionTable.get(hash);
            } else {
                results = iterativeDeepMove(stateCopy, depth - 1, alpha, beta);
                if (results.containsKey(3)) {
                    return results;
                }
                newScore = results.get(2);
                transpositionTable.put(hash, newScore);
            }
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }
            hash = game.update(currentPlayer, moveIndex);
            previousScores.get(hash).put(moveIndex, newScore);

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

    public ArrayList<Integer> sortByValue(HashMap<Integer, Integer> hm) {
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

    public LinkedHashMap<Integer, GameEnvironment> getStates(GameEnvironment game, long hash) throws Exception {
        LinkedHashMap<Integer, GameEnvironment> results = new LinkedHashMap<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves();
        ArrayList<Integer> indexes = new ArrayList<>();
        if (previousScores.containsKey(hash)) {
            HashMap<Integer, Integer> moves = previousScores.get(hash);
            indexes = sortByValue(moves);
        } else {
            previousStates.put(hash, new HashMap<>());
            previousScores.put(hash, new HashMap<>());
        }

        for (int moveIndex : indexes) {
            results.put(moveIndex, previousStates.get(hash).get(moveIndex));
        }
        if (indexes.size() < legalMoves.size()) {
            GameEnvironment gameCopy;
            for (int moveIndex : legalMoves) {
                if (!results.containsKey(moveIndex)) {
                    gameCopy = game.copy();
                    gameCopy.move(moveIndex);
                    previousStates.get(hash).put(moveIndex, gameCopy);
                    results.put(moveIndex, gameCopy);
                }
            }
        }

        return results;
    }

}
