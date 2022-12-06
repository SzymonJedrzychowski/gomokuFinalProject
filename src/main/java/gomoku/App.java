package gomoku;

import java.util.HashMap;

public class App {
    public static void main(String[] args) {
        HashMap<Integer, Integer> result;
        GameEnvironment game = new GameEnvironment(7, true);
        Player player1 = new AlphaBetaPruning(3);
        Player player2 = new AlphaBetaPruning(3);
        int move;
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
                System.out.printf("Player %d has won.", result.get(1));
                break;
            }
        }
    }
}
