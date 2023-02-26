package gomoku;

public class App {

    public static void main(String[] args) {
        
        int limit = 1;
        int boardSize = 7;
        int gameNumber = 10;
        boolean closeMoves = false;
        GameData[] gameData;
        Player player1 = new AlphaBetaPruning(limit, closeMoves);
        Player player2 = new AlphaBetaPruning(limit, closeMoves);
        Player player3 = new AlphaBetaPruning(limit, closeMoves);
        Player player4 = new AlphaBetaPruning(limit, closeMoves);
        Player player5 = new AlphaBetaPruning(limit, closeMoves);

        Player p1 = player1;
        Player p2 = player1;

        PlayGames games;

        games = new PlayGames(gameNumber, boardSize, p1, p2);
        gameData = games.play();

        gameData[0].printData();
        gameData[1].printData();


    }
}
