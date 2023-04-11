package gomoku;

import java.util.ArrayList;

/**
 * Class responsible for storing and processing the data of each move.
 */
public class GameData {
    ArrayList<Integer> timesVisited = new ArrayList<>();
    ArrayList<Long> savedTime = new ArrayList<>();
    ArrayList<Long> savedMemory = new ArrayList<>();
    int[] gameResults = new int[3];

    /**
     * Method used to add new data.
     * 
     * @param currentMove number of move in the game
     * @param time time how long move took to calculate
     * @param memory maximum memory used during move calculation
     */
    public void addData(int currentMove, long time, long memory) {
        if (currentMove >= timesVisited.size()) {
            timesVisited.add(1);
            savedTime.add(time);
            savedMemory.add(memory);
        } else {
            timesVisited.set(currentMove, timesVisited.get(currentMove) + 1);
            savedTime.set(currentMove, savedTime.get(currentMove) + time);
            savedMemory.set(currentMove, savedMemory.get(currentMove) + memory);
        }
    }

    /**
     * Method used to add empty move data.
     * 
     * @param currentMove number of move in the game
     */
    public void addData(int currentMove) {
        if (currentMove >= timesVisited.size()) {
            timesVisited.add(0);
            savedTime.add((long) 0);
            savedMemory.add((long) 0);
        }
    }

    /**
     * Method used to add results of the game.
     * 
     * @param winner result of the game
     */
    public void finishGame(int winner) {
        if (winner == 1) {
            gameResults[0] += 1;
        } else if (winner == -1) {
            gameResults[2] += 1;
        } else {
            gameResults[1] += 1;
        }
    }

    /**
     * Method used to calculate average time per move.
     * 
     * @return average time of move
     */
    public ArrayList<Long> getAverageTime() {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < savedTime.size(); i++) {
            results.add(savedTime.get(i) / timesVisited.get(i));
        }
        return results;
    }

    /**
     * Method used to calculate average maximum memory per move.
     * 
     * @return average maximum memory of move
     */
    public ArrayList<Long> getAverageMemory() {
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < savedMemory.size(); i++) {
            results.add(savedMemory.get(i) / timesVisited.get(i));
        }
        return results;
    }

    /**
     * Method used to display the time data.
     */
    public void printTimes() {
        for (long time : savedTime) {
            System.out.printf("%d ", time);
        }
        System.out.println();
    }

    /**
     * Method used to display the memory data.
     */
    public void printMemory() {
        for (long memory : savedMemory) {
            System.out.printf("%d ", memory);
        }
        System.out.println();
    }

    /**
     * Method used to display the visits data.
     */
    public void printVisits() {
        for (int visit : timesVisited) {
            System.out.printf("%d ", visit);
        }
        System.out.println();
    }

    /**
     * Method used to display the game results data.
     */
    public void printResults() {
        System.out.printf("%d/%d/%d%n", gameResults[0], gameResults[1], gameResults[2]);
    }

    /**
     * Method used to display all data at once.
     */
    public void printData() {
        //printTimes();
        //printMemory();
        //printVisits();
        printResults();
    }
}
