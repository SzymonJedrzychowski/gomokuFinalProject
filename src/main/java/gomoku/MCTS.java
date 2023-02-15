package gomoku;

import java.sql.Timestamp;
import java.util.HashMap;

public class MCTS extends Player {
    int timeLimit;
    float explorationValue;
    boolean onlyCloseMoves;

    MCTS(int timeLimit) {
        this.timeLimit = timeLimit;
        this.onlyCloseMoves = false;
    }

    MCTS(int timeLimit, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public int move(GameEnvironment state) throws Exception {
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp2;
        int moveCount = 0;

        MCTS_node currentNode = new MCTS_node(state, null, onlyCloseMoves);
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
                scores.put(moveIndex, (float) ((child.stats[0] + child.stats[1] * 0.5) / child.visits));
            } else {
                scores.put(moveIndex, (float) ((child.stats[2] + child.stats[1] * 0.5) / child.visits));
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
        //System.out.printf("%-30s time: %10d moveCount: %10d %n", "MCTS",
        //        timestamp2.getTime() - timestamp1.getTime(), moveCount);

        return bestMovePlace;
    }
}