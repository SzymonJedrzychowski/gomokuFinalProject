package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class BFM extends Player {
    int timeLimit;
    Evaluator evaluator = new Evaluator();
    Timestamp timestamp1;
    int moveCount;

    BFM(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int move(GameEnvironment state) throws Exception {
        moveCount = 0;
        GameEnvironment game = state.copy();
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> legalMoves = game.getLegalMoves();
        TreeMap<Integer, ArrayList<Integer>> bestMoves;
        if (currentPlayer == 1) {
            bestMoves = new TreeMap<>(Collections.reverseOrder());
        } else {
            bestMoves = new TreeMap<>();
        }

        int evaluationScore;
        ArrayList<Integer> tempArray;
        HashMap<Integer, Integer> results;

        for (int move : legalMoves) {
            game.move(move);

            results = game.ifTerminal();
            if (results.get(0) == 1) {
                evaluationScore = 0;
                if (results.get(1) == 1) {
                    evaluationScore = Integer.MAX_VALUE;
                } else if (results.get(1) == -1) {
                    evaluationScore = Integer.MIN_VALUE;
                }
            } else {
                evaluationScore = evaluator.calculateEvaluation(game);
            }

            tempArray = bestMoves.getOrDefault(evaluationScore, new ArrayList<>());
            tempArray.add(move);
            bestMoves.put(evaluationScore, tempArray);

            game.undoMove(move);
            moveCount += 1;
        }
        timestamp1 = new Timestamp(System.currentTimeMillis());

        int randomIndex;
        int newScore;
        int currentMove;
        int secondBest;
        while (new Timestamp(System.currentTimeMillis()).getTime() - timestamp1.getTime() < timeLimit && !((currentPlayer == 1 && bestMoves.firstKey() == Integer.MAX_VALUE) || (currentPlayer == -1 && bestMoves.firstKey() == Integer.MIN_VALUE))) {
            if (bestMoves.firstEntry().getValue().size() > 1) {
                secondBest = bestMoves.firstKey();
            } else if (bestMoves.size() == 1) {
                secondBest = currentPlayer == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            } else {
                secondBest = currentPlayer == 1 ? bestMoves.ceilingKey(bestMoves.firstKey() - 1)
                        : bestMoves.floorKey(bestMoves.firstKey() + 1);
            }
            
            randomIndex = (int) (bestMoves.firstEntry().getValue().size() * Math.random());
            currentMove = bestMoves.firstEntry().getValue().get(randomIndex);
            game.move(currentMove);
            
            if(currentPlayer == 1){
                newScore = deepMove(game, Math.max(secondBest, Integer.MIN_VALUE), Integer.MAX_VALUE);
            }else{
                newScore = deepMove(game, Integer.MAX_VALUE, Math.min(secondBest, Integer.MAX_VALUE));
            }
            game.undoMove(currentMove);
            if (newScore != bestMoves.firstKey()) {
                if (bestMoves.firstEntry().getValue().size() > 1) {
                    bestMoves.firstEntry().getValue().remove(randomIndex);
                } else {
                    bestMoves.remove(bestMoves.firstKey());
                }
                tempArray = bestMoves.getOrDefault(newScore, new ArrayList<>());
                tempArray.add(currentMove);
                bestMoves.put(newScore, tempArray);
            }
        }
        
        System.out.printf("%-30s time: %10d moveCount: %10d %n", "RBFM",
                new Timestamp(System.currentTimeMillis()).getTime() - timestamp1.getTime(), moveCount);
        System.out.println(bestMoves);
        return bestMoves.firstEntry().getValue().get((int)(bestMoves.firstEntry().getValue().size()*Math.random()));
    }

    private int deepMove(GameEnvironment game, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> legalMoves = game.getLegalMoves();
        TreeMap<Integer, ArrayList<Integer>> bestMoves;
        if (currentPlayer == 1) {
            bestMoves = new TreeMap<>(Collections.reverseOrder());
        } else {
            bestMoves = new TreeMap<>();
        }

        int evaluationScore;
        ArrayList<Integer> tempArray;
        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            evaluationScore = 0;
            if (results.get(1) == 1) {
                evaluationScore = Integer.MAX_VALUE;
            } else if (results.get(1) == -1) {
                evaluationScore = Integer.MIN_VALUE;
            }
            return evaluationScore;
        }
        for (int move : legalMoves) {
            game.move(move);

            results = game.ifTerminal();
            if (results.get(0) == 1) {
                evaluationScore = 0;
                if (results.get(1) == 1) {
                    evaluationScore = Integer.MAX_VALUE;
                } else if (results.get(1) == -1) {
                    evaluationScore = Integer.MIN_VALUE;
                }
            } else {
                evaluationScore = evaluator.calculateEvaluation(game);
            }

            tempArray = bestMoves.getOrDefault(evaluationScore, new ArrayList<>());
            tempArray.add(move);
            bestMoves.put(evaluationScore, tempArray);

            game.undoMove(move);
            moveCount += 1;

            if ((currentPlayer == 1 && evaluationScore > beta) || (currentPlayer == -1 && evaluationScore < alpha)) {
                return evaluationScore;
            }
        }


        int secondBest;
        int randomIndex;
        int currentMove;
        int newScore;

        if (bestMoves.firstEntry().getValue().size() > 1) {
            secondBest = bestMoves.firstKey();
        } else if (bestMoves.size() == 1) {
            secondBest = currentPlayer == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        } else {
            secondBest = currentPlayer == 1 ? bestMoves.ceilingKey(bestMoves.firstKey() - 1)
                    : bestMoves.floorKey(bestMoves.firstKey() + 1);
        }
        while (new Timestamp(System.currentTimeMillis()).getTime() - timestamp1.getTime() < timeLimit
                && (bestMoves.firstKey() >= alpha && bestMoves.firstKey() <= beta)) {
            randomIndex = (int) (bestMoves.firstEntry().getValue().size() * Math.random());
            currentMove = bestMoves.firstEntry().getValue().get(randomIndex);
            game.move(currentMove);

            if(currentPlayer == 1){
                newScore = deepMove(game, Math.max(alpha, secondBest), beta);
            }else{
                newScore = deepMove(game, alpha, Math.min(beta, secondBest));
            }
            game.undoMove(currentMove);
            if (newScore != bestMoves.firstKey()) {
                if (bestMoves.firstEntry().getValue().size() > 1) {
                    bestMoves.firstEntry().getValue().remove(randomIndex);
                } else {
                    bestMoves.remove(bestMoves.firstKey());
                }
                tempArray = bestMoves.getOrDefault(newScore, new ArrayList<>());
                tempArray.add(currentMove);
                bestMoves.put(newScore, tempArray);
            }
            if (bestMoves.firstEntry().getValue().size() > 1) {
                secondBest = bestMoves.firstKey();
            } else if (bestMoves.size() == 1) {
                secondBest = currentPlayer == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            } else {
                secondBest = currentPlayer == 1 ? bestMoves.ceilingKey(bestMoves.firstKey() - 1)
                        : bestMoves.floorKey(bestMoves.firstKey() + 1);
            }

        }
        
        return bestMoves.firstKey();
    }
}
