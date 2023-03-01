package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class IterativeDeepening_PVS extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable;
    HashMap<Long, Integer> previousScores;
    int moveCount;
    boolean onlyCloseMoves;

    int simulationLimit;
    long startTimestamp;
    boolean isLimitTime;

    IterativeDeepening_PVS(int simulationLimit, boolean isLimitTime, boolean onlyCloseMoves) {
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public MoveData move(GameEnvironment gameState) throws Exception {
        startTimestamp = System.nanoTime();
        GameEnvironment game = gameState.copy();
        globalDepth = 1;
        transpositionTable = new HashMap<>();
        previousScores = new HashMap<>();

        HashMap<String, Integer> results;
        HashMap<String, Integer> previousResult = new HashMap<>();

        previousResult.put("bestMove", -1);

        game.hashInit();

        do {
            transpositionTable = null;
            results = iterativeMove(game, globalDepth);

            if (!results.containsKey("time")) {
                previousResult = results;
            }

            globalDepth += 1;

            if (globalDepth == 30 || (!isLimitTime && globalDepth - 1 == simulationLimit)) {
                break;
            }
        } while (!results.containsKey("time"));

        long endTimestamp = System.nanoTime();
        MoveData moveData = new MoveData(endTimestamp - startTimestamp, previousResult.get("moveCount"),
                previousResult.get("bestMove"), 
                GraphLayout.parseInstance(this).totalSize(),
                previousResult.get("bestScore"));
        transpositionTable = null;
        previousScores = null;
        return moveData;
    }

    public HashMap<String, Integer> iterativeMove(GameEnvironment gameState, int depth) throws Exception {
        transpositionTable = new HashMap<>();
        moveCount = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
        int b = beta;

        HashMap<String, Integer> moveResults = new HashMap<>();

        if (isLimitTime) {
            if (System.nanoTime() - simulationLimit*1000000 + 1000 > startTimestamp) {
                moveResults.put("time", 1);
                return moveResults;
            }
        }

        ArrayList<Integer> legalMoves = getMoves(game);
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            moveResults = deepMove(game, globalDepth - 1, -b, -alpha);
            if (moveResults.containsKey("time")) {
                return moveResults;
            }
            newScore = -moveResults.get("bestScore");
            moveCount += 1;

            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                moveResults = deepMove(game, globalDepth - 1, -beta, -alpha);
                if (moveResults.containsKey("time")) {
                    return moveResults;
                }
                newScore = -moveResults.get("bestScore");
            }

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);
            b = alpha + 1;

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }
        moveResults.put("bestMove", bestMovePlace);
        moveResults.put("moveCount", moveCount);
        moveResults.put("bestScore", bestScore);

        previousScores.put(game.getHash(), bestMovePlace);

        return moveResults;
    }

    public HashMap<String, Integer> deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;
        HashMap<String, Integer> moveResults = new HashMap<>();
        int bestMovePlace = -1;
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
                    moveResults.put("bestScore", newScore);
                    return moveResults;
                }
                case 1 -> alpha = Math.max(alpha, newScore);
                case 2 -> beta = Math.min(beta, newScore);
                default -> {
                }
            }
            if (alpha >= beta) {
                moveResults.put("bestScore", newScore);
                return moveResults;
            }
        }

        HashMap<Integer, Integer> results;
        if(depth == 0){
            results = game.evaluateBoard();
        }else{
            results = game.ifTerminal();
        }
        
        if (isLimitTime) {
            if (System.nanoTime() - simulationLimit*1000000 + 1000 > startTimestamp) {
                moveResults.put("time", 1);
                return moveResults;
            }
        }

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                moveResults.put("bestScore", 0);
                transpositionTable.put(hash, new ArrayList<>(Arrays.asList(0, 0)));
                return moveResults;
            }
            moveResults.put("bestScore", Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10);
            transpositionTable.put(hash,
                    new ArrayList<>(Arrays.asList(Integer.MIN_VALUE + 1 + (globalDepth - depth) * 10, 0)));
            return moveResults;
        } else if (depth == 0) {
            moveResults.put("bestScore", currentPlayer*results.get(2));
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(currentPlayer*results.get(2), 0)));
            return moveResults;
        }

        ArrayList<Integer> legalMoves = getMoves(game);
        int b = beta;

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            moveResults = deepMove(game, depth - 1, -b, -alpha);
            if (moveResults.containsKey("time")) {
                return moveResults;
            }
            newScore = -moveResults.get("bestScore");
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                moveResults = deepMove(game, depth - 1, -beta, -alpha);
                if (moveResults.containsKey("time")) {
                    return moveResults;
                }
                newScore = -moveResults.get("bestScore");
            }
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            game.undoMove(moveIndex);
            game.update(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore);

            if (alpha >= beta) {
                break;
            }
            b = alpha + 1;
        }

        if (bestScore <= startAlpha) {
            flag = 2;
        } else if (bestScore >= b) {
            flag = 1;
        } else {
            flag = 0;
        }
        transpositionTable.put(game.getHash(), new ArrayList<>(Arrays.asList(bestScore, flag)));
        moveResults.put("bestScore", bestScore);

        previousScores.put(game.getHash(), bestMovePlace);

        return moveResults;
    }

    public ArrayList<Integer> getMoves(GameEnvironment game) {
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        if (!previousScores.containsKey(game.getHash())) {
            return legalMoves;
        }

        ArrayList<Integer> sortedMoves = new ArrayList<>();
        sortedMoves.add(previousScores.get(game.getHash()));
        for (int moveIndex : legalMoves) {
            if (sortedMoves.get(0) != moveIndex) {
                sortedMoves.add(moveIndex);
            }
        }

        return sortedMoves;
    }

}
