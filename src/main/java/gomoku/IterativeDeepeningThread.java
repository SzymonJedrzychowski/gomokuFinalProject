package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IterativeDeepeningThread extends Thread {
    private int globalDepth;
    private int depthLimit;
    private GameEnvironment game;
    private HashMap<Long, ArrayList<Integer>> transpositionTable;
    private HashMap<Long, Integer> previousScores;
    private HashMap<Long, ArrayList<Integer>> largestTT = new HashMap<>();
    private HashMap<String, Integer> results;
    private boolean onlyCloseMoves;
    private int moveCount;

    IterativeDeepeningThread(int depthLimit, GameEnvironment game, boolean onlyCloseMoves) {
        this.depthLimit = depthLimit;
        this.game = game;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public void run() {
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

                if (results.get("bestScore") > 5000 || globalDepth > game.getBoardSpace()) {
                    globalDepth = depthLimit + 1;
                    break;
                }
            }
        } catch (Exception e) {
            if (globalDepth <= depthLimit) {
                System.out.println("Problem with thread.");
                System.out.println(e);
                System.out.printf("%d %d%n", globalDepth, depthLimit);
            }
        }
    }

    public HashMap<String, Integer> getResults() {
        return results;
    }

    public boolean isFinished() {
        return globalDepth > depthLimit;
    }

    public void finishThread() throws Exception {
        globalDepth = depthLimit + 1;
        transpositionTable = null;
        previousScores = null;
    }

    public HashMap<Long, ArrayList<Integer>> getLargestTT() {
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
        moveCount = 0;

        int currentPlayer = gameState.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;

        HashMap<String, Integer> moveResults = new HashMap<>();

        ArrayList<Integer> legalMoves = getMoves(gameState);

        for (int moveIndex : legalMoves) {
            try {
                gameState.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Iterative Deepening: " + e);
            }

            gameState.update(currentPlayer, moveIndex);

            moveResults = deepMove(gameState, depth - 1, -beta, -alpha);

            newScore = -moveResults.get("bestScore");
            moveCount += 1;

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore);

            gameState.update(currentPlayer, moveIndex);
            gameState.undoMove(moveIndex);
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
        if (depth == 0) {
            results = game.evaluateBoard();
        } else {
            results = game.ifTerminal();
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
            moveResults.put("bestScore", currentPlayer * results.get(2));
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(currentPlayer * results.get(2), 0)));
            return moveResults;
        }

        ArrayList<Integer> legalMoves = getMoves(game);

        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            moveResults = deepMove(game, depth - 1, -beta, -alpha);

            newScore = -moveResults.get("bestScore");
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
        }

        if (bestScore <= startAlpha) {
            flag = 2;
        } else if (bestScore >= beta) {
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
