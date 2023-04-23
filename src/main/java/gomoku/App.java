package gomoku;

public class App {

    public static void main(String[] args) {

        // Initialise parameters for the experiment
        int limit = 100;
        int boardSize = 7;
        int gameNumber = 50;
        boolean isLimitTime = true;
        boolean gatherMemory = false;

        GameData[] gameData;
        Player[] players = new Player[10];

        // Initialise players for correct experiment
        if (isLimitTime) {
            if (limit < 100) {
                System.out.println("Minimal limit for time experiments is 100.");
                return;
            }
            players[0] = new MCTS(limit, false, gatherMemory);
            players[1] = new MCTS_UCT(limit, false, gatherMemory);
            players[2] = new RecursiveBestFirstMinimax(limit, false, gatherMemory);
            players[3] = new IterativeDeepening(limit, isLimitTime, false, gatherMemory);
            players[4] = new PrincipalVariationSearch(limit, isLimitTime, false, gatherMemory);

            players[5] = new MCTS(limit, true, gatherMemory);
            players[6] = new MCTS_UCT(limit, true, gatherMemory);
            players[7] = new RecursiveBestFirstMinimax(limit, true, gatherMemory);
            players[8] = new IterativeDeepening(limit, isLimitTime, true, gatherMemory);
            players[9] = new PrincipalVariationSearch(limit, isLimitTime, true, gatherMemory);
        } else {
            if (limit < 1) {
                System.out.println("Minimal limit for time experiments is 1.");
                return;
            }
            players[0] = new Minimax(limit, false, gatherMemory);
            players[1] = new AlphaBetaPruning(limit, false, gatherMemory);
            players[2] = new AlphaBetaPruning_Ordered(limit, false, gatherMemory);
            players[3] = new IterativeDeepening(limit, isLimitTime, false, gatherMemory);
            players[4] = new PrincipalVariationSearch(limit, isLimitTime, false, gatherMemory);

            players[5] = new Minimax(limit, true, gatherMemory);
            players[6] = new AlphaBetaPruning(limit, true, gatherMemory);
            players[7] = new AlphaBetaPruning_Ordered(limit, true, gatherMemory);
            players[8] = new IterativeDeepening(limit, isLimitTime, true, gatherMemory);
            players[9] = new PrincipalVariationSearch(limit, isLimitTime, true, gatherMemory);
        }

        // Select two players for the experiment
        Player p1 = players[0];
        Player p2 = players[2];

        PlayGames games;

        // Conduct the experiment
        games = new PlayGames(gameNumber, boardSize, p1, p2, isLimitTime);
        gameData = games.play(true);

        // Display relevant data
        gameData[0].printData();
        gameData[1].printData();

    }
}
