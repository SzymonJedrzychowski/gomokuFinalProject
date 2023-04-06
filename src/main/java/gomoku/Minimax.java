package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

import org.openjdk.jol.info.GraphLayout;

public class Minimax extends Player {
    int globalDepth;
    HashMap<Long, Integer> transpositionTable;
    int moveCount;
    boolean onlyCloseMoves;

    Minimax(int globalDepth, boolean onlyCloseMoves) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        long startTimestamp = System.nanoTime();
        transpositionTable = new HashMap<>();
        moveCount = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestMovePlace = -1;
        ArrayList<Integer> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int newScore;

        game.hashInit();

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1);
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
                bestMoves.clear();
                bestMoves.add(moveIndex);
            } else if (newScore == bestScore) {
                bestMoves.add(moveIndex);
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);
        }

        long endTimestamp = System.nanoTime();

        MoveData moveData = new MoveData(endTimestamp - startTimestamp, moveCount, bestMovePlace,
                GraphLayout.parseInstance(this).totalSize(),
                bestScore);
        transpositionTable = null;
        return moveData;
    }

    public int deepMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;

        long hash = game.getHash();

        if (transpositionTable.containsKey(hash)) {
            return transpositionTable.get(hash);
        }

        HashMap<Integer, Integer> results;
        if (depth == 0) {
            results = game.evaluateBoard();
        } else {
            results = game.ifTerminal();
        }

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                transpositionTable.put(hash, 0);
                return 0;
            }
            transpositionTable.put(hash, Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10);
            return Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10;
        } else if (depth == 0) {
            transpositionTable.put(hash, currentPlayer * results.get(2));
            return currentPlayer * results.get(2);
        }

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);
            newScore = -deepMove(game, depth - 1);
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        transpositionTable.put(hash, bestScore);

        return bestScore;
    }

}
