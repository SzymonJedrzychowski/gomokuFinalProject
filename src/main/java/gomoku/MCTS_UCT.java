package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class MCTS_UCT extends Player {
    int timeLimit;
    float explorationValue;
    boolean onlyCloseMoves;

    MCTS_UCT(int timeLimit, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.explorationValue = (float) 1.41;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    @Override
    public MoveData move(GameEnvironment state) throws Exception {
        int moveCount = 0;
        long startTimestamp = System.nanoTime();
        long endTimestamp;

        MCTS_UCT_node currentNode = new MCTS_UCT_node(state, null, onlyCloseMoves);
        MCTS_UCT_node selectedNode;

        do {
            endTimestamp = System.nanoTime();
            selectedNode = currentNode.select(explorationValue);
            while (selectedNode != null) {
                selectedNode = selectedNode.select(explorationValue);
            }
            moveCount += 1;
        } while (endTimestamp - startTimestamp < timeLimit * 1000000);
        HashMap<Integer, Float> UCB = new HashMap<>();
        MCTS_UCT_node child;

        for (int moveIndex : currentNode.children.keySet()) {
            child = currentNode.children.get(moveIndex);
            if (state.getCurrentPlayer() == 1) {
                UCB.put(moveIndex, (float) ((child.stats[0] + child.stats[1] * 0.5) / child.visits));
            } else {
                UCB.put(moveIndex, (float) ((child.stats[2] + child.stats[1] * 0.5) / child.visits));
            }
        }

        float bestValue = Float.NEGATIVE_INFINITY;
        int bestMovePlace = -1;
        float moveValue;

        ArrayList<Integer> keys = new ArrayList<>(UCB.keySet());
        Collections.shuffle(keys);
        for (int moveIndex : keys) {
            moveValue = UCB.get(moveIndex);
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMovePlace = moveIndex;
            }
        }

        endTimestamp = System.nanoTime();
        MoveData moveData = new MoveData(endTimestamp - startTimestamp,
                moveCount,
                bestMovePlace,
                GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(currentNode).totalSize(), 0);
        return moveData;
    }
}