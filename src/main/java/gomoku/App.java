package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        int maxLimit = 4;
        int boardSize = 7;
        int gameNumber = 100;
        for (int limit = 1; limit < maxLimit; limit++) {
            Player player1 = new IterativeDeepening(limit, false);
            Player player2 = new IterativeDeepening(limit, false);
            PlayGames games;
            Timestamp s = new Timestamp(System.currentTimeMillis());
            ArrayList<Integer> results;
            games = new PlayGames(gameNumber, player1, player2);
            results = games.play(boardSize);
            Timestamp e = new Timestamp(System.currentTimeMillis());
            System.out.printf("F: %d/%d/%d %d%n", results.get(0), results.get(1), results.get(2),
                    e.getTime() - s.getTime());
            Timestamp s2 = new Timestamp(System.currentTimeMillis());
            games = new PlayGames(gameNumber, player2, player1);
            results = games.play(boardSize);
            Timestamp e2 = new Timestamp(System.currentTimeMillis());
            System.out.printf("S: %d/%d/%d %d%n", results.get(0), results.get(1), results.get(2),
                    e2.getTime() - s2.getTime());
        }
    }
}
