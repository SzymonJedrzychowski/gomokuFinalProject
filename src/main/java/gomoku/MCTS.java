package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import org.openjdk.jol.info.GraphLayout;

public class MCTS extends Player {
    int timeLimit;
    boolean onlyCloseMoves;

    MCTS(int timeLimit, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    @Override
    public MoveData move(GameEnvironment state) throws Exception {
        long startTimestamp = System.nanoTime();
        long endTimestamp;
        int moveCount = 0;

        MCTS_node currentNode = new MCTS_node(state, null, onlyCloseMoves);
        MCTS_node selectedNode;
        do {
            endTimestamp = System.nanoTime();
            selectedNode = currentNode.select();
            while (selectedNode != null) {
                selectedNode = selectedNode.select();
            }
            moveCount += 1;
        } while (endTimestamp - startTimestamp < (long)timeLimit * 1000000);

        MCTS_node child;
        float bestValue = Float.NEGATIVE_INFINITY;
        int bestMovePlace = -1;
        float moveValue;

        ArrayList<Integer> keys = new ArrayList<>(currentNode.children.keySet());
        Collections.shuffle(keys);
        for (int moveIndex : keys) {
            child = currentNode.children.get(moveIndex);
            if (state.getCurrentPlayer() == 1) {
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
        MoveData moveData = new MoveData(endTimestamp - startTimestamp,
                moveCount,
                bestMovePlace,
                GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(currentNode).totalSize(),
                0);
        return moveData;
    }
}