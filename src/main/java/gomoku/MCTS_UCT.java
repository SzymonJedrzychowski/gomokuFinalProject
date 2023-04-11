package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class MCTS_UCT extends Player {
    int timeLimit;
    float explorationValue;
    boolean onlyCloseMoves;
    boolean gatherMemory;

    MCTS_UCT(int timeLimit, boolean onlyCloseMoves, boolean gatherMemory) {
        this.timeLimit = timeLimit;
        this.explorationValue = (float) 1.41;
        this.onlyCloseMoves = onlyCloseMoves;
        this.gatherMemory = gatherMemory;
    }

    @Override
    public MoveData move(GameEnvironment state) throws Exception {
        long startTimestamp = System.nanoTime();
        long endTimestamp;

        MCTS_UCT_Node currentNode = new MCTS_UCT_Node(state, null, onlyCloseMoves);
        MCTS_UCT_Node selectedNode;

        do {
            endTimestamp = System.nanoTime();
            selectedNode = currentNode.select(explorationValue);
            while (selectedNode != null) {
                selectedNode = selectedNode.select(explorationValue);
            }
        } while (endTimestamp - startTimestamp < (long) timeLimit * 1000000);
        HashMap<Integer, Float> UCB = new HashMap<>();
        MCTS_UCT_Node child;

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

        MoveData moveData;
        if(gatherMemory){
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp,
                GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(currentNode).totalSize());
        }else{
            moveData = new MoveData(bestMovePlace, endTimestamp - startTimestamp);
        }

        return moveData;
    }
}