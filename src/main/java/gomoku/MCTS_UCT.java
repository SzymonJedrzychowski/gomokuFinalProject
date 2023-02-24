package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class MCTS_UCT extends Player {
    int timeLimit;
    float explorationValue;
    boolean simulationLimitIsMoves;
    boolean onlyCloseMoves;

    MCTS_UCT(int timeLimit, float explorationValue, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.explorationValue = explorationValue;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    MCTS_UCT(int timeLimit, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.explorationValue = 1.4f;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public MoveData move(GameEnvironment state) throws Exception {
        int moveCount = 0;
        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp endTimestamp;

        MCTS_UCT_node currentNode = new MCTS_UCT_node(state, null, onlyCloseMoves);
        MCTS_UCT_node selectedNode;

        do {
            endTimestamp = new Timestamp(System.currentTimeMillis());
            selectedNode = currentNode.select(explorationValue);
            while (selectedNode != null) {
                selectedNode = selectedNode.select(explorationValue);
            }
            moveCount += 1;
        } while (endTimestamp.getTime() - startTimestamp.getTime() < timeLimit);
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

        endTimestamp = new Timestamp(System.currentTimeMillis());
        // System.out.printf("%-30s time: %10d moveCount: %10d %n", "MCTS_UCT",
        // endTimestamp.getTime() - startTimestamp.getTime(), moveCount);
        MoveData moveData = new MoveData(endTimestamp.getTime() - startTimestamp.getTime(), moveCount,
                moves.get((int) (Math.random() * moves.size())), 0, 0);
        return moveData;
    }
}