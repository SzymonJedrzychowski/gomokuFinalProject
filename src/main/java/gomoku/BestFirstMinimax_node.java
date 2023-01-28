package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

public class BestFirstMinimax_node {
    GameEnvironment state;
    BestFirstMinimax_node parent;
    HashMap<Integer, BestFirstMinimax_node> children = new HashMap<>();
    int score;
    int depth;

    BestFirstMinimax_node(GameEnvironment state, BestFirstMinimax_node parent, int depth) {
        this.state = state;
        this.parent = parent;
        this.depth = depth;
    }

    public BestFirstMinimax_node select() throws Exception {
        if (children.size() == 0) {
            return null;
        } else {
            HashMap<Integer, Integer> scores = new HashMap<>();
            BestFirstMinimax_node child;
            for (Integer move : children.keySet()) {
                child = children.get(move);
                if (state.getCurrentPlayer() == 1) {
                    scores.put(move, child.score);
                } else {
                    scores.put(move, -child.score);
                }
            }
            int bestValue = Integer.MIN_VALUE;
            ArrayList<Integer> moves = new ArrayList<>();

            for (int move : scores.keySet()) {
                if (scores.get(move) > bestValue) {
                    bestValue = scores.get(move);
                    moves.clear();
                    moves.add(move);
                } else if (scores.get(move) == bestValue) {
                    moves.add(move);
                }
            }
            return children.get(moves.get((int) (Math.random() * moves.size())));
        }
    }

    public void expand() throws Exception {
        GameEnvironment stateCopy;
        BestFirstMinimax_node newNode;

        ArrayList<Integer> legalMoves = state.getLegalMoves();

        for (int move : legalMoves) {
            stateCopy = state.copy();
            stateCopy.move(move);

            newNode = new BestFirstMinimax_node(stateCopy, this, depth + 1);

            children.put(move, newNode);
        }
    }

    public void evaluate(Evaluator evaluator) {
        int currentPlayer = state.getCurrentPlayer();
        HashMap<Integer, Integer> results = state.ifTerminal();
        if (results.get(0) == 1) {
            if (results.get(1) == 1) {
                score = Integer.MAX_VALUE;
            } else if (results.get(1) == -1) {
                score = Integer.MIN_VALUE;
            } else {
                score = 0;
            }
            this.propagate(score);
        }

        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (BestFirstMinimax_node child : children.values()) {
            results = child.state.ifTerminal();
            if (results.get(0) == 1) {
                if (results.get(1) == 1) {
                    child.score = Integer.MAX_VALUE;
                } else if (results.get(1) == -1) {
                    child.score = Integer.MIN_VALUE;
                } else {
                    child.score = 0;
                }
            } else {
                child.score = evaluator.calculateEvaluation(child.state);
            }

            if ((currentPlayer == 1 && child.score > bestScore) || (currentPlayer == -1 && child.score < bestScore)) {
                bestScore = child.score;
            }
        }
        this.propagate(bestScore);
    }

    private void propagate(int result) {
        BestFirstMinimax_node parentNode = this;
        int currentPlayer;
        while (true) {
            parentNode = parentNode.parent;
            if (parentNode == null) {
                break;
            }
            currentPlayer = parentNode.state.getCurrentPlayer();

            if ((currentPlayer == 1 && parentNode.score < result)
                    || (currentPlayer == -1 && parentNode.score > result)) {
                parentNode.score = result;
            } else {
                parentNode.score = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                for (BestFirstMinimax_node child : parentNode.children.values()) {
                    if ((currentPlayer == 1 && child.score > parentNode.score)
                            || (currentPlayer == -1 && child.score < parentNode.score)) {
                        result = child.score;
                        parentNode.score = result;
                    }
                }
            }
        }
    }
}
