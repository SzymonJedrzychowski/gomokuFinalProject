package gomoku;

import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        Player player1 = new Minimax(4);
        Player player2 = new Minimax(4);
        PlayGames games;
        ArrayList<Integer> results;
        games = new PlayGames(100, player1, player2);
        results = games.play(7);
        System.out.println(results);
        games = new PlayGames(100, player2, player1);
        results = games.play(7);
        System.out.println(results);
    }
}
