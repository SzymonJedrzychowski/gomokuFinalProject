package gomoku;

import java.awt.*;
import java.util.BitSet;

import javax.swing.*;

public class BoardDisplay extends JPanel {
    private int boardSize;
    private int windowSize=500;
    private int difference;
    private Graphics2D g2D;
    private BitSet boardOne;
    private BitSet boardTwo;

    
    BoardDisplay(int boardSize) {
        setPreferredSize(new Dimension(windowSize, windowSize));
        this.difference = (int)((windowSize-100)/(boardSize-1));
        this.boardSize = boardSize;
        this.boardOne = new BitSet();
        this.boardTwo = new BitSet();
    }

    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        g2D.setPaint(Color.gray);
        g2D.setStroke(new BasicStroke(2));
        for(int i=0; i<boardSize;i++){
            g2D.drawLine(50+i*difference, 50, 50+i*difference, 50+(boardSize-1)*difference);
            g2D.drawLine(50, 50+i*difference, 50+(boardSize-1)*difference, 50+i*difference);
        }
        int x;
        int y;
        g2D.setPaint(Color.white);
        for (int i = boardOne.nextSetBit(0); i != -1; i = boardOne.nextSetBit(i + 1)) {
            x = (i%boardSize) * difference + 50 - (int)(0.4*difference);
            y = (int)(i/boardSize) * difference + 50 - (int)(0.4*difference);
            g2D.fillOval(x, y, (int)(0.8*difference), (int)(0.8*difference));
        }
        g2D.setPaint(Color.black);
        for (int i = boardTwo.nextSetBit(0); i != -1; i = boardTwo.nextSetBit(i + 1)) {
            x = (i%boardSize) * difference + 50 - (int)(0.4*difference);
            y = (int)(i/boardSize) * difference + 50 - (int)(0.4*difference);
            g2D.fillOval(x, y, (int)(0.8*difference), (int)(0.8*difference));
        }
    }

    public void makeMove(BitSet boardOne, BitSet boardTwo){
        this.boardOne = boardOne;
        this.boardTwo = boardTwo;
        repaint();
    }
}