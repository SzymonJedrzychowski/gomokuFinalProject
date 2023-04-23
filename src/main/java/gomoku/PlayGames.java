package gomoku;

import java.util.HashMap;

/**
 * Class responsible for playing the games for the experiment.
 */
public class PlayGames {
    int gamesOnSide;
    int boardSize;
    Player player1;
    Player player2;
    boolean isLimitTime;
    
    /**
     * @param gamesOnSide number of games per experiment
     * @param boardSize size of the board
     * @param player1 first player
     * @param player2 second player
     * @param isLimitTime if the limit of the search is time
     */
    PlayGames(int gamesOnSide, int boardSize, Player player1, Player player2, boolean isLimitTime) {
        this.gamesOnSide = gamesOnSide;
        this.boardSize = boardSize;
        this.player1 = player1;
        this.player2 = player2;
        this.isLimitTime = isLimitTime;
    }

    /**
     * Method used to conduct the experiment.
     * 
     * @param displayInformation if the information of played games should be displayed
     * @return GameData[] of played games
     */
    public GameData[] play(boolean displayInformation) {
        HashMap<Integer, Integer> result;
        MoveData move;
        GameEnvironment game;
        
        //Create the game environment
        try {
            game = new GameEnvironment(boardSize);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        
        //Play test games
        if(isLimitTime){
            playTestGames(game, 1);
        }else{
            playTestGames(game, 10);
        }
        
        GameData[] gameData = { new GameData(), new GameData() };
        int currentMove;
        boolean notException = true;
        
        if (displayInformation) {
            System.out.printf("Player 1: %s %n", player1.getClass());
            System.out.printf("Player 2: %s %n", player2.getClass());
        }
        
        //Play the games
        for (int currentGame = 0; currentGame < gamesOnSide; currentGame ++) {
            notException = true;
            game.resetState();
            currentMove = 0;
            
            //Play a game
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
                    System.out.printf("Game %d (1) has been stopped: %s%n", currentGame, e.getMessage());
                    notException = false;
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    gameData[0].finishGame(result.get(1));
                    break;
                }
                currentMove += 1;
            }
            
            if (displayInformation && notException){
                System.out.printf("Game %d (1) has ended.%n", currentGame);
            }
            
            notException = true;
            game.resetState();
            currentMove = 0;

            //Play the games with reversed players
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
                    System.out.printf("Game %d (2) has been stopped: %s%n", currentGame, e.getMessage());
                    notException = false;
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    gameData[1].finishGame(result.get(1));
                    break;
                }
                currentMove += 1;
            }

            if (displayInformation && notException){
                System.out.printf("Game %d (2) has ended.%n", currentGame);
            }
        }
        return gameData;
    }
    
    /**
     * Method used to play test games.
     * 
     * @param game game environment
     * @param testGamesNumber number of test games to be played
     */
    private void playTestGames(GameEnvironment game, int testGamesNumber){
        MoveData move;
        HashMap<Integer, Integer> result;
        
        //Play the test games
        for(int i = 0; i<testGamesNumber; i++) {
            game.resetState();
            while (true) {
                try {
                    if (game.getCurrentPlayer() == 1) {
                        move = player1.move(game);
                    } else {
                        move = player2.move(game);
                    }
                    game.move(move.selectedMove);
                } catch (Exception e) {
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    break;
                }
            }
            
            game.resetState();
            while (true) {
                try {
                    if (game.getCurrentPlayer() == 1) {
                        move = player2.move(game);
                    } else {
                        move = player1.move(game);
                    }
                    game.move(move.selectedMove);
                } catch (Exception e) {
                    break;
                }
                result = game.ifTerminal();
                if (result.get(0) != 0) {
                    break;
                }
            }
        }
    }

}
