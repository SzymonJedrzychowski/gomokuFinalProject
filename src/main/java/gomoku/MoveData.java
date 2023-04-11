package gomoku;

/**
 * Class responsible for holding data of move.
 */
public class MoveData {
    int selectedMove;
    long time;
    long memoryUsed;

    /**
     * @param selectedMove selected move space
     * @param time time used to calculate the move (in ns)
     * @param memoryUsed maximum memory used to calculate the move
     */
    MoveData(int selectedMove, long time, long memoryUsed) {
        this.selectedMove = selectedMove;
        this.time = time / 1000;
        this.memoryUsed = memoryUsed;
    }

    /**
     * @param selectedMove selected move space
     * @param time time used to calculate the move (in ns)
     */
    MoveData(int selectedMove, long time) {
        this.selectedMove = selectedMove;
        this.time = time / 1000;
        this.memoryUsed = 0;
    }
}
