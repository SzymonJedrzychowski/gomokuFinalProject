package gomoku;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        int limit = 3;
        Player player1 = new Minimax(limit, false);
        Player player2 = new Minimax(limit, false);
        PlayGames games;
        ArrayList<Integer> results;
        games = new PlayGames(100, player1, player2);
        results = games.play(7);
        System.out.printf("'%d/%d/%d%n", results.get(0), results.get(1), results.get(2));
        //games = new PlayGames(100, player2, player1);
        //results = games.play(7);
        //System.out.printf("'%d/%d/%d%n", results.get(0), results.get(1), results.get(2));

    }
}
