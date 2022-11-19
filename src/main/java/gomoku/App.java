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
        GameEnvironment game = new GameEnvironment(6);
        int move;
        Scanner scanner = new Scanner(System.in);
        while(true){
            try {
                move = scanner.nextInt();
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
        scanner.close();
    }
}
