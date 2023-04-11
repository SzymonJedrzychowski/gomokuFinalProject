package gomoku;

/**
 * Class that is a parent for agent classes.
 */
public abstract class Player {
    /**
     * Mothod used to get move calculated by the agent.
     * 
     * @param game current game environment state
     * @return MoveData object including data of the move and selected move
     * @throws Exception if problems ocurred while calculating the move
     */
    public MoveData move(GameEnvironment game) throws Exception {
        return new MoveData(0, 0, 0);
    }
}
