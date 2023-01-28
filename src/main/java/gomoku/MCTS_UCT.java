package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class MCTS_UCT extends Player {
    int timeLimit;
    float explorationValue;
    boolean simulationLimitIsMoves;

    MCTS_UCT(int timeLimit, float explorationValue) {
        this.timeLimit = timeLimit;
        this.explorationValue = explorationValue;
    }

    MCTS_UCT(int timeLimit) {
        this.timeLimit = timeLimit;
        this.explorationValue = 1.4f;
    }

    public int move(GameEnvironment state) throws Exception {
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp2;
        int moveCount = 0;

        MCTS_UCT_node currentNode = new MCTS_UCT_node(state, null);
        MCTS_UCT_node selectedNode;

        do {
            timestamp2 = new Timestamp(System.currentTimeMillis());
            selectedNode = currentNode.select(explorationValue);
            while (selectedNode != null) {
                selectedNode = selectedNode.select(explorationValue);
            }
            moveCount += 1;
        } while (timestamp2.getTime() - timestamp1.getTime() < timeLimit);
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
        ArrayList<Integer> moves = new ArrayList<>();
        float moveValue;

        for (int moveIndex : UCB.keySet()) {
            moveValue = UCB.get(moveIndex);
            if (moveValue > bestValue) {
                bestValue = moveValue;
                moves.clear();
                moves.add(moveIndex);
            } else if (moveValue == bestValue) {
                moves.add(moveIndex);
            }
        }

        timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s time: %10d moveCount: %10d %n", "MCTS_UCT",
                timestamp2.getTime() - timestamp1.getTime(), moveCount);

        return moves.get((int) (Math.random() * moves.size()));
    }
}