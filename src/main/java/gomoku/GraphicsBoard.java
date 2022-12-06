package gomoku;

import javax.swing.*;
import java.awt.*;
import java.util.BitSet;

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

    public void makeMove(BitSet boardOne, BitSet boardTwo) {
        board.makeMove(boardOne, boardTwo);
    }
}