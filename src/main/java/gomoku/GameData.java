package gomoku;

import java.util.ArrayList;

public class GameData {
    ArrayList<Integer> timesVisited = new ArrayList<>();
    ArrayList<Long> savedTime = new ArrayList<>();
    ArrayList<Long> savedMemory = new ArrayList<>();
    int[] gameResults = new int[3];

    public void addData(int currentMove, long time, long memory){
        if(currentMove >= timesVisited.size()){
            timesVisited.add(1);
            savedTime.add(time);
            savedMemory.add(memory);
        }else{
            timesVisited.set(currentMove, timesVisited.get(currentMove)+1);
            savedTime.set(currentMove, savedTime.get(currentMove)+time);
            savedMemory.set(currentMove, savedMemory.get(currentMove)+memory);
        }
    }

    public void addData(int currentMove){
        if(currentMove >= timesVisited.size()){
            timesVisited.add(0);
            savedTime.add((long)0);
            savedMemory.add((long)0);
        }
    }

    public void finishGame(int winner){
        if (winner == 1) {
            gameResults[0] += 1;
        } else if (winner == -1) {
            gameResults[2] += 1;
        } else {
            gameResults[1] += 1;
        }
    }

    public ArrayList<Long> getAverageTime(){
        ArrayList<Long> results = new ArrayList<>();
        for(int i=0; i<savedTime.size(); i++){
            results.add(savedTime.get(i)/timesVisited.get(i));
        }
        return results;
    }

    public ArrayList<Long> getAverageMemory(){
        ArrayList<Long> results = new ArrayList<>();
        for(int i=0; i<savedMemory.size(); i++){
            results.add(savedMemory.get(i)/timesVisited.get(i));
        }
        return results;
    }
    
    public void printTimes(){
        for(long time:savedTime){
            System.out.printf("%d ", time);
        }
        System.out.println();
    }
    
    public void printMemory(){
        for(long memory:savedMemory){
            System.out.printf("%d ", memory);
        }
        System.out.println();
    }
    
    public void printVisits(){
        for(int visit:timesVisited){
            System.out.printf("%d ", visit);
        }
        System.out.println();
    }

    public void printData(){
        printTimes();
        printMemory();
        printVisits();
    }
    
    public String printResults(){
        return String.format("%d/%d/%d", gameResults[0], gameResults[1], gameResults[2]);
    }
}
