package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class AlphaBetaPruning extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    AlphaBetaPruning(int globalDepth, boolean onlyCloseMoves, boolean gatherMemory) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = onlyCloseMoves;
        this.gatherMemory = gatherMemory;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        long startTimestamp = System.nanoTime();
        transpositionTable = new HashMap<>();

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;

        game.hashInit();

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -beta, -alpha);

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);

            game.updateHash(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        long endTimestamp = System.nanoTime();

        MoveData moveData;
        if (gatherMemory) {
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp,
                    GraphLayout.parseInstance(this).totalSize());
        } else {
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp);
        }
        transpositionTable = null;

        return moveData;
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;
        int startAlpha = alpha;

        long hash = game.getHash();
        ArrayList<Integer> tempArray;
        int flag;
        if (transpositionTable.containsKey(hash)) {
            tempArray = transpositionTable.get(hash);
            flag = tempArray.get(1);
            newScore = tempArray.get(0);
            switch (flag) {
                case 0 -> {
                    return newScore;
                }
                case 1 -> alpha = Math.max(alpha, newScore);
                case 2 -> beta = Math.min(beta, newScore);
                default -> {
                }
            }
            if (alpha >= beta) {
                return newScore;
            }
        }

        HashMap<Integer, Integer> results;
        if (depth == 0) {
            results = game.evaluateBoard();
        } else {
            results = game.ifTerminal();
        }

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                transpositionTable.put(hash, new ArrayList<>(Arrays.asList(0, 0)));
                return 0;
            }
            transpositionTable.put(hash,
                    new ArrayList<>(Arrays.asList(Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10, 0)));
            return Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10;
        } else if (depth == 0) {
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(currentPlayer * results.get(2), 0)));
            return currentPlayer * results.get(2);
        }

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -beta, -alpha);

            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.undoMove(moveIndex);
            game.updateHash(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore);

            if (alpha >= beta) {
                break;
            }
        }

        if (bestScore <= startAlpha) {
            flag = 2;
        } else if (bestScore >= beta) {
            flag = 1;
        } else {
            flag = 0;
        }
        transpositionTable.put(game.getHash(), new ArrayList<>(Arrays.asList(bestScore, flag)));

        return bestScore;
    }

}
