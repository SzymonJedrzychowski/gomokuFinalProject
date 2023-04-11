package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

import org.openjdk.jol.info.GraphLayout;

public class PrincipalVariationSearch extends Player {
    boolean onlyCloseMoves;
    int simulationLimit;
    long startTimestamp;
    boolean isLimitTime;
    boolean gatherMemory;

    PrincipalVariationSearch(int simulationLimit, boolean isLimitTime, boolean onlyCloseMoves, boolean gatherMemory){
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
        this.onlyCloseMoves = onlyCloseMoves;
        this.gatherMemory = gatherMemory;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        startTimestamp = System.nanoTime();
        GameEnvironment game = gameState.copy();

        HashMap<String, Integer> results = new HashMap<>();

        PrincipalVariationSearch_Thread thread;
        if (isLimitTime) {
            thread = new PrincipalVariationSearch_Thread(game, onlyCloseMoves);
            thread.start();
            while (System.nanoTime() - (long) simulationLimit * 1000000 < startTimestamp) {
                results = thread.getResults();

                if (thread.isFinished()) {
                    break;
                }
                Thread.sleep(5);
            }
        } else {
            thread = new PrincipalVariationSearch_Thread(simulationLimit, game, onlyCloseMoves);
            thread.startNormally();
            results = thread.getResults();
        }

        HashMap<Long, Integer> previousScores = thread.getPreviousScores();
        HashMap<Long, ArrayList<Integer>> largestTT = thread.getLargestTT();
        thread.finishThread();

        if(results == null || !results.containsKey("bestMove")){
            throw new Exception("Provided time was not enough to calculate the move.");
        }

        long endTimestamp = System.nanoTime();

        MoveData moveData;
        if(gatherMemory){
            moveData = new MoveData(results.get("bestMove"), endTimestamp - startTimestamp,
            GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(previousScores).totalSize()
                    + GraphLayout.parseInstance(largestTT).totalSize());
        }else{
            moveData = new MoveData(results.get("bestMove"), endTimestamp - startTimestamp);
        }
        return moveData;
    }
}
