package com.gnani.project4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //Variable declaration
    TextView result;
    TextView status;
    int set_value = 0;
    Button start;
    private List<Button> buttons = new ArrayList<Button>();
    int count = 9;
    boolean game_ended = false;

    ArrayList<Integer> selection = new ArrayList<>();

    //Creating two worker threads .
    Thread thread1 = new Thread(new Player1());
    Thread thread2 = new Thread(new Player2());

    //Set value
    String Player_one = "X";
    String Player_two = "O";

    //Handler for worker threads.
    Handler PlayerTwoHandler;
    Handler PlayerOneHandler;

    public static final int SLEEP_TIME = 1000;
    public static final int SET_PLAYER_1 = 0;
    public static final int HANDLER_PLAYER_1 = 1;
    public static final int SET_PLAYER_2 = 2;
    public static final int HANDLER_PLAYER_2 = 3;

    // All possible win positions in a array
    int[][] win_positions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};

    //array of button ids
    private static final int[] Button_ID = {
            R.id.one,
            R.id.two,
            R.id.three,
            R.id.four,
            R.id.five,
            R.id.six,
            R.id.seven,
            R.id.eight,
            R.id.nine,
    };

    //Creating global lock for the synchronization blocks
    static final Object lock = new Object();

    //OnCreate method
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int id : Button_ID) {
            Button button = (Button) findViewById(id);
            buttons.add(button);
        }

        Button start = (Button) findViewById(R.id.start);
        result = (TextView) findViewById(R.id.result);
        status = (TextView) findViewById(R.id.status);

    }

    //Handler for threads
    @SuppressLint("HandlerLeak")
    final
    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int worker = msg.what;

            Log.i("handler", "handlerMessage: " + worker);
            //To check whether the game has produced a result .
            int value = msg.arg1;

            // Check which player has played and set the value based on it . Also to check for the result .
            if (worker == SET_PLAYER_1) {
                //Set the values on the button in board .
                setValue(value, "Player1");
                Check_Result(worker);
                if(!game_ended) {
                    Message message = PlayerTwoHandler.obtainMessage(HANDLER_PLAYER_2);
                    PlayerTwoHandler.sendMessage(message);
                }
            } else {
                setValue(value, "Player2");
                Check_Result(worker);
                if(!game_ended) {
                    Message message = PlayerOneHandler.obtainMessage(HANDLER_PLAYER_1);
                    PlayerOneHandler.sendMessage(message);
                }
            }
        }
    };

    //To check the result
    private void Check_Result(int worker) {
        for (int[] win : win_positions) {
            if (buttons.get(win[0]).getText() == buttons.get(win[1]).getText() && buttons.get(win[1]).getText() == buttons.get(win[2]).getText()) {
                CharSequence text = buttons.get(win[0]).getText();
                if (text.equals("")) continue;
                if (Player_one.equals(text)) {
                    result.setText("Player1 has won");
                    status.setText("Game has ended");
                } else if (Player_two.equals(text)) {
                    result.setText("Player2 has won");
                    status.setText("Game has ended");
                }
                //To quit the loop if the game ends
                End_Game();
                return;
            }
        }
        if (selection.isEmpty()) {
            result.setText("Game has ended in tie");
            status.setText("Game has ended");
            End_Game();
            return;
        }
        result.setText("");
    }

    private void End_Game() {
        PlayerOneHandler.getLooper().quit();
        PlayerTwoHandler.getLooper().quit();
        game_ended = true;
    }

    //When the button to start game is clicked .
    public void StartGame(View view) {

        game_ended = false;
        count = 9;
        status.setText("Game Started");
        result.setText("");
        selection.clear();

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setEnabled(true);
            buttons.get(i).setText("");
            selection.add(i);
        }

        thread1 = new Thread(new Player1());
        thread2 = new Thread(new Player2());
        thread1.start();
        thread2.start();
    }

    //To randomly select a button to play
    public int RandomSelection() {
        int value = 0;
        Collections.shuffle(selection);
        //Remove the selected value from the array
        if (selection.size() > 0) {
            value = selection.get(0);
            selection.remove(0);
        }
        return value;
    }

    private int minMax() {

        int minMaxValue = 0;
        StringBuilder minmaxBuilder = new StringBuilder();
        for (int i=0; i< 9; i++) {
            if (buttons.get(i).getText() == "X") {
                minmaxBuilder.append("X");
            } else if (buttons.get(i).getText() == "O") {
                minmaxBuilder.append("O");
            } else {
                minmaxBuilder.append("-");
            }
        }
        AIMinMax minMax = new AIMinMax(minmaxBuilder.toString());

        minMaxValue = minMax.getBestMove() - 1;
        if (minMaxValue == -1) {
            minMaxValue = 0;
        }

        selection.remove(minMaxValue);
        return minMaxValue;
    }

    //To set the value of the board .
    private void setValue(int value, String player) {

        //Check whether its player1 and if count greater than1 , set the button value to Player_one
        if (player.equalsIgnoreCase("Player1")) {
            buttons.get(value).setText(Player_one);
            buttons.get(value).setEnabled(false);
            status.setText("Player2 turn");
            //Decrease the value of the count
            count--;
        }
        //Check whether its player2 and if count greater than1 , set the button value to Player_two
        else if (player.equalsIgnoreCase("Player2")) {
            buttons.get(value).setText(Player_two);
            buttons.get(value).setEnabled(false);
            status.setText("Player1 turn");
            //Decrease the value of the count
            count--;
        }
    }

    //Thread for player 1.
    public class Player1 implements Runnable {

        @Override
        public void run() {
            Looper.prepare();
            Message message = handler.obtainMessage(SET_PLAYER_1);
            message.arg1 = RandomSelection();
            handler.sendMessage(message);
            PlayerOneHandler = new Handler(Looper.myLooper()) {
                public void handleMessage(Message message1) {
                    for (int i = 0; i < buttons.size(); i++) {
                        //Check whether the button is enabled .
                        if (buttons.get(i).isEnabled()) {
                            Message message = handler.obtainMessage(SET_PLAYER_1);
                            message.arg1 = RandomSelection();
                            //Send the message to handler
                            try {
                                Thread.sleep(SLEEP_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(message);
                            return;
                        }
                    }
                }
            };
            Looper.loop();
        }
    }

    //Thread for player2
    public class Player2 implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            PlayerTwoHandler = new Handler(Looper.myLooper()) {
                public void handleMessage(Message message) {
                    for (int i = 0; i < buttons.size(); i++) {
                        //Check whether the button is enabled .
                        if (buttons.get(i).isEnabled()) {
                            Message message2 = handler.obtainMessage(SET_PLAYER_2);
                            message2.arg1 = RandomSelection();
                            //Send the message to handler
                            try {
                                //Sleep method of thread
                                Thread.sleep(SLEEP_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(message2);
                            return;
                        }
                    }
                }
            };
            Looper.loop();
        }
    }
}



