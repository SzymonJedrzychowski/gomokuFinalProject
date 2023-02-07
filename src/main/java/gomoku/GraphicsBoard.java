package gomoku;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphicsBoard extends JFrame {

    BoardDisplay board;

    GraphicsBoard(int boardSize) {
        this.setSize(new Dimension(500, 500));
        board = new BoardDisplay(boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(board);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void makeMove(ArrayList<ArrayList<Integer>> gameBoard) {
        board.makeMove(gameBoard);
    }
}