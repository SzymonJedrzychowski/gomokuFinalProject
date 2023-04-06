package gomoku;

import java.util.ArrayList;
import java.util.HashMap;
import org.openjdk.jol.info.GraphLayout;

public class IterativeDeepening extends Player {
    boolean onlyCloseMoves;
    int simulationLimit;
    long startTimestamp;
    boolean isLimitTime;

    IterativeDeepening(int simulationLimit, boolean isLimitTime, boolean onlyCloseMoves) {
        this.simulationLimit = simulationLimit;
        this.isLimitTime = isLimitTime;
        this.onlyCloseMoves = onlyCloseMoves;
    }

    @Override
    public MoveData move(GameEnvironment gameState) throws Exception {
        startTimestamp = System.nanoTime();
        GameEnvironment game = gameState.copy();

        HashMap<String, Integer> results = new HashMap<>();

        IterativeDeepeningThread thread;
        if (isLimitTime) {
            thread = new IterativeDeepeningThread(Integer.MAX_VALUE - 10, game, onlyCloseMoves);
        } else {
            thread = new IterativeDeepeningThread(simulationLimit, game, onlyCloseMoves);
        }

        thread.start();
        if (isLimitTime) {
            while (System.nanoTime() - (long) simulationLimit * 1000000 < startTimestamp) {
                results = thread.getResults();

                if (thread.isFinished()) {
                    break;
                } else {
                    Thread.sleep(5);
                }
            }
        } else {
            while (true) {
                results = thread.getResults();

                if (thread.isFinished()) {
                    break;
                } else {
                    Thread.sleep(5);
                }
            }
        }

        HashMap<Long, Integer> previousScores = thread.getPreviousScores();
        HashMap<Long, ArrayList<Integer>> largestTT = thread.getLargestTT();
        thread.finishThread();

        long endTimestamp = System.nanoTime();
        MoveData moveData = new MoveData(endTimestamp - startTimestamp, results.get("moveCount"),
                results.get("bestMove"),
                GraphLayout.parseInstance(this).totalSize() + GraphLayout.parseInstance(previousScores).totalSize()
                        + GraphLayout.parseInstance(largestTT).totalSize(),
                results.get("bestScore"));
        return moveData;
    }

}
