package gomoku;

import java.util.HashMap;

public class PlayGames {
    int gamesOnSide;
    int boardSize;
    Player player1;
    Player player2;

    PlayGames(int gamesOnSide, int boardSize, Player player1, Player player2) {
        this.gamesOnSide = gamesOnSide;
        this.boardSize = boardSize;
        this.player1 = player1;
        this.player2 = player2;
    }

    public GameData[] play() {
        HashMap<Integer, Integer> result;
        MoveData move;
        GameEnvironment game;
        try {
            game = new GameEnvironment(boardSize);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        int currentGame = 0;
        GameData[] gameData = {new GameData(), new GameData()};
        int currentMove = 0;
        while (currentGame < gamesOnSide) {
            game.resetState();
            currentMove = 0;
            while (true) {
                try {
                    if (game.getCurrentPlayer() == 1) {
                        move = player1.move(game);
                        gameData[0].addData(currentMove, move.time, move.memoryUsed);
                        gameData[1].addData(currentMove);
                    } else {
                        move = player2.move(game);
                        gameData[0].addData(currentMove);
                        gameData[1].addData(currentMove, move.time, move.memoryUsed);
                    }
                    game.move(move.selectedMove);
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    gameData[0].finishGame(result.get(1));
                    break;
                }
                currentMove += 1;
            }
            game.resetState();
            currentMove = 0;
            while (true) {
                try {
                    if (game.getCurrentPlayer() == 1) {
                        move = player2.move(game);
                        gameData[0].addData(currentMove);
                        gameData[1].addData(currentMove, move.time, move.memoryUsed);
                    } else {
                        move = player1.move(game);
                        gameData[0].addData(currentMove, move.time, move.memoryUsed);
                        gameData[1].addData(currentMove);
                    }
                    
                    game.move(move.selectedMove);
                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    gameData[1].finishGame(result.get(1));
                    break;
                }
                currentMove += 1;
            }
            currentGame += 1;
        }
        return gameData;
    }

}
