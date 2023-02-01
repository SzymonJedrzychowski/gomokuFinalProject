package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.sql.Timestamp;

public class AlphaBetaPruning_Ordered extends Player {
    int globalDepth;
    Evaluator evaluator = new Evaluator();
    HashMap<Long, Integer> transpositionTable = new HashMap<>();
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
        
        transpositionTable.clear();
        
        game.hashInit();

        Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());

        ArrayList<Integer> sortedMoves = sortMoves(game);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int moveIndex : sortedMoves) {
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

            if(currentPlayer == 1){
                alpha = Math.max(alpha, newScore);
            }else{
                beta = Math.min(beta, newScore);
            }
        
            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }
        System.out.println(bestScore);
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s: %d time: %8d moveCount: %10d%n", "AlphaBetaPruning_Ordered", currentPlayer,
                timestamp2.getTime() - timestamp1.getTime(), count);
        return bestMovePlace.get((int) (Math.random() * bestMovePlace.size()));
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        
        int bestScore = currentPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int newScore;

        long hash;        

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if(results.get(1) == 0){
                return 0;
            }
            return results.get(1) == 1 ? Integer.MAX_VALUE-(globalDepth-depth)*10 : Integer.MIN_VALUE+(globalDepth-depth)*10;
        }

        if (depth == 0) {
            return evaluator.calculateEvaluation(game);
        }

        ArrayList<Integer> movesArray;
        if(globalDepth-depth <= 2){
            movesArray = sortMoves(game);
        }else{
            movesArray = game.getLegalMoves();
        }

        for (int moveIndex : movesArray) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            hash = game.update(currentPlayer, moveIndex);
            
            if (transpositionTable.containsKey(hash)) {
                count += 1;
                newScore = transpositionTable.get(hash);
            } else {
                newScore = deepMove(game, depth - 1, alpha, beta);
                transpositionTable.put(hash, newScore);
            }

            if ((newScore > bestScore && currentPlayer == 1)
                    || (newScore < bestScore && currentPlayer == -1)) {
                bestScore = newScore;
            }
            
            game.undoMove(moveIndex);
            hash = game.update(currentPlayer, moveIndex);

            if (currentPlayer == 1) {
                if (newScore > beta) {
                    break;
                }
                alpha = Math.max(alpha, newScore);
            } else {
                if (newScore < alpha) {
                    break;
                }
                beta = Math.min(beta, newScore);
            }
        }
        
        return bestScore;
    }

    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception{
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves();
        TreeMap<Integer, ArrayList<Integer>> sortedMap;
        if(game.getCurrentPlayer() == 1){
            sortedMap = new TreeMap<>(Collections.reverseOrder()); 
        }else{
            sortedMap = new TreeMap<>(); 
        }

        ArrayList<Integer> tempArray;
        int tempScore;
        for(int moveIndex: legalMoves){
            game.move(moveIndex);
            
            tempScore = evaluator.calculateEvaluation(game);
            tempArray = sortedMap.getOrDefault(tempScore, new ArrayList<>());
            tempArray.add(moveIndex);
            sortedMap.put(tempScore, tempArray);

            game.undoMove(moveIndex);
        }

        for(int score: sortedMap.keySet()){
            sortedMoves.addAll(sortedMap.get(score));
        }
        return sortedMoves;
    } 
}
