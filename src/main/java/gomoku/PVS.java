package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.Timestamp;

public class PVS extends Player {
    int globalDepth;
    HashMap<Long, ArrayList<Integer>> transpositionTable = new HashMap<>();
    int count;
    boolean onlyCloseMoves;

    PVS(int globalDepth) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = false;
    }

    PVS(int globalDepth, boolean onlyCloseMoves) {
        this.globalDepth = globalDepth;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    public int move(GameEnvironment gameState) throws Exception {
        count = 0;

        GameEnvironment game = gameState.copy();
        int currentPlayer = game.getCurrentPlayer();

        int bestScore = Integer.MIN_VALUE;
        int bestMovePlace = -1;
        int newScore;

        game.hashInit();

        Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

        ArrayList<Integer> possibleMoves;
        if(globalDepth > 1){
            possibleMoves = sortMoves(game);
        }else{
            possibleMoves = getMoves(game);
        }
        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE;
        int b = beta;
        for (int moveIndex : possibleMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = -deepMove(game, globalDepth - 1, -b, -alpha);
            if (newScore > alpha && newScore < beta && possibleMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, globalDepth - 1, -beta, -newScore);
            }

            if (newScore > bestScore) {
                bestScore = newScore;
                bestMovePlace = moveIndex;
            }

            if (newScore > alpha) {
                alpha = newScore;
            }
            b = alpha+1;
            count += 1;
            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
        }

        Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
        System.out.printf("%-30s: player %2d time: %8d moveCount: %10d%n", "PVS",
        currentPlayer,
        endTimestamp.getTime() - startTimestamp.getTime(), count);
        transpositionTable.clear();
        return bestMovePlace;
    }

    public int deepMove(GameEnvironment game, int depth, int alpha, int beta) throws Exception {
        int currentPlayer = game.getCurrentPlayer();
        int newScore;
        long hash;
        int bestScore = alpha;
        int startAlpha = alpha;

        hash = game.getHash();
        ArrayList<Integer> tempArray;
        int flag;
        if (transpositionTable.containsKey(hash)) {
            tempArray = transpositionTable.get(hash);
            flag = tempArray.get(1);
            newScore = tempArray.get(0);
            if (flag == 0) {
                return newScore;
            }else if(flag == 1){
                alpha = Math.max(alpha, newScore);
            }else if(flag == 2){
                beta = Math.min(beta, newScore);
            }
            if(alpha>=beta){
                return newScore;
            }
        }

        HashMap<Integer, Integer> results = game.ifTerminal();

        if (results.get(0) == 1) {
            if (results.get(1) == 0) {
                return 0;
            }
            return Integer.MIN_VALUE + 1;
        }

        if (depth == 0) {
            return currentPlayer * game.evaluateBoard();
        }

        ArrayList<Integer> possibleMoves = game.getLegalMoves(onlyCloseMoves);
        int b = beta;
        for (int moveIndex : possibleMoves) {
            try {
                game.move(moveIndex);
            } catch (Exception e) {
                throw new Exception("Minimax: " + e);
            }

            game.update(currentPlayer, moveIndex);

            newScore = -deepMove(game, depth - 1, -b, -alpha);
            if (newScore > alpha && newScore < beta && possibleMoves.get(0) != moveIndex) {
                newScore = -deepMove(game, depth - 1, -beta, -newScore);
            }

            if (newScore > bestScore) {
                bestScore = newScore;
            }

            game.update(currentPlayer, moveIndex);
            game.undoMove(moveIndex);
            if (newScore > alpha) {
                alpha = newScore;
            }
            count += 1;
            if(alpha >= beta){
                break;
            }
            b = alpha+1;
        }
        if(bestScore<=startAlpha){
            flag = 2;
        }else if(bestScore >= b){
            flag = 1;
        }else{
            flag = 0;
        }
        transpositionTable.put(game.getHash(), new ArrayList<>(Arrays.asList(bestScore, flag)));

        return bestScore;
    }

    ArrayList<Integer> getMoves(GameEnvironment game){
        ArrayList<Integer> possibleMoves = game.getLegalMoves(onlyCloseMoves);
        return possibleMoves;
    }

    public ArrayList<Integer> sortMoves(GameEnvironment game) throws Exception{
        ArrayList<Integer> sortedMoves = new ArrayList<>();
        ArrayList<Integer> legalMoves = game.getLegalMoves(onlyCloseMoves);
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