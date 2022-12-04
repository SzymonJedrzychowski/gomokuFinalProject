package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

public class Minimax extends Player {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<Long, Integer> transpositionTable = new HashMap<>();
    int count;

    Minimax(int globalDepth) {
        this.globalDepth = globalDepth;
    }

    public int move(GameEnvironment game) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        transpositionTable.clear();
        game.hashInit();
        count = 0;

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());

        ArrayList<Integer> legalMoves = game.getLegalMoves();

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = deepMove(game, globalDepth - 1);
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
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s: %d time: %10d%n", "Minimax", currentPlayer,
                timestamp2.getTime() - timestamp1.getTime());
        System.out.println(count);
        return bestMovePlace.get((int) (Math.random() * bestMovePlace.size()));
    }

    public int deepMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        long hash = game.getHash();

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            return results.get(1) * 5000 - results.get(1) * 10 * (globalDepth - depth);
        }

        if (depth == 0) {
            return evaluator.calculateEvaluation(game);
        }

        ArrayList<Integer> legalMoves = game.getLegalMoves();

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
                newScore = deepMove(game, depth - 1);
                transpositionTable.put(hash, newScore);
            }
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }
            hash = game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }
        return bestScore;
    }

}
