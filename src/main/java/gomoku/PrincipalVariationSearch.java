package gomoku;

import java.util.ArrayList;
import java.util.HashMap;

import org.openjdk.jol.info.GraphLayout;

/**
 * Class responsible for Principal Variation Search agent.
 */
public class PrincipalVariationSearch extends Player {
    boolean onlyCloseMoves;
    int simulationLimit;
    long startTimestamp;
    boolean isLimitTime;
    boolean gatherMemory;

    /**
     * @param simulationLimit limit of the search
     * @param isLimitTime     if the search is time-limited
     * @param onlyCloseMoves  if only close moves should be used
     * @param gatherMemory    if memory should be gathered
     */
    PrincipalVariationSearch(int simulationLimit, boolean isLimitTime, boolean onlyCloseMoves, boolean gatherMemory) {
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

        // Create new thread with proper simulation limits
        PrincipalVariationSearch_Thread thread;
        if (isLimitTime) {
            thread = new PrincipalVariationSearch_Thread(game, onlyCloseMoves);
            thread.start(); // Run the thread - time-limited
            while (System.nanoTime() - (long) simulationLimit * 1000000 < startTimestamp) {
                results = thread.getResults();

                if (thread.isFinished()) {
                    break;
                }
                Thread.sleep(5); // Sleep to prevent constant run of previous function
            }
        } else {
            thread = new PrincipalVariationSearch_Thread(simulationLimit, game, onlyCloseMoves);
            thread.startNormally(); // Run the thread - depth-limited
            results = thread.getResults();
        }

        HashMap<Long, Integer> previousScores = thread.getPreviousScores();
        HashMap<Long, ArrayList<Integer>> largestTT = thread.getLargestTT();
        thread.finishThread();

        if (results == null || !results.containsKey("bestMove")) {
            throw new Exception("Provided time was not enough to calculate the move.");
        }

        long endTimestamp = System.nanoTime();

        // Gather the data of the move
        MoveData moveData;
        if (gatherMemory) {
            moveData = new MoveData(results.get("bestMove"), endTimestamp - startTimestamp,
                    GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(previousScores).totalSize()
                            + GraphLayout.parseInstance(largestTT).totalSize());
        } else {
            moveData = new MoveData(results.get("bestMove"), endTimestamp - startTimestamp);
        }
        return moveData;
    }
}
