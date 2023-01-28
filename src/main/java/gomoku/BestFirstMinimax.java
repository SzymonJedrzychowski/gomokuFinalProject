package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class BestFirstMinimax extends Player {
    int timeLimit;
    Evaluator evaluator = new Evaluator();

    BestFirstMinimax(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int move(GameEnvironment state) throws Exception {
        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp2;
        int moveCount = 0;

        BestFirstMinimax_node currentNode = new BestFirstMinimax_node(state, null, 1);
        BestFirstMinimax_node selectedNode = currentNode;
        BestFirstMinimax_node nextNode;

        do {
            timestamp2 = new Timestamp(System.currentTimeMillis());
            nextNode = currentNode.select();
            while (nextNode != null) {
                selectedNode = nextNode;
                nextNode = selectedNode.select();
            }
            selectedNode.expand();
            selectedNode.evaluate(evaluator);
            moveCount += 1;
        } while (timestamp2.getTime() - timestamp1.getTime() < timeLimit);

        HashMap<Integer, Integer> UCB = new HashMap<>();
        BestFirstMinimax_node child;

        for (int moveIndex : currentNode.children.keySet()) {
            child = currentNode.children.get(moveIndex);
            if (state.getCurrentPlayer() == 1) {
                UCB.put(moveIndex, child.score);
            } else {
                UCB.put(moveIndex, -child.score);
            }
        }

        int bestValue = Integer.MIN_VALUE;
        ArrayList<Integer> moves = new ArrayList<>();
        int moveValue;

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
        System.out.printf("%-30s time: %10d moveCount: %10d %n", "BFS",
                timestamp2.getTime() - timestamp1.getTime(), moveCount);

        return moves.get((int) (Math.random() * moves.size()));
    }
}
