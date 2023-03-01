package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class AlphaBetaPruning_Ordered extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable;
    int moveCount;
    boolean onlyCloseMoves;

    AlphaBetaPruning_Ordered(int globalDepth, boolean onlyCloseMoves) {
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

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;

        game.hashInit();

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

        long endTimestamp = System.nanoTime();
        MoveData moveData = new MoveData(endTimestamp - startTimestamp, moveCount, bestMovePlace,
                GraphLayout.parseInstance(this).totalSize(),
                bestScore);
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
        if(depth == 0){
            results = game.evaluateBoard();
        }else{
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
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(currentPlayer*results.get(2), 0)));
            return currentPlayer*results.get(2);
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

    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception {
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        int bestScore = Integer.MIN_VALUE;
        int bestIndex = -1;

        int tempScore;
        for (int moveIndex : legalMoves) {
            game.move(moveIndex);
            tempScore = game.evaluateBoard().get(2);
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