package com.gnani.project4;

/**
 * This class is used to store a state of a tic tac toe puzzle in the form of a string as well as a min/max value
 * Methods are included to set the min/max value depending on whose turn it is, X or O
 * @author Mark Hallenbeck
 *
 * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
 *
 */

public class Node {
    private String[] state;
    private int minMaxValue;
    private int movedTo;

    // All possible win positions in a array
    int[][] win_positions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};

    Node(String[] stateOfPuzzle, int move) {
        state = stateOfPuzzle;
        movedTo = move;
        minMaxValue = -1;
    }

    int getMovedTo()
    {
        return movedTo;
    }

    /**
     * checks for all the ways that O can win and sets minmax to -10. If it is a draw, sets it to 0
     */
    void setMinMax_for_O() {
        if(checkForDraw())
            minMaxValue = 0;

        for (int[] win : win_positions) {
            if(state[win[0]].equals("O") && state[win[1]].equals("O") && state[win[2]].equals("O")) {
                minMaxValue = -10;
                return;
            }
        }
    }

    /**
     * checks for all the ways that X can win and sets minmax to 10. If a draw, sets minmax to 0
     */
    void setMinMax_for_X() {
        if(checkForDraw())
            minMaxValue = 0;

        for (int[] win : win_positions) {
            if(state[win[0]].equals("X") && state[win[1]].equals("X") && state[win[2]].equals("X")) {
                minMaxValue = 10;
                return;
            }
        }
    }

    void setMinMax(int x) {
        minMaxValue = x;
    }

    /**
     * check the state to see if it is a draw (no b's in the string only X and O)
     * @return true if its a draw, false if not
     */
    boolean checkForDraw() {
        for(int x = 0; x < state.length; x++)
        {
            if(state[x].equals("b"))
                return false;
        }
        return true;
    }

    int getMinMax()  {
        return minMaxValue;
    }

    String[] getInitStateString() {
        return state;
    }

}
