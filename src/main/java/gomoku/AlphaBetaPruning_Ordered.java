package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

/**
 * Class resposbile for Alpha-Beta Pruning (Ordered) agent.
 */
public class AlphaBetaPruning_Ordered extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    /**
     * @param globalDepth    depth of the search
     * @param onlyCloseMoves if only close moves should be used
     * @param gatherMemory   if memory should be gathered
     */
    AlphaBetaPruning_Ordered(int globalDepth, boolean onlyCloseMoves, boolean gatherMemory) {
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

        // Decide if moves should be ordered or not
        ArrayList<Integer> legalMoves;
        if (globalDepth > 1) {
            legalMoves = sortMoves(game);
        } else {
            legalMoves = game.getLegalMoves(onlyCloseMoves);
        }

        // Iterate through possible moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -beta, -alpha); // Get value from deeper search

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore); // Update Alpha

            game.updateHash(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        long endTimestamp = System.nanoTime();

        // Gather the data of the move
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

        // Decide if moves should be ordered or not
        ArrayList<Integer> legalMoves;
        if (globalDepth - depth <= Math.min(2, globalDepth - 2)) {
            legalMoves = sortMoves(game);
        } else {
            legalMoves = game.getLegalMoves(onlyCloseMoves);
        }

        // Iterate through the moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -beta, -alpha); // Get value from deeper search

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.undoMove(moveIndex);
            game.updateHash(currentPlayer, moveIndex);

            alpha = Math.max(alpha, newScore); // Update the Alpha

            // Check for cutoff
            if (alpha >= beta) {
                break;
            }
        }

        // Select the flag for transposition table
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

    /**
     * Method used to sort the moves
     * 
     * @param game game environment
     * @return ArrayList of moves
     * @throws Exception if error ocurred while playing the move
     */
    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception {
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        int bestScore = Integer.MIN_VALUE;
        int bestIndex = -1;
        int tempScore;

        // Iterate through the possible moves
        for (int moveIndex : legalMoves) {
            game.move(moveIndex);
            tempScore = game.evaluateBoard().get(2); // Evaluate the board state
            game.undoMove(moveIndex);
            if (game.getCurrentPlayer() * tempScore > bestScore) {
                bestScore = tempScore;
                bestIndex = moveIndex;
            }
        }

        sortedMoves.add(bestIndex); // Add the best move first
        // Add remaining moves to the ArrayList
        for (int moveIndex : legalMoves) {
            if (moveIndex != bestIndex) {
                sortedMoves.add(moveIndex);
            }
        }
        return sortedMoves;
    }
}