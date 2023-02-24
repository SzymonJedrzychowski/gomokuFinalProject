package gomoku;

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

    public GameData play(int boardSize) {
        HashMap<Integer, Integer> result;
        MoveData move;
        GameEnvironment game = new GameEnvironment(boardSize);
        int currentGame = 0;
        GameData gameData = new GameData();
        while (currentGame < gamesOnSide) {
            game.resetState();
            int currentMove = 0;
            while (true) {
                try {
                    if (game.getCurrentPlayer() == 1) {
                        move = player1.move(game);
                    } else {
                        move = player2.move(game);
                    }
                    gameData.addData(currentMove, move.time, move.memoryUsed);
                    game.move(move.selectedMove);
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    gameData.finishGame(result.get(1));
                    break;
                }
                currentMove += 1;
            }
            currentGame += 1;
        }
        return gameData;
    }

}
