package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import org.openjdk.jol.info.GraphLayout;

public class BFM extends Player {

    int timeLimit;
    long startTimestamp;
    int moveCount;
    boolean onlyCloseMoves;
    HashMap<Long, Integer> transpositionTable;

    BFM(int timeLimit, boolean onlyCloseMoves) {
        this.timeLimit = timeLimit;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    @Override
    public MoveData move(GameEnvironment state) throws Exception {
        startTimestamp = System.nanoTime();
        moveCount = 0;
        transpositionTable = new HashMap<>();
        GameEnvironment game = state.copy();
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
        TreeMap<Integer, ArrayList<Integer>> bestMoves;
        if (currentPlayer == 1) {
            bestMoves = new TreeMap<>(Collections.reverseOrder());
        } else {
            bestMoves = new TreeMap<>();
        }

        int evaluationScore;
        ArrayList<Integer> tempArray;
        HashMap<Integer, Integer> results;

        game.hashInit();
        long hash;
        for (int move : legalMoves) {
            game.move(move);
            hash = game.update(currentPlayer, move);

            if (transpositionTable.containsKey(hash)) {
                evaluationScore = transpositionTable.get(hash);
            } else {
                results = game.evaluateBoard();
                if (results.get(0) == 1) {
                    evaluationScore = 0;
                    if (results.get(1) == 1) {
                        evaluationScore = Integer.MAX_VALUE;
                    } else if (results.get(1) == -1) {
                        evaluationScore = Integer.MIN_VALUE;
                    }
                } else {
                    evaluationScore = results.get(2);
                }
                transpositionTable.put(hash, evaluationScore);
            }

            tempArray = bestMoves.getOrDefault(evaluationScore, new ArrayList<>());
            tempArray.add(move);
            bestMoves.put(evaluationScore, tempArray);

            game.update(currentPlayer, move);
            game.undoMove(move);
            moveCount += 1;
        }

        int randomIndex;
        int newScore;
        int currentMove;
        int secondBest;
        while (System.nanoTime() - startTimestamp < (long) timeLimit * 1000000
                && !((currentPlayer == 1 && bestMoves.firstKey() == Integer.MAX_VALUE)
                        || (currentPlayer == -1 && bestMoves.firstKey() == Integer.MIN_VALUE))) {
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
            game.update(currentPlayer, currentMove);

            if (currentPlayer == 1) {
                newScore = deepMove(game, secondBest, Integer.MAX_VALUE, 1);
            } else {
                newScore = deepMove(game, Integer.MIN_VALUE, secondBest, 1);
            }
            game.update(currentPlayer, currentMove);
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
        MoveData moveData = new MoveData(System.nanoTime() - startTimestamp, moveCount,
                bestMoves.firstEntry().getValue().get((int) (bestMoves.firstEntry().getValue().size() * Math.random())),
                GraphLayout.parseInstance(this).totalSize(),
                bestMoves.firstKey());
        transpositionTable = null;
        return moveData;
    }

    private int deepMove(GameEnvironment game, int alpha, int beta, int depth) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
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
                evaluationScore = Integer.MAX_VALUE - depth * 10;
            } else if (results.get(1) == -1) {
                evaluationScore = Integer.MIN_VALUE + depth * 10;
            }
            return evaluationScore;
        }

        long hash;

        for (int move : legalMoves) {
            game.move(move);
            hash = game.update(currentPlayer, move);

            if (transpositionTable.containsKey(hash)) {
                evaluationScore = transpositionTable.get(hash);
            } else {
                results = game.evaluateBoard();
                if (results.get(0) == 1) {
                    evaluationScore = 0;
                    if (results.get(1) == 1) {
                        evaluationScore = Integer.MAX_VALUE - depth * 10 - 10;
                    } else if (results.get(1) == -1) {
                        evaluationScore = Integer.MIN_VALUE + depth * 10 + 10;
                    }
                } else {
                    evaluationScore = results.get(2);
                }
                transpositionTable.put(hash, evaluationScore);
            }

            tempArray = bestMoves.getOrDefault(evaluationScore, new ArrayList<>());
            tempArray.add(move);
            bestMoves.put(evaluationScore, tempArray);

            game.update(currentPlayer, move);
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
        while (System.nanoTime() - startTimestamp < (long) timeLimit * 1000000
                && (bestMoves.firstKey() >= alpha && bestMoves.firstKey() <= beta)) {
            randomIndex = (int) (bestMoves.firstEntry().getValue().size() * Math.random());
            currentMove = bestMoves.firstEntry().getValue().get(randomIndex);
            game.move(currentMove);
            game.update(currentPlayer, currentMove);

            if (currentPlayer == 1) {
                newScore = deepMove(game, Math.max(alpha, secondBest), beta, depth + 1);
            } else {
                newScore = deepMove(game, alpha, Math.min(beta, secondBest), depth + 1);
            }
            game.update(currentPlayer, currentMove);
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
