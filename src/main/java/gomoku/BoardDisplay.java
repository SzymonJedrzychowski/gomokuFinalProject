package gomoku;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class BoardDisplay extends JPanel {
    private int boardSize;
    private int windowSize=500;
    private int difference;
    private Graphics2D g2D;
    public ArrayList<ArrayList<Integer>> gameBoard;

    
    BoardDisplay(int boardSize) {
        setPreferredSize(new Dimension(windowSize, windowSize));
        this.difference = (int)((windowSize-100)/(boardSize-1));
        this.boardSize = boardSize;
        this.gameBoard = new ArrayList<>();
        for(int i=0; i<boardSize; i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j=0; j<boardSize; j++){
                temp.add(0);
            }
            this.gameBoard.add(temp);
        }
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
        for(int row = 0; row<boardSize; row++){
            for(int column = 0; column<boardSize; column++){
                if(gameBoard.get(row).get(column) == 1){
                    g2D.setPaint(Color.white);
                    x = column * difference + 50 - (int)(0.4*difference);
                    y = row * difference + 50 - (int)(0.4*difference);
                    g2D.fillOval(x, y, (int)(0.8*difference), (int)(0.8*difference));
                }else if(gameBoard.get(row).get(column) == -1){
                    g2D.setPaint(Color.black);
                    x = column * difference + 50 - (int)(0.4*difference);
                    y = row * difference + 50 - (int)(0.4*difference);
                    g2D.fillOval(x, y, (int)(0.8*difference), (int)(0.8*difference));
                }
            }
        }
    }

    public void makeMove(ArrayList<ArrayList<Integer>> gameBoard){
        this.gameBoard = gameBoard;
        repaint();
    }
}