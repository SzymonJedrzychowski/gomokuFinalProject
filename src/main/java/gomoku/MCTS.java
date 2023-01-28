package gomoku;

import java.sql.Timestamp;
import java.util.HashMap;

public class MCTS extends Player {
    int timeLimit;
    float explorationValue;

    MCTS(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int move(GameEnvironment state) throws Exception {
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp2;
        int moveCount = 0;

        MCTS_node currentNode = new MCTS_node(state, null);
        MCTS_node selectedNode;
        do {
            timestamp2 = new Timestamp(System.currentTimeMillis());
            selectedNode = currentNode.select();
            while (selectedNode != null) {
                selectedNode = selectedNode.select();
            }
            moveCount += 1;
        } while (timestamp2.getTime() - timestamp1.getTime() < timeLimit);

        HashMap<Integer, Float> scores = new HashMap<>();
        MCTS_node child;

        for (int moveIndex : currentNode.children.keySet()) {
            child = currentNode.children.get(moveIndex);
            if (state.getCurrentPlayer() == 1) {
                scores.put(moveIndex, ((float) child.reward / (float) child.visits));
            } else {
                scores.put(moveIndex, ((float) -child.reward / (float) child.visits));
            }
        }

        float bestValue = Float.NEGATIVE_INFINITY;
        int bestMovePlace = -1;
        float moveValue;

        for (int moveIndex : scores.keySet()) {
            moveValue = scores.get(moveIndex);
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMovePlace = moveIndex;
            }
        }

        timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s time: %10d moveCount: %10d %n", "MCTS",
                timestamp2.getTime() - timestamp1.getTime(), moveCount);

        return bestMovePlace;
    }
}