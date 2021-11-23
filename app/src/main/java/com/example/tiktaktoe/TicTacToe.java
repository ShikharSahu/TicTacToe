package com.example.tiktaktoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tiktaktoe.databinding.ActivityTicTacToeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class TicTacToe extends AppCompatActivity {

    private ActivityTicTacToeBinding binding;
    private boolean isSinglePlayer;

    private ArrayList<Button> allButtons = new ArrayList<>();

    private final String CROSS = "X";
    private final String CIRCLE = "O";

    private final String YOU = CROSS;
    private final String COMP = CIRCLE;

    private final Enum YOU_TURN = Symbols.Cross;
    private final Enum COMP_TURN = Symbols.Circle;

    private Enum firstTurn = Symbols.Cross;
    private Enum currentTurn = Symbols.Cross;
    volatile int moves = 9;

    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicTacToeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isSinglePlayer = getIntent().getBooleanExtra(MainActivity.IS_SINGLE_PLAYER_KEY,false);

        allButtons.add(binding.xo00);
        allButtons.add(binding.xo01);
        allButtons.add(binding.xo02);
        allButtons.add(binding.xo10);
        allButtons.add(binding.xo11);
        allButtons.add(binding.xo12);
        allButtons.add(binding.xo20);
        allButtons.add(binding.xo21);
        allButtons.add(binding.xo22);

        if (isSinglePlayer){
            setUpBoardSinglePlayer();
        }
        else{
            setUpBoardMultiPlayer();
        }

    }

    private void setUpBoardMultiPlayer() {

        View.OnClickListener onClickListener = v -> {
            ((Button)v).setText(YOU);
            moves--;
            checkIfWinMultiPlayer(YOU);
            v.setClickable(false);
            currentTurn = COMP_TURN;
            performCompTurn();
        };
        for(Button button : allButtons){
            button.setOnClickListener(onClickListener);
        }

        if (!currentTurn.equals(YOU_TURN)) {
            performCompTurn();
        }
    }

    private void performCompTurn() {
        if(moves<=0){
            askToResetWithAlertBox("Draw \nUsers Out of valid moves");
        }
        ArrayList<Button> buttonsToBeTurnedOffOn = new ArrayList<>();
        for(Button button : allButtons){
            if(button.getText().toString().equals("")) buttonsToBeTurnedOffOn.add(button);
        }
        for(Button button : buttonsToBeTurnedOffOn){
            button.setClickable(false);
        }



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                Random random = new Random(System.currentTimeMillis());
                int rInt = random.nextInt(buttonsToBeTurnedOffOn.size());
                Button toBePressed = buttonsToBeTurnedOffOn.remove(rInt);
                toBePressed.setText(COMP);
                moves --;

                currentTurn = YOU_TURN;
                for( Button button : buttonsToBeTurnedOffOn){
                    button.setClickable(true);
                }
                buttonsToBeTurnedOffOn.clear();


            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            checkIfWinMultiPlayer(COMP);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkIfWinMultiPlayer(String player) {
        boolean won = checkIfWin(player);
        if (won && !gameOver){
            gameOver = true;
            askToResetWithAlertBox(player + " Won!");
        }
        else{
            if(moves<=0){ askToResetWithAlertBox("Draw \nUsers Out of valid moves"); }
        }
    }


    private void setUpBoardSinglePlayer() {

        setTurnLabel();

        View.OnClickListener onClickListener = v -> {
            moves--;
            if(currentTurn == Symbols.Cross){
                ((Button)v).setText(CROSS);
            }
            else{
                ((Button)v).setText(CIRCLE);
            }
            v.setClickable(false);
            Toast.makeText(TicTacToe.this,v.getId()+ " clicked",Toast.LENGTH_SHORT).show();

            boolean won = checkIfWin(currentTurn.equals(Symbols.Cross) ? CROSS : CIRCLE);

            if(!won){
                if(currentTurn == Symbols.Cross){
                    currentTurn = Symbols.Circle;
                }
                else{
                    currentTurn = Symbols.Cross;
                }
                setTurnLabel();

            }
            else {
                askToResetWithAlertBox((currentTurn.equals(Symbols.Cross) ? CROSS : CIRCLE )+ " wins!");
            }
            if (!won && moves<=0){
                askToResetWithAlertBox("Draw");
            }

        };

        for (Button button : allButtons) button.setOnClickListener(onClickListener);

    }

    public void setTurnLabel(){
        if(currentTurn .equals( Symbols.Cross)){
            binding.tvTurnOf.setText("Turn of: "+ CROSS);
        }
        else{
            binding.tvTurnOf.setText("Turn of: "+ CIRCLE);
        }
    }


    private void resetBoardSinglePlayer() {

        gameOver = false;

        for (Button x : allButtons){
            x.setText("");
            x.setClickable(true);
        }

        moves = 9;

        currentTurn = (currentTurn.equals(Symbols.Cross)) ? Symbols.Circle : Symbols.Cross;
        firstTurn = currentTurn;

        if( !isSinglePlayer){
            if (!currentTurn.equals(YOU_TURN)) {
                performCompTurn();
            }
        }
        else {
            setTurnLabel();
        }

    }

    private void askToResetWithAlertBox(String Message) {
        new MaterialAlertDialogBuilder(TicTacToe.this)
                .setTitle("Result")
                .setMessage(Message)
                .setPositiveButton("Reset", (dialog, which) -> resetBoardSinglePlayer())
                .setCancelable(true)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean checkIfWin(String currentTurnSymbol) {

        String xo00 = binding.xo00.getText().toString();
        String xo01 = binding.xo01.getText().toString();
        String xo02 = binding.xo02.getText().toString();
        String xo10 = binding.xo10.getText().toString();
        String xo11 = binding.xo11.getText().toString();
        String xo12 = binding.xo12.getText().toString();
        String xo20 = binding.xo20.getText().toString();
        String xo21 = binding.xo21.getText().toString();
        String xo22 = binding.xo22.getText().toString();

        boolean horizontal = symbolMatch(xo00, xo01, xo02, currentTurnSymbol)
                || symbolMatch(xo10, xo11, xo12, currentTurnSymbol)
                || symbolMatch(xo20, xo21, xo22, currentTurnSymbol);

        boolean vertical = symbolMatch(xo00, xo10, xo20, currentTurnSymbol)
                || symbolMatch(xo01, xo11, xo21, currentTurnSymbol)
                || symbolMatch(xo02, xo12, xo22, currentTurnSymbol);

        boolean diagonal = symbolMatch(xo00, xo11, xo22, currentTurnSymbol)
                || symbolMatch(xo02, xo11, xo20, currentTurnSymbol);

        return horizontal || vertical || diagonal;
    }

    private boolean symbolMatch(String s1, String s2, String s3, String currentTurnSymbol){
        return s1.equals(currentTurnSymbol) && s2.equals(currentTurnSymbol) && s3.equals(currentTurnSymbol);
    }

    interface CallbackInterface{
        void callbackMethod();
    }

}