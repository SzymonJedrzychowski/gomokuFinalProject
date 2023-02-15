package gomoku;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        int limit = 4;
        Player player1 = new PVS(limit, false);
        Player player2 = new AlphaBetaPruning_Ordered(limit, false);
        HashMap<Integer, Integer> result;
        int move;
        GameEnvironment game = new GameEnvironment(7, false);
        while (true) {
            try {
                if (game.getCurrentPlayer() == 1) {
                    move = player1.move(game);
                } else {
                    move = player2.move(game);
                }
                game.move(move);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                break;
            }
            game.printBoard();
            result = game.ifTerminal();
            if (result.get(0) != 0) {
                // System.out.printf("Game %3d finished. ", currentGame);
                System.out.printf("Player %2d has won.\n", result.get(1));
                break;
            }
        }
        // PlayGames games;
        // ArrayList<Integer> results;
        // games = new PlayGames(100, player1, player2);
        // results = games.play(7);
        // System.out.println(results);
        // games = new PlayGames(100, player2, player1);
        // results = games.play(7);
        // System.out.println(results);
    }
}
