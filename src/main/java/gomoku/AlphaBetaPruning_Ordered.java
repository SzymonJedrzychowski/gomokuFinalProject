package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.sql.Timestamp;

public class AlphaBetaPruning_Ordered extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable = new HashMap<>();
    int count;

    AlphaBetaPruning_Ordered(int globalDepth) {
        this.globalDepth = globalDepth;
    }

    public int move(GameEnvironment gameState) throws Exception {
        count = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        ArrayList<Integer> bestMovePlace = new ArrayList<>();
        int newScore;

        game.hashInit();

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
        
        ArrayList<Integer> possibleMoves;
        if(globalDepth>1){
            possibleMoves = sortMoves(game);
        }else{
            possibleMoves = game.getLegalMoves();
        }
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int moveIndex : possibleMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = deepMove(game, globalDepth - 1, alpha, beta);
            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
                bestMovePlace.clear();
                bestMovePlace.add(moveIndex);
            } else if ((newScore == bestScore && currentPlayer == 1)
                    || (newScore == bestScore && currentPlayer == -1)) {
                bestMovePlace.add(moveIndex);
            }

            if (currentPlayer == 1) {
                alpha = Math.max(alpha, newScore);
            } else {
                beta = Math.min(beta, newScore);
            }

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        //System.out.printf("%-30s: %d time: %8d moveCount: %10d%n", "AlphaBetaPruning", currentPlayer,
        //        timestamp2.getTime() - timestamp1.getTime(), count);
        transpositionTable.clear();
        return bestMovePlace.get((int) (Math.random() * bestMovePlace.size()));
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int newScore = -1;

        long hash;

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                return 0;
            }
            return results.get(1) == 1 ? Integer.MAX_VALUE - (globalDepth - depth) * 10
                    : Integer.MIN_VALUE + (globalDepth - depth) * 10;
        }

        if (depth == 0) {
            return game.evaluateBoard();
        }
        ArrayList<Integer> tempArray;
        int flag;
        boolean skipSimulation;
        boolean cutoff = false;
        ArrayList<Integer> movesArray;
        if(globalDepth-depth <= Math.min(2, globalDepth-2)){
            movesArray = sortMoves(game);
        }else{
            movesArray = game.getLegalMoves();
        }

        for (int moveIndex : movesArray) {
            skipSimulation = false;
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            hash = game.update(currentPlayer, moveIndex);

            if (transpositionTable.containsKey(hash)) {
                tempArray = transpositionTable.get(hash);
                flag = tempArray.get(1);
                newScore = tempArray.get(0);
                if (flag == 0) {
                    skipSimulation = true;
                }else if(flag == 1 && newScore >= beta){
                    skipSimulation = true;
                }else if(flag == 2 && newScore <= alpha){
                    skipSimulation = true;
                }else if(beta <= alpha){
                    skipSimulation = true;
                }
            }
            if (!skipSimulation) {
                count += 1;
                newScore = deepMove(game, depth - 1, alpha, beta);
            }

            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }

            game.undoMove(moveIndex);

            flag = 0;
            if (currentPlayer == 1) {
                alpha = Math.max(alpha, newScore);
                if (newScore > beta) {
                    flag = 2;
                    cutoff = true;
                }
            } else {
                beta = Math.min(beta, newScore);
                if (newScore < alpha) {
                    flag = 1;
                    cutoff = true;
                }
            }
            transpositionTable.put(hash, new ArrayList<>(Arrays.asList(newScore, flag)));
            hash = game.update(currentPlayer, moveIndex);

            if(cutoff){
                break;
            }
        }

        return bestScore;
    }

    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception{
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves();
        int bestScore = game.getCurrentPlayer() == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        ArrayList<Integer> bestIndex = new ArrayList<>();

        int tempScore;
        for(int moveIndex: legalMoves){
            game.move(moveIndex);
            tempScore = game.evaluateBoard();
            game.undoMove(moveIndex);
            if(game.getCurrentPlayer() == 1){
                if(tempScore > bestScore){
                    bestScore = tempScore;
                    bestIndex.clear();
                    bestIndex.add(moveIndex);
                }else if(tempScore == bestScore){
                    bestIndex.add(moveIndex);
                }
            }else{
                if(tempScore < bestScore){
                    bestScore = tempScore;
                    bestIndex.clear();
                    bestIndex.add(moveIndex);
                }else if(tempScore == bestScore){
                    bestIndex.add(moveIndex);
                }
            }
        }
        int randomBest = bestIndex.get((int) (Math.random() * bestIndex.size()));
        sortedMoves.add(randomBest);
        for(int moveIndex:legalMoves){
            if(moveIndex != randomBest){
                sortedMoves.add(moveIndex);
            }
        }
        return sortedMoves;
    } 
}
