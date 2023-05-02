package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

/**
 * Class resposbile for Alpha-Beta Pruning agent.
 */
public class AlphaBetaPruning extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    /**
     * @param globalDepth    depth of the search
     * @param onlyCloseMoves if only close moves should be used
     * @param gatherMemory   if memory should be gathered
     */
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

        // Iterate through the moves
        for (int moveIndex : legalMoves) {
            game.move(moveIndex);
            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -beta, -alpha); // Get value from deeper search

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            alpha = Math.max(alpha, newScore); // Update the Alpha

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

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

        // Iterate through the moves
        for (int moveIndex : legalMoves) {
            game.move(moveIndex);
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

}
