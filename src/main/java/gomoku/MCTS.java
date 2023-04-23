package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import org.openjdk.jol.info.GraphLayout;

/**
 * Class resposbile for Monte Carlo Tree Search agent.
 */
public class MCTS extends Player {
    int timeLimit;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    /**
     * @param timeLimit      limit of the search
     * @param onlyCloseMoves if only close moves should be used
     * @param gatherMemory   if memory should be gathered
     */
    MCTS(int timeLimit, boolean onlyCloseMoves, boolean gatherMemory) {
        this.timeLimit = timeLimit;
        this.onlyCloseMoves = onlyCloseMoves;
        this.gatherMemory = gatherMemory;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        long startTimestamp = System.nanoTime();
        long endTimestamp;

        // Create node with current game state
        MCTS_Node currentNode = new MCTS_Node(gameState, null, onlyCloseMoves);
        MCTS_Node selectedNode;

        // Select new nodes to search as long as there is time
        do {
            endTimestamp = System.nanoTime();
            selectedNode = currentNode.select();
            while (selectedNode != null) {
                selectedNode = selectedNode.select();
            }
        } while (endTimestamp - startTimestamp < (long) timeLimit * 1000000);

        MCTS_Node child;
        float bestValue = Float.NEGATIVE_INFINITY;
        int bestMovePlace = -1;
        float moveValue;

        // Evaluate the nodes in random order to determine one has the largest win rate
        ArrayList<Integer> keys = new ArrayList<>(currentNode.children.keySet());
        Collections.shuffle(keys);
        for (int moveIndex : keys) {
            child = currentNode.children.get(moveIndex);
            if (gameState.getCurrentPlayer() == 1) {
                moveValue = (float) ((child.stats[0] + child.stats[1] * 0.5) / child.visits);
            } else {
                moveValue = (float) ((child.stats[2] + child.stats[1] * 0.5) / child.visits);
            }
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMovePlace = moveIndex;
            }
        }

        endTimestamp = System.nanoTime();

        // Gather the data of the move
        MoveData moveData;
        if (gatherMemory) {
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp,
                    GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(currentNode).totalSize());
        } else {
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp);
        }

        return moveData;
    }
}