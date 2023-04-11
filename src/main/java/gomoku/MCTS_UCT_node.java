package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MCTS_UCT_Node {
    GameEnvironment gameState;
    MCTS_UCT_Node parent;
    HashMap<Integer, MCTS_UCT_Node> children = new HashMap<>();
    int[] stats = { 0, 0, 0 };
    int visits = 0;
    boolean onlyCloseMoves;

    MCTS_UCT_Node(GameEnvironment gameState, MCTS_UCT_Node parent, boolean onlyCloseMoves) {
        this.gameState = gameState;
        this.parent = parent;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public MCTS_UCT_Node select(float explorationValue) throws Exception {
        ArrayList<Integer> legalMoves = gameState.getLegalMoves(onlyCloseMoves);
        if (children.size() < legalMoves.size()) {
            expand();
            return null;
        } else if (legalMoves.isEmpty()) {
            randomPolicy();
            return null;
        } else {
            MCTS_UCT_Node child;
            float bestValue = Float.NEGATIVE_INFINITY;
            float moveValue;
            int bestMovePlace = -1;

            ArrayList<Integer> keys = new ArrayList<>(children.keySet());
            Collections.shuffle(keys);
            for (int move : keys) {
                child = children.get(move);
                if (gameState.getCurrentPlayer() == 1) {
                    moveValue = (float) ((child.stats[0] + 0.5 * child.stats[1]) / child.visits
                            + explorationValue * Math.sqrt((Math.log(visits) / child.visits)));
                } else {
                    moveValue = (float) ((child.stats[2] + 0.5 * child.stats[1]) / child.visits
                            + explorationValue * Math.sqrt((Math.log(visits) / child.visits)));
                }
                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMovePlace = move;
                }
            }

            return children.get(bestMovePlace);
        }
    }

    private void expand() throws Exception {
        GameEnvironment gameStateCopy;
        MCTS_UCT_Node newNode;

        ArrayList<Integer> legalMoves = gameState.getLegalMoves(onlyCloseMoves);

        int selectedMove = -1;
        for (int move : legalMoves) {
            if (!children.containsKey(move)) {
                selectedMove = move;
                break;
            }
        }
        gameStateCopy = gameState.copy();
        gameStateCopy.move(selectedMove);

        newNode = new MCTS_UCT_Node(gameStateCopy, this, onlyCloseMoves);
        newNode.randomPolicy();

        children.put(selectedMove, newNode);
    }

    private void randomPolicy() throws Exception {
        HashMap<Integer, Integer> results = gameState.ifTerminal();
        if (results.get(0) == 0) {
            ArrayList<Integer> legalMoves;
            GameEnvironment thisGameState = gameState.copy();
            while (results.get(0) == 0) {
                legalMoves = thisGameState.getLegalMoves(onlyCloseMoves);
                thisGameState.move(legalMoves.get(0));
                results = thisGameState.ifTerminal();
            }
        }
        switch (results.get(1)) {
            case 1 ->
                stats[0] += 1;
            case -1 ->
                stats[2] += 1;
            default ->
                stats[1] += 1;
        }
        visits += 1;
        propagate(results.get(1));
    }

    protected void propagate(int result) {
        MCTS_UCT_Node parentNode = this;
        while (true) {
            parentNode = parentNode.parent;
            if (parentNode == null) {
                break;
            }
            switch (result) {
                case 1 ->
                    parentNode.stats[0] += 1;
                case -1 ->
                    parentNode.stats[2] += 1;
                default ->
                    parentNode.stats[1] += 1;
            }
            parentNode.visits += 1;
        }
    }
}
