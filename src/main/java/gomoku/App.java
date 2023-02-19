package gomoku;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class App {

    public static void main(String[] args) {
        int limit;
        for (limit = 0; limit < 5; limit++) {
            Player player1 = new IterativeDeepening(limit, false);
            Player player2 = new AlphaBetaPruning(limit, false);
            PlayGames games;
            for (int i = 0; i < 10; i++) {
                Timestamp s = new Timestamp(System.currentTimeMillis());
                ArrayList<Integer> results;
                games = new PlayGames(100, player1, player2);
                results = games.play(7);
                Timestamp e = new Timestamp(System.currentTimeMillis());
                System.out.printf("F: %d/%d/%d %d%n", results.get(0), results.get(1), results.get(2),
                        e.getTime() - s.getTime());
                Timestamp s2 = new Timestamp(System.currentTimeMillis());
                games = new PlayGames(100, player2, player1);
                results = games.play(7);
                Timestamp e2 = new Timestamp(System.currentTimeMillis());
                System.out.printf("S: %d/%d/%d %d%n", results.get(0), results.get(1), results.get(2),
                        e2.getTime() - s2.getTime());
            }
        }
    }
}
