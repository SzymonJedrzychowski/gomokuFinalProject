package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MCTS_node {

    GameEnvironment state;
    MCTS_node parent;
    HashMap<Integer, MCTS_node> children = new HashMap<>();
    int[] stats = {0, 0, 0};
    int visits = 0;
    boolean onlyCloseMoves;

    MCTS_node(GameEnvironment state, MCTS_node parent, boolean onlyCloseMoves) {
        this.state = state;
        this.parent = parent;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public MCTS_node select() throws Exception {
        ArrayList<Integer> legalMoves = state.getLegalMoves(onlyCloseMoves);
        if (children.size() < legalMoves.size()) {
            expand();
            return null;
        } else if (legalMoves.isEmpty()) {
            randomPolicy();
            return null;
        } else {
            MCTS_node child;
            float bestValue = Float.NEGATIVE_INFINITY;
            float moveValue;
            int bestMovePlace = -1;

            ArrayList<Integer> keys = new ArrayList<>(children.keySet());
            Collections.shuffle(keys);
            for (int move : keys) {
                child = children.get(move);
                if (state.getCurrentPlayer() == 1) {
                    moveValue = (float) ((child.stats[0] + child.stats[1] * 0.5) / child.visits);
                } else {
                    moveValue = (float) ((child.stats[2] + child.stats[1] * 0.5) / child.visits);
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
        GameEnvironment stateCopy;
        MCTS_node newNode;

        ArrayList<Integer> legalMoves = state.getLegalMoves(onlyCloseMoves);
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        for (int move : legalMoves) {
            if (!children.containsKey(move)) {
                possibleMoves.add(move);
            }
        }
        int move = possibleMoves.get(0);
        stateCopy = state.copy();
        stateCopy.move(move);

        newNode = new MCTS_node(stateCopy, this, onlyCloseMoves);
        newNode.randomPolicy();

        children.put(move, newNode);
    }

    private void randomPolicy() throws Exception {
        HashMap<Integer, Integer> results = state.ifTerminal();
        if (results.get(0) == 1) {
            if (null == results.get(1)) {
                stats[1] += 1;
            } else {
                switch (results.get(1)) {
                    case 1 ->
                        stats[0] += 1;
                    case -1 ->
                        stats[2] += 1;
                    default ->
                        stats[1] += 1;
                }
            }
            visits += 1;
            propagate(results.get(1));
        } else {
            ArrayList<Integer> legalMoves;
            GameEnvironment thisState = state.copy();
            while (results.get(0) == 0) {
                legalMoves = thisState.getLegalMoves(onlyCloseMoves);
                thisState.move(legalMoves.get(0));
                results = thisState.ifTerminal();
            }
            if (null == results.get(1)) {
                stats[1] += 1;
            } else {
                switch (results.get(1)) {
                    case 1 ->
                        stats[0] += 1;
                    case -1 ->
                        stats[2] += 1;
                    default ->
                        stats[1] += 1;
                }
            }
            visits += 1;
            propagate(results.get(1));
        }
    }

    protected void propagate(int result) {
        MCTS_node parentNode = this;
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
