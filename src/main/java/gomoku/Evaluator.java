package gomoku;

import java.util.BitSet;
import java.util.HashMap;

public class Evaluator {
    private HashMap<BitSet, Integer> points = new HashMap<>();

    Evaluator() {
        int[] rewards = { 1, 10, 50 };
        // Doubles
        BitSet a1 = new BitSet(5);
        a1.set(0, 2);
        points.put(a1, rewards[0]);

        BitSet a2 = new BitSet(5);
        a2.set(1, 3);
        points.put(a2, rewards[0]);

        BitSet a3 = new BitSet(5);
        a3.set(2, 4);
        points.put(a3, rewards[0]);

        BitSet a4 = new BitSet(5);
        a4.set(3, 5);
        points.put(a4, rewards[0]);

        BitSet b1 = new BitSet(5);
        b1.set(0, 2);
        b1.set(3);
        points.put(b1, rewards[0]);

        BitSet b2 = new BitSet(5);
        b2.set(0, 2);
        b2.set(4);
        points.put(b2, rewards[0]);

        BitSet b3 = new BitSet(5);
        b3.set(1, 3);
        b3.set(4);
        points.put(b3, rewards[0]);

        BitSet b4 = new BitSet(5);
        b4.set(2, 4);
        b4.set(0);
        points.put(b4, rewards[0]);

        BitSet b5 = new BitSet(5);
        b5.set(3, 5);
        b5.set(0);
        points.put(b5, rewards[0]);

        BitSet b6 = new BitSet(5);
        b6.set(3, 5);
        b6.set(1);
        points.put(b6, rewards[0]);

        BitSet c1 = new BitSet(5);
        c1.set(0, 2);
        c1.set(3, 5);
        points.put(c1, rewards[0]);

        // Threes
        BitSet d1 = new BitSet(5);
        d1.set(0, 3);
        points.put(d1, rewards[1]);

        BitSet d2 = new BitSet(5);
        d2.set(1, 4);
        points.put(d2, rewards[1]);

        BitSet d3 = new BitSet(5);
        d3.set(2, 5);
        points.put(d3, rewards[1]);

        BitSet e1 = new BitSet(5);
        e1.set(0, 3);
        e1.set(4);
        points.put(e1, rewards[1]);

        BitSet e2 = new BitSet(5);
        e2.set(2, 5);
        e2.set(0);
        points.put(e2, rewards[1]);

        // Fours
        BitSet f1 = new BitSet(5);
        f1.set(0, 4);
        points.put(f1, rewards[2]);

        BitSet f2 = new BitSet(5);
        f2.set(1, 5);
        points.put(f2, rewards[2]);
    }

    public int calculateEvaluation(GameEnvironment game) {
        int boardSize = game.getBoardSize();
        BitSet gameBoardOne = game.getGameBoardOne();
        BitSet gameBoardTwo = game.getGameBoardTwo();
        int scoreOne = 0;
        int scoreTwo = 0;
        int thisPoints;
        BitSet temp1 = new BitSet(5);
        BitSet temp2 = new BitSet(5);

        // HORIZONTAL
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize - 4; col++) {
                temp1 = gameBoardOne.get(row * boardSize + col, row * boardSize + col + 5);
                temp2 = gameBoardTwo.get(row * boardSize + col, row * boardSize + col + 5);

                if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp1, 0);
                    scoreOne += thisPoints;
                } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp2, 0);
                    scoreTwo += thisPoints;
                }

            }
        }

        // VERTICAL
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize - 4; row++) {
                temp1.set(0, gameBoardOne.get(col + row * boardSize));
                temp1.set(1, gameBoardOne.get(col + row * boardSize + boardSize));
                temp1.set(2, gameBoardOne.get(col + row * boardSize + 2 * boardSize));
                temp1.set(3, gameBoardOne.get(col + row * boardSize + 3 * boardSize));
                temp1.set(4, gameBoardOne.get(col + row * boardSize + 4 * boardSize));

                temp2.set(0, gameBoardTwo.get(col + row * boardSize));
                temp2.set(1, gameBoardTwo.get(col + row * boardSize + boardSize));
                temp2.set(2, gameBoardTwo.get(col + row * boardSize + 2 * boardSize));
                temp2.set(3, gameBoardTwo.get(col + row * boardSize + 3 * boardSize));
                temp2.set(4, gameBoardTwo.get(col + row * boardSize + 4 * boardSize));

                if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp1, 0);
                    scoreOne += thisPoints;
                } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp2, 0);
                    scoreTwo += thisPoints;
                }
            }
        }

        // DIAGONAL (L->R)
        for (int col = 0; col < boardSize; col++) {
            if ((boardSize - col) - 5 < 0) {
                break;
            }
            for (int row = 0; row < boardSize - col - 4; row++) {
                temp1.set(0, gameBoardOne.get(col + row * boardSize + row));
                temp1.set(1, gameBoardOne.get(col + row * boardSize + boardSize + 1 + row));
                temp1.set(2, gameBoardOne.get(col + row * boardSize + 2 * boardSize + 2 + row));
                temp1.set(3, gameBoardOne.get(col + row * boardSize + 3 * boardSize + 3 + row));
                temp1.set(4, gameBoardOne.get(col + row * boardSize + 4 * boardSize + 4 + row));

                temp2.set(0, gameBoardTwo.get(col + row * boardSize + row));
                temp2.set(1, gameBoardTwo.get(col + row * boardSize + boardSize + 1 + row));
                temp2.set(2, gameBoardTwo.get(col + row * boardSize + 2 * boardSize + 2 + row));
                temp2.set(3, gameBoardTwo.get(col + row * boardSize + 3 * boardSize + 3 + row));
                temp2.set(4, gameBoardTwo.get(col + row * boardSize + 4 * boardSize + 4 + row));

                if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp1, 0);
                    scoreOne += thisPoints;
                } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp2, 0);
                    scoreTwo += thisPoints;
                }

                if (col > 0) {
                    temp1.set(0, gameBoardOne.get(col + row * boardSize + row + boardSize - 1));
                    temp1.set(1, gameBoardOne.get(col + row * boardSize + boardSize + 1 + row + boardSize - 1));
                    temp1.set(2, gameBoardOne.get(col + row * boardSize + 2 * boardSize + 2 + row + boardSize - 1));
                    temp1.set(3, gameBoardOne.get(col + row * boardSize + 3 * boardSize + 3 + row + boardSize - 1));
                    temp1.set(4, gameBoardOne.get(col + row * boardSize + 4 * boardSize + 4 + row + boardSize - 1));

                    temp2.set(0, gameBoardTwo.get(col + row * boardSize + row + boardSize - 1));
                    temp2.set(1, gameBoardTwo.get(col + row * boardSize + boardSize + 1 + row + boardSize - 1));
                    temp2.set(2, gameBoardTwo.get(col + row * boardSize + 2 * boardSize + 2 + row + boardSize - 1));
                    temp2.set(3, gameBoardTwo.get(col + row * boardSize + 3 * boardSize + 3 + row + boardSize - 1));
                    temp2.set(4, gameBoardTwo.get(col + row * boardSize + 4 * boardSize + 4 + row + boardSize - 1));

                    if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                        thisPoints = points.getOrDefault(temp1, 0);
                        scoreOne += thisPoints;
                    } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                        thisPoints = points.getOrDefault(temp2, 0);
                        scoreTwo += thisPoints;
                    }
                }
            }
        }

        // DIAGONAL (R->L)
        for (int col = boardSize - 1; col >= 4; col--) {
            for (int row = 0; row <= col - 4; row++) {
                temp1.set(0, gameBoardOne.get(col + row * (boardSize - 1)));
                temp1.set(1, gameBoardOne.get(col + (row + 1) * (boardSize - 1)));
                temp1.set(2, gameBoardOne.get(col + (row + 2) * (boardSize - 1)));
                temp1.set(3, gameBoardOne.get(col + (row + 3) * (boardSize - 1)));
                temp1.set(4, gameBoardOne.get(col + (row + 4) * (boardSize - 1)));

                temp2.set(0, gameBoardTwo.get(col + row * (boardSize - 1)));
                temp2.set(1, gameBoardTwo.get(col + (row + 1) * (boardSize - 1)));
                temp2.set(2, gameBoardTwo.get(col + (row + 2) * (boardSize - 1)));
                temp2.set(3, gameBoardTwo.get(col + (row + 3) * (boardSize - 1)));
                temp2.set(4, gameBoardTwo.get(col + (row + 4) * (boardSize - 1)));

                if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp1, 0);
                    scoreOne += thisPoints;
                } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                    thisPoints = points.getOrDefault(temp2, 0);
                    scoreTwo += thisPoints;
                }

                if (col < boardSize - 1) {
                    temp1.set(0, gameBoardOne.get(col + row * (boardSize - 1) + boardSize + 1));
                    temp1.set(1, gameBoardOne.get(col + (row + 1) * (boardSize - 1) + boardSize + 1));
                    temp1.set(2, gameBoardOne.get(col + (row + 2) * (boardSize - 1) + boardSize + 1));
                    temp1.set(3, gameBoardOne.get(col + (row + 3) * (boardSize - 1) + boardSize + 1));
                    temp1.set(4, gameBoardOne.get(col + (row + 4) * (boardSize - 1) + boardSize + 1));

                    temp2.set(0, gameBoardTwo.get(col + row * (boardSize - 1) + boardSize + 1));
                    temp2.set(1, gameBoardTwo.get(col + (row + 1) * (boardSize - 1) + boardSize + 1));
                    temp2.set(2, gameBoardTwo.get(col + (row + 2) * (boardSize - 1) + boardSize + 1));
                    temp2.set(3, gameBoardTwo.get(col + (row + 3) * (boardSize - 1) + boardSize + 1));
                    temp2.set(4, gameBoardTwo.get(col + (row + 4) * (boardSize - 1) + boardSize + 1));

                    if (temp1.cardinality() > 1 && temp2.cardinality() == 0) {
                        thisPoints = points.getOrDefault(temp1, 0);
                        scoreOne += thisPoints;
                    } else if (temp2.cardinality() > 1 && temp1.cardinality() == 0) {
                        thisPoints = points.getOrDefault(temp2, 0);
                        scoreTwo += thisPoints;
                    }
                }
            }
        }

        if(game.getCurrentPlayer() == 1){
            return 10*scoreOne-15*scoreTwo;
        }else{
            return 15*scoreOne-10*scoreTwo;
        }
    }

}