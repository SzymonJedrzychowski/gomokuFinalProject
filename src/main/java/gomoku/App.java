package gomoku;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        HashMap<Integer, Integer> result;
        GameEnvironment game = new GameEnvironment(5);
        int move;
        Minimax player1 = new Minimax(3);
        while(true){
            try {
                move = player1.move(game);
                System.out.println(move);
                game.move(move);
            } catch (Exception e) {
                System.out.println(e);
                break;
            }
            game.printBoard();
            result = game.ifTerminal();
            if(result.get(0) != 0){
                System.out.printf("Player %d has won.", result.get(1));
                break;
            }
        }
    }
}
