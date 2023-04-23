package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class responsible for Principal Variation Search agent (thread).
 */
public class PrincipalVariationSearch_Thread extends Thread {
    private int globalDepth;
    private final int depthLimit;
    private GameEnvironment game;
    private HashMap<Long, ArrayList<Integer>> transpositionTable;
    private HashMap<Long, Integer> previousScores;
    private HashMap<Long, ArrayList<Integer>> largestTT = new HashMap<>();
    private HashMap<String, Integer> results;
    private final boolean onlyCloseMoves;

    /**
     * Constructor for depth-limited search.
     * 
     * @param depthLimit     depth of the search
     * @param game           game environment
     * @param onlyCloseMoves if only close moves should be used
     */
    PrincipalVariationSearch_Thread(int depthLimit, GameEnvironment game, boolean onlyCloseMoves) {
        this.depthLimit = depthLimit;
        this.game = game;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    /**
     * Constructor for time-limited search.
     * 
     * @param gamegame         game environment
     * @param onlyCloseMovesif only close moves should be used
     */
    PrincipalVariationSearch_Thread(GameEnvironment game, boolean onlyCloseMoves) {
        this.depthLimit = Integer.MAX_VALUE - 10;
        this.game = game;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    /**
     * Method used to start the thread for time-limited search.
     */
    public void run() {
        startNormally();
    }

    /**
     * Method used to start the thread for depth-limited search.
     */
    public void startNormally() {
        transpositionTable = new HashMap<>();
        previousScores = new HashMap<>();
        globalDepth = 1;

        game.hashInit();

        try {
            // Run the search with higher depth
            for (globalDepth = 1; globalDepth <= depthLimit; globalDepth++) {
                results = iterativeMove(game, globalDepth);

                // Update the largest transposition table
                if (transpositionTable.size() > largestTT.size()) {
                    largestTT = new HashMap<>(transpositionTable);
                }

                // Finish the search if winning move was found
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

    /**
     * Method used to get previous results.
     * 
     * @return HashMap of results
     */
    public HashMap<String, Integer> getResults() {
        return results;
    }

    /**
     * Method used to check if game was finished.
     * 
     * @return
     */
    public boolean isFinished() {
        return globalDepth > depthLimit;
    }

    /**
     * Method used to finish the thread.
     */
    public void finishThread() {
        globalDepth = depthLimit + 1;

        // Make the variables null, so that the search throws an exception
        transpositionTable = null;
        previousScores = null;
    }

    /**
     * Method used to get the largest transposition table.
     * 
     * @return the largest transposition table
     */
    public HashMap<Long, ArrayList<Integer>> getLargestTT() {
        if (transpositionTable.size() > largestTT.size()) {
            return transpositionTable;
        }
        return largestTT;
    }

    /**
     * Method used to get the HashMap of previous scores.
     * 
     * @return HashMap of previous scores
     */
    public HashMap<Long, Integer> getPreviousScores() {
        return previousScores;
    }

    /**
     * Method used to run the search.
     * 
     * @param gameState game environment
     * @param depth     depth of the search
     * @return best move from the search
     * @throws Exception if error occurred while playing the move
     */
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

        // Iterate through possible moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -b, -alpha); // Get value from deeper search

            // Do second PVS search
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, globalDepth - 1, -beta, -alpha);
            }

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore); // Update the Alpha
            b = alpha + 1; // Update the tighter search value

            game.updateHash(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        previousScores.put(game.getHash(), bestMovePlace);

        HashMap<String, Integer> moveResults = new HashMap<>();
        moveResults.put("bestMove", bestMovePlace);
        moveResults.put("bestScore", bestScore);

        return moveResults;
    }

    /**
     * Method used to search deeper.
     * 
     * @param game  game environment
     * @param depth remaining depth
     * @param alpha Alpha
     * @param beta  Beta
     * @return score from the sub-tree
     * @throws Exception if error occurred while playing the move
     */
    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;
        int bestMovePlace = -1;
        int startAlpha = alpha;

        long hash = game.getHash();
        ArrayList<Integer> tempArray;
        int flag;

        // If gameState occurred in the transposition table, decide how to use it
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

        // Evaluate the game
        HashMap<Integer, Integer> results;
        if (depth == 0) {
            results = game.evaluateBoard();
        } else {
            results = game.ifTerminal();
        }

        // If game is over or depth is 0, return the score
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

        // Iterate through the moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -b, -alpha); // Get value from deeper search

            // Do second PVS search
            if (newScore > alpha && newScore < beta && legalMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, depth - 1, -beta, -alpha);
            }

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            game.undoMove(moveIndex);
            game.updateHash(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore); // Update the Alpha

            // Check for cutoff
            if (alpha >= beta) {
                break;
            }
            b = alpha + 1; // Update the tighter search value
        }

        // Select the flag for transposition table
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

    /**
     * Method used to get moves.
     * 
     * @param game game environment
     * @return ArrayList of moves
     */
    public ArrayList<Integer> getMoves(GameEnvironment game) {
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        // Check if game state was previously played
        if (!previousScores.containsKey(game.getHash())) {
            return legalMoves; // If no, return unordered moves
        }

        ArrayList<Integer> sortedMoves = new ArrayList<>();
        int bestIndex = previousScores.get(game.getHash());
        sortedMoves.add(bestIndex); // Add the best move first
        // Add remaining moves to the ArrayList
        for (int moveIndex : legalMoves) {
            if (bestIndex != moveIndex) {
                sortedMoves.add(moveIndex);
            }
        }

        return sortedMoves;
    }
}
