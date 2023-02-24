package gomoku;

import java.sql.Timestamp;

public class App {

    public static void main(String[] args) {
        
        int maxLimit = 5;
        int boardSize = 7;
        int gameNumber = 5;
        GameData gameData;
        GameData gameData2;
        for (int limit = maxLimit; limit <= maxLimit; limit++) {
            Player player1 = new AlphaBetaPruning(limit, true);
            Player player2 = new AlphaBetaPruning(limit, true);
            PlayGames games;
            games = new PlayGames(5, boardSize, player1, player2);
            //games.play(7);
            Timestamp s = new Timestamp(System.currentTimeMillis());
            games = new PlayGames(gameNumber, boardSize, player1, player2);
            gameData = games.play();
            Timestamp e = new Timestamp(System.currentTimeMillis());
            gameData.printTimes();
            gameData.printMemory();
            gameData.printVisits();
            Timestamp s2 = new Timestamp(System.currentTimeMillis());
            games = new PlayGames(gameNumber, boardSize, player2 , player1);
            gameData2 = games.play();
            Timestamp e2 = new Timestamp(System.currentTimeMillis());
            gameData2.printTimes();
            gameData2.printMemory();
            gameData2.printVisits();
            System.out.printf("F: %d/%d/%d %d%n", gameData.gameResults[0], gameData.gameResults[1], gameData.gameResults[2],
                    e.getTime() - s.getTime());
            System.out.printf("F: %d/%d/%d %d%n", gameData2.gameResults[0], gameData2.gameResults[1], gameData2.gameResults[2],
                    e2.getTime() - s2.getTime());
        }
        
        /**
        int maxLimit = 3;
        int boardSize = 7;
        int gameNumber = 25;
        GameData gameData;
        GameData gameData2;
        GameData gameData3;
        GameData gameData4;
        GameData gameData5;
        for (int limit = 3; limit <= maxLimit; limit++) {
            Player player1 = new AlphaBetaPruning(limit, true);
            Player player2 = new AlphaBetaPruning(limit, true);
            Player player3 = new AlphaBetaPruning_Ordered(limit, true);
            Player player4 = new IterativeDeepening(limit, false, true);
            Player player5 = new IterativeDeepening_PVS(limit, false, true);
            PlayGames games;
            System.out.printf("Depth %d%n", limit);
            games = new PlayGames(gameNumber, player1, player1);
            gameData = games.play(boardSize);
            System.out.printf("%d/%d/%d/", gameData.gameResults[0], gameData.gameResults[1], gameData.gameResults[2]);
            games = new PlayGames(gameNumber, player1, player2);
            gameData2 = games.play(boardSize);
            System.out.printf("%d/%d/%d/", gameData2.gameResults[0], gameData2.gameResults[1], gameData2.gameResults[2]);
            games = new PlayGames(gameNumber, player1, player3);
            gameData3 = games.play(boardSize);
            System.out.printf("%d/%d/%d/", gameData3.gameResults[0], gameData3.gameResults[1], gameData3.gameResults[2]);
            games = new PlayGames(gameNumber, player1, player4);
            gameData4 = games.play(boardSize);
            System.out.printf("%d/%d/%d/", gameData4.gameResults[0], gameData4.gameResults[1], gameData4.gameResults[2]);
            games = new PlayGames(gameNumber, player1, player5);
            gameData5 = games.play(boardSize);
            System.out.printf("%d/%d/%d%n", gameData5.gameResults[0], gameData5.gameResults[1], gameData5.gameResults[2]);
        }
        */
    }
}
