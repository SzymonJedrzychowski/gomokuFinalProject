package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import org.openjdk.jol.info.GraphLayout;

/**
 * Class resposbile for Monte Carlo Tree Search with UCT agent.
 */
public class MCTS_UCT extends Player {
    int timeLimit;
    float explorationValue;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    /**
     * @param timeLimit      limit of the search
     * @param onlyCloseMoves if only close moves should be used
     * @param gatherMemory   if memory should be gathered
     */
    MCTS_UCT(int timeLimit, boolean onlyCloseMoves, boolean gatherMemory) {
        this.timeLimit = timeLimit;
        this.explorationValue = (float) 1.41;
        this.onlyCloseMoves = onlyCloseMoves;
        this.gatherMemory = gatherMemory;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        long startTimestamp = System.nanoTime();
        long endTimestamp;

        // Create node with current game state
        MCTS_UCT_Node currentNode = new MCTS_UCT_Node(gameState, null, onlyCloseMoves);
        MCTS_UCT_Node selectedNode;

        // Select new nodes to search as long as there is time
        do {
            endTimestamp = System.nanoTime();
            selectedNode = currentNode.select(explorationValue);
            while (selectedNode != null) {
                selectedNode = selectedNode.select(explorationValue);
            }
        } while (endTimestamp - startTimestamp < (long) timeLimit * 1000000);

        MCTS_UCT_Node child;
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