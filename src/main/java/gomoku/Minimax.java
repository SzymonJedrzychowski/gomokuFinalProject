package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

import org.openjdk.jol.info.GraphLayout;

/**
 * Class responsible for Minimax agent.
 */
public class Minimax extends Player {
    int globalDepth;
    HashMap<Long, Integer> transpositionTable;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    /**
     * @param globalDepth    depth of the search
     * @param onlyCloseMoves if only close moves should be used
     */
    Minimax(int globalDepth, boolean onlyCloseMoves, boolean gatherMemory) {
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

        int bestMovePlace = -1;
        int bestScore = Integer.MIN_VALUE;
        int newScore;

        game.hashInit();

        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);

        // Iterate through possible moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1); // Get value from deeper search

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            game.undoMove(moveIndex);
            game.updateHash(currentPlayer, moveIndex);
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

        return moveData;
    }

    /**
     * Method used to search deeper.
     * 
     * @param game  game environment
     * @param depth remaining depth
     * @return score from the sub-tree
     * @throws Exception if error occurred while playing the move
     */
    public int deepMove(GameEnvironment game, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int newScore;

        long hash = game.getHash();

        // Check if hash is in transposition table
        if (transpositionTable.containsKey(hash)) {
            return transpositionTable.get(hash);
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

        // Iterate through the moves
        for (int moveIndex : legalMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.updateHash(currentPlayer, moveIndex);
            newScore = -deepMove(game, depth - 1); // Get value from deeper search

            // Change the best move if new move is better
            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.updateHash(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        transpositionTable.put(hash, bestScore);

        return bestScore;
    }

}
