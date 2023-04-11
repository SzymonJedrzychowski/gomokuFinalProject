package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PrincipalVariationSearch_Thread extends Thread {
    private int globalDepth;
    private final int depthLimit;
    private GameEnvironment game;
    private HashMap<Long, ArrayList<Integer>> transpositionTable;
    private HashMap<Long, Integer> previousScores;
    private HashMap<Long, ArrayList<Integer>> largestTT = new HashMap<>();
    private HashMap<String, Integer> results;
    private final boolean onlyCloseMoves;

    PrincipalVariationSearch_Thread(int depthLimit, GameEnvironment game, boolean onlyCloseMoves) {
        this.depthLimit = depthLimit;
        this.game = game;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    PrincipalVariationSearch_Thread(GameEnvironment game, boolean onlyCloseMoves) {
        this.depthLimit = Integer.MAX_VALUE - 10;
        this.game = game;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public void run() {
        startNormally();
    }

    public void startNormally() {
        transpositionTable = new HashMap<>();
        previousScores = new HashMap<>();
        globalDepth = 1;

        game.hashInit();

        try {
            for (globalDepth = 1; globalDepth <= depthLimit; globalDepth++) {
                results = iterativeMove(game, globalDepth);

                if (transpositionTable.size() > largestTT.size()) {
                    largestTT = new HashMap<>(transpositionTable);
                }

                if (results.get("bestScore") > 25000 || globalDepth > game.getBoardSpace()) {
                    globalDepth = depthLimit + 1;
                    break;
                }
            }
        } catch (Exception e) {
            if (globalDepth <= depthLimit) {
                System.out.printf("Problem with thread: %s%n", e);
            }
        }
    }

    public HashMap<String, Integer> getResults() {
        return results;
    }

    public boolean isFinished() {
        return globalDepth > depthLimit;
    }

    public void finishThread() {
        globalDepth = depthLimit + 1;
        transpositionTable = null;
        previousScores = null;
    }

    public HashMap<Long, ArrayList<Integer>> getLargestTT() throws Exception{
        if (transpositionTable == null || largestTT == null){
            throw new Exception("Provided time was not enough to calculate the move.");
        }

        if (transpositionTable.size() > largestTT.size()) {
            return transpositionTable;
        }
        return largestTT;
    }

    public HashMap<Long, Integer> getPreviousScores() {
        return previousScores;
    }

    public HashMap<String, Integer> iterativeMove(GameEnvironment gameState, int depth) throws Exception {
        transpositionTable = new HashMap<>();

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
        int b = beta;

        ArrayList<Integer> legalMoves = getMoves(game);
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -b, -alpha);

            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, globalDepth - 1, -beta, -alpha);
            }

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);
            b = alpha + 1;

            game.updateHash(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        previousScores.put(game.getHash(), bestMovePlace);

        HashMap<String, Integer> moveResults = new HashMap<>();
        moveResults.put("bestMove", bestMovePlace);
        moveResults.put("bestScore", bestScore);

        return moveResults;
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;
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

        ArrayList<Integer> legalMoves = getMoves(game);
        int b = beta;

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -b, -alpha);
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, depth - 1, -beta, -alpha);
            }

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            game.undoMove(moveIndex);
            game.updateHash(currentPlayer, moveIndex);

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

        previousScores.put(game.getHash(), bestMovePlace);

        return bestScore;
    }

    public ArrayList<Integer> getMoves(GameEnvironment game) {
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        if (!previousScores.containsKey(game.getHash())) {
            return legalMoves;
        }

        ArrayList<Integer> sortedMoves = new ArrayList<>();
        int bestIndex = previousScores.get(game.getHash());
        sortedMoves.add(bestIndex);
        for (int moveIndex : legalMoves) {
            if (bestIndex != moveIndex) {
                sortedMoves.add(moveIndex);
            }
        }

        return sortedMoves;
    }
}
