package gomoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PlayGames {
    int gamesOnSide;
    Player player1;
    Player player2;

    PlayGames(int gamesOnSide, Player player1, Player player2) {
        this.gamesOnSide = gamesOnSide;
        this.player1 = player1;
        this.player2 = player2;
    }

    public ArrayList<Integer> play(int boardSize) {
        ArrayList<Integer> results = new ArrayList<>(Arrays.asList(0, 0, 0));
        HashMap<Integer, Integer> result;
        int move;
        GameEnvironment game = new GameEnvironment(boardSize, false);
        int currentGame = 0;
        while (currentGame < gamesOnSide) {
            game.resetState();
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
                // game.printBoard();
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    if (result.get(1) == 1) {
                        results.set(0, results.get(0) + 1);
                    } else if (result.get(1) == -1) {
                        results.set(2, results.get(2) + 1);
                    } else {
                        results.set(1, results.get(1) + 1);
                    }
                    // System.out.printf("Game %3d finished. ", currentGame);
                    // System.out.printf("Player %2d has won.\n", result.get(1));
                    break;
                }
            }
            currentGame += 1;
        }
        return results;
    }

}
