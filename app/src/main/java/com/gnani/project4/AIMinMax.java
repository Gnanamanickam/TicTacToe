package com.gnani.project4;

import java.util.ArrayList;

/**
 * This class is used to read in a state of a tic tac toe board. It creates a MinMax object and passes the state to it. What returns is a list
 * of possible moves for the player X that have been given min/max values by the method findMoves.
 *
 * @author Mark Hallenbeck
 *
 * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
 *
 */
public class AIMinMax {

    private String[] init_board;
    private ArrayList<Node> movesList;

    AIMinMax(String value)
    {
        init_board = value.split("[ ]+");
        if(init_board.length != 9)
            System.exit(-1);
        MinMax sendIn_InitState = new MinMax(init_board);
        movesList = sendIn_InitState.findMoves();
        getBestMove();
    }

    /**
     * goes through a node list and prints out the moves with the best result for player X
     * checks the min/max function of each state and only recomends a path that leads to a win or tie
     */
    int getBestMove()
    {
        for(int x = 0; x < movesList.size(); x++)
        {
            Node temp = movesList.get(x);
            if(temp.getMinMax() == 10 || temp.getMinMax() == 0)
                return temp.getMovedTo();
        }
        return 0;
    }

}

