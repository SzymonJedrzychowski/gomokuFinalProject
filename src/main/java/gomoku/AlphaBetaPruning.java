package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

public class AlphaBetaPruning {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<String, Integer> transpositionTable = new HashMap<>();

    AlphaBetaPruning(int globalDepth) {
        this.globalDepth = globalDepth;
    }

    public int move(GameEnvironment game) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        transpositionTable.clear();

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());

        ArrayList<Integer> legalMoves = game.getLegalMoves();
        GameEnvironment stateCopy;

        for (int moveIndex : legalMoves) {
            stateCopy = game.copy();
            try {
                stateCopy.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            newScore = deepMove(stateCopy, globalDepth - 1, -10000, 10000);
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if ((newScore == bestScore && currentPlayer == 1)
                    || (newScore == bestScore && currentPlayer == -1)) {
                bestMovePlace.add(moveIndex);
            }
        }
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s: %d time: %10d%n", "AlphaBetaPruning", currentPlayer, timestamp2.getTime() - timestamp1.getTime());
        return bestMovePlace.get((int) (Math.random() * bestMovePlace.size()));
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int bestScore = -10000 * currentPlayer;
        int newScore;
        String hs;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            return results.get(1) * 1000 - results.get(1)*10*(globalDepth-depth);
        }

        if (depth == 0) {
            return evaluator.calculateEvaluation(game);
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
                newScore = deepMove(stateCopy, depth - 1, alpha, beta);
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
        return bestScore;
    }

}
