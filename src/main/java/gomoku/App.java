package gomoku;

import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

        int limit = 5000;
        int boardSize = 15;
        int gameNumber = 10;
        boolean closeMoves = false;
        
        GameData[] gameData;
        Player player1 = new MCTS(limit, closeMoves);
        Player player2 = new MCTS_UCT(limit, closeMoves);
        Player player3 = new BFM(limit, closeMoves);
        Player player4 = new IterativeDeepening(limit, true, closeMoves);
        Player player5 = new IterativeDeepening_PVS(limit, true, closeMoves);

        Player p1 = player1;
        Player p2 = player1;

        PlayGames games;

        Player t = new IterativeDeepening(1, false, false);
        games = new PlayGames(30, 7, t, t);
        games.play(false);

        games = new PlayGames(gameNumber, boardSize, p1, p2);
        gameData = games.play(true);

        gameData[0].printData();
        gameData[1].printData();

    }
}
