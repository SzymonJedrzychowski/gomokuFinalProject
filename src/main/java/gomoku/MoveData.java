package gomoku;

public class MoveData {
    long time;
    int moveCount;
    int selectedMove;
    long memoryUsed;
    int bestScore;

    MoveData(long time, int moveCount, int selectedMove, long memoryUsed, int bestScore) {
        this.time = time / 1000;
        this.moveCount = moveCount;
        this.selectedMove = selectedMove;
        this.memoryUsed = memoryUsed;
        this.bestScore = bestScore;
    }
}
