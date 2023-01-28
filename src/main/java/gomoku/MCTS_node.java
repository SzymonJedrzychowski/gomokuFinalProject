package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class MCTS_node {
    GameEnvironment state;
    MCTS_node parent;
    HashMap<Integer, MCTS_node> children = new HashMap<>();
    int reward = 0;
    int visits = 0;

    MCTS_node(GameEnvironment state, MCTS_node parent) {
        this.state = state;
        this.parent = parent;
    }

    public MCTS_node select() throws Exception {
        ArrayList<Integer> legalMoves = state.getLegalMoves();
        if (children.size() < legalMoves.size()) {
            expand();
            return null;
        } else if(legalMoves.size() == 0){
            randomPolicy();
            return null;
        } else {
            HashMap<Integer, Float> UCB = new HashMap<>();
            MCTS_node child;
            for (Integer move : children.keySet()) {
                child = children.get(move);
                if (state.getCurrentPlayer() == 1) {
                    UCB.put(move, (float) (child.reward / child.visits));
                } else {
                    UCB.put(move, (float) (-child.reward / child.visits));
                }
            }
            float bestValue = Float.NEGATIVE_INFINITY;
            ArrayList<Integer> moves = new ArrayList<>();

            for (int move : UCB.keySet()) {
                if (UCB.get(move) > bestValue) {
                    bestValue = UCB.get(move);
                    moves.clear();
                    moves.add(move);
                } else if (UCB.get(move) == bestValue) {
                    moves.add(move);
                }
            }
            
            return children.get(moves.get((int) (Math.random()*moves.size())));
        }
    }

    private void expand() throws Exception{
        GameEnvironment stateCopy;
        MCTS_node newNode;

        ArrayList<Integer> legalMoves = state.getLegalMoves();
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        for (int move : legalMoves) {
            if (!children.containsKey(move)) {
                possibleMoves.add(move);
            }
        }

        int move = possibleMoves.get((int) (Math.random() * possibleMoves.size()));
        stateCopy = state.copy();
        stateCopy.move(move);

        newNode = new MCTS_node(stateCopy, this);
        newNode.randomPolicy();

        children.put(move, newNode);
    }

    private void randomPolicy() throws Exception {
        HashMap<Integer, Integer> results = state.ifTerminal();
        if (results.get(0) == 1) {
            reward += results.get(1);
            visits += 1;
            propagate(results.get(1));
        } else {
            ArrayList<Integer> legalMoves;
            int randomNum;
            int moveIndex;
            GameEnvironment thisState = state.copy();
            while (results.get(0) == 0) {
                legalMoves = thisState.getLegalMoves();
                randomNum = ThreadLocalRandom.current().nextInt(0, legalMoves.size());
                moveIndex = 0;
                for (Integer move : legalMoves) {
                    if (moveIndex == randomNum) {
                        thisState.move(move);
                        break;
                    }
                    moveIndex += 1;
                }
                results = thisState.ifTerminal();
            }
            reward += results.get(1);
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
            parentNode.reward += result;
            parentNode.visits += 1;
        }
    }
}
