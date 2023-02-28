package gomoku;

import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

        int limit = 1;
        int boardSize = 7;
        int gameNumber = 50;
        boolean closeMoves = true;
        GameData[] gameData;
        Player player1 = new Minimax(limit, closeMoves);
        Player player2 = new AlphaBetaPruning(limit, closeMoves);
        Player player3 = new AlphaBetaPruning_Ordered(limit, closeMoves);
        Player player4 = new IterativeDeepening(limit, false, closeMoves);
        Player player5 = new IterativeDeepening_PVS(limit, false, closeMoves);

        Player p1 = player1;
        Player p2 = player1;

        PlayGames games;

        Player t = new IterativeDeepening(1, false);
        games = new PlayGames(30, 7, t, t);
        games.play();

        games = new PlayGames(gameNumber, boardSize, p1, p2);
        gameData = games.play();

        gameData[0].printData();
        gameData[1].printData();

        /**
         * int limit = 1;
         * int boardSize = 7;
         * int gameNumber = 200;
         * boolean closeMoves = false;
         * ArrayList<GameData[]> gameData = new ArrayList<>();
         * Player player1 = new Minimax(limit, closeMoves);
         * Player player2 = new AlphaBetaPruning(limit, closeMoves);
         * Player player3 = new AlphaBetaPruning_Ordered(limit, closeMoves);
         * Player player4 = new IterativeDeepening(limit, false, closeMoves);
         * Player player5 = new IterativeDeepening_PVS(limit, false, closeMoves);
         * Player player1c = new Minimax(limit, !closeMoves);
         * Player player2c = new AlphaBetaPruning(limit, !closeMoves);
         * Player player3c = new AlphaBetaPruning_Ordered(limit, !closeMoves);
         * Player player4c = new IterativeDeepening(limit, false, !closeMoves);
         * Player player5c = new IterativeDeepening_PVS(limit, false, !closeMoves);
         * 
         * Player p1 = player1;
         * 
         * PlayGames games;
         * 
         * games = new PlayGames(gameNumber, boardSize, p1, player1c);
         * gameData.add(games.play());
         * games = new PlayGames(gameNumber, boardSize, p1, player2c);
         * gameData.add(games.play());
         * games = new PlayGames(gameNumber, boardSize, p1, player3c);
         * gameData.add(games.play());
         * games = new PlayGames(gameNumber, boardSize, p1, player4);
         * gameData.add(games.play());
         * games = new PlayGames(gameNumber, boardSize, p1, player5c);
         * gameData.add(games.play());
         * 
         * System.out.printf("%s/%s/%s/%s/%s%n", gameData.get(0)[0].printResults(),
         * gameData.get(1)[0].printResults(),
         * gameData.get(2)[0].printResults(), gameData.get(3)[0].printResults(),
         * gameData.get(4)[0].printResults());
         * System.out.printf("%s%n%s%n%s%n%s%n%s%n", gameData.get(0)[1].printResults(),
         * gameData.get(1)[1].printResults(),
         * gameData.get(2)[1].printResults(), gameData.get(3)[1].printResults(),
         * gameData.get(4)[1].printResults());
         */
    }
}
