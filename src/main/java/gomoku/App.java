package gomoku;

import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

        int limit = 1000;
        int boardSize = 7;
        int gameNumber = 1;
        boolean closeMoves = true;
        
        GameData[] gameData;
        Player player1 = new MCTS(limit, closeMoves);
        Player player2 = new MCTS_UCT(limit, closeMoves);
        Player player3 = new BFM(limit, closeMoves);
        Player player4 = new IterativeDeepening(limit, true, closeMoves);
        Player player5 = new IterativeDeepening_PVS(limit, true, closeMoves);

        Player player1c = new MCTS(limit, !closeMoves);
        Player player2c = new MCTS_UCT(limit, !closeMoves);
        Player player3c = new BFM(limit, !closeMoves);
        Player player4c = new IterativeDeepening(limit, true, !closeMoves);
        Player player5c = new IterativeDeepening_PVS(limit, true, !closeMoves);

        Player p1 = player1;
        Player p2 = player4;

        PlayGames games;

        Player t = new IterativeDeepening(1, false, false);
        games = new PlayGames(1, 7, t, t);
        games.play(false);

        games = new PlayGames(gameNumber, boardSize, p1, p2);
        gameData = games.play(true);

        gameData[0].printData();
        gameData[1].printData();

    }
}
