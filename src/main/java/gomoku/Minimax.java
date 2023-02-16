package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

public class Minimax extends Player {
    int globalDepth;
    HashMap<Long, Integer> transpositionTable = new HashMap<>();
    int moveCount;
    boolean onlyCloseMoves;

    Minimax(int globalDepth) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = false;
    }

    Minimax(int globalDepth, boolean onlyCloseMoves) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public int move(GameEnvironment gameState) throws Exception {
        moveCount = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestMovePlace = -1;
        ArrayList<Integer> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int newScore;

        game.hashInit();

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

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
            }else if(newScore == bestScore){
                bestMoves.add(moveIndex);
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);
        }

        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());

        //System.out.printf("%-30s: player %2d time: %10d moveCount: %10d%n", "Minimax",
        //        currentPlayer,
        //        endTimestamp.getTime() - startTimestamp.getTime(), moveCount);
        transpositionTable.clear();
        return bestMovePlace;
    }

    public int deepMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;

        long hash = game.getHash();

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                return 0;
            }
            return Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10;
        }

        if (depth == 0) {
            return currentPlayer * game.evaluateBoard();
        }

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

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
                newScore = -deepMove(game, depth - 1);
                moveCount += 1;
                transpositionTable.put(hash, newScore);
            }

            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        return bestScore;
    }

}
