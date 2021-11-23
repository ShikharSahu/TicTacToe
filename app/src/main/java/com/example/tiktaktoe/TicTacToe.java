package com.example.tiktaktoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tiktaktoe.databinding.ActivityTicTacToeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Random;

public class TicTacToe extends AppCompatActivity implements CallbackInterface {

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

    int youWins = 0, compWins = 0, xWins = 0, oWins = 0;

    Handler handler = new Handler();

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
            setUpBoardMultiplePlayers();
        }

    }

    private void setUpBoardSinglePlayer() {
//        Toast.makeText(this,Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();

        setTurnLabel();
        setScoreCard();

        View.OnClickListener onClickListener = v -> {
            if(!movesLeft()) {
                return;
            }
            ((Button)v).setText(YOU);
            v.setClickable(false);
            currentTurn = COMP_TURN;
            setTurnLabel();
            performCompTurnMP();
        };
        for(Button button : allButtons){
            button.setOnClickListener(onClickListener);
        }

        if (!currentTurn.equals(YOU_TURN)) {
            performCompTurnMP();
        }
    }

    private void performCompTurnMP() {

        if(checkIfWin(YOU)) {
            youWins++;
            askToResetWithAlertBox("You Won!!");
            return;
        }
        else if(!movesLeft()){
            askToResetWithAlertBox("Draw \nUsers Out of valid moves");
            return;
        }
        ArrayList<Button> buttonsToBeTurnedOffOn = new ArrayList<>();
        for(Button button : allButtons){
            if(button.getText().toString().equals("")) buttonsToBeTurnedOffOn.add(button);
        }
        for(Button button : buttonsToBeTurnedOffOn){
            button.setClickable(false);
        }
        Log.d("eee", "performCompTurn: "+buttonsToBeTurnedOffOn.size());

        Random random = new Random(System.currentTimeMillis());
        int rInt = random.nextInt(buttonsToBeTurnedOffOn.size());
        Button toBePressed = buttonsToBeTurnedOffOn.remove(rInt);
        ThreadedMoveMaker threadedMoveMaker = new ThreadedMoveMaker(this, toBePressed, buttonsToBeTurnedOffOn , handler);

    }



    private void setUpBoardMultiplePlayers() {

        setTurnLabel();
        setScoreCard();

        View.OnClickListener onClickListener = v -> {
            if(currentTurn == Symbols.Cross){
                ((Button)v).setText(CROSS);
            }
            else{
                ((Button)v).setText(CIRCLE);
            }
            v.setClickable(false);
//            Toast.makeText(TicTacToe.this,v.getId()+ " clicked",Toast.LENGTH_SHORT).show();

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
                if(currentTurn == Symbols.Cross){
                    xWins++;
                }
                else{
                    oWins++;
                }
                askToResetWithAlertBox((currentTurn.equals(Symbols.Cross) ? CROSS : CIRCLE )+ " wins!");
            }
            if (!won && !movesLeft()){
                askToResetWithAlertBox("Draw \nUsers Out of valid moves");
            }

        };

        for (Button button : allButtons) button.setOnClickListener(onClickListener);

    }

    public void setTurnLabel(){
        if (!isSinglePlayer) {
            if (currentTurn.equals(Symbols.Cross)) {
                binding.tvTurnOf.setText("Turn of: " + CROSS);
            } else {
                binding.tvTurnOf.setText("Turn of: " + CIRCLE);
            }
        }
        else{
            if(currentTurn.equals(YOU_TURN)){
                binding.tvTurnOf.setText("Turn of: " + "YOU/"+YOU);
            }
            else{
                binding.tvTurnOf.setText("Turn of: " + "COMP/"+COMP);
            }
        }
    }

    private void resetBoard() {

        for (Button x : allButtons){
            x.setText("");
            x.setClickable(true);
        }




        if(isSinglePlayer){
            if (!currentTurn.equals(YOU_TURN)) {
                currentTurn = COMP_TURN;
                performCompTurnMP();
            }
            else{
                currentTurn = YOU_TURN;
            }
        }
        else{
            currentTurn = (currentTurn.equals(Symbols.Cross)) ? Symbols.Circle : Symbols.Cross;

        }
        firstTurn = currentTurn;
        setTurnLabel();

    }

    private void askToResetWithAlertBox(String Message) {
        new MaterialAlertDialogBuilder(TicTacToe.this)
                .setTitle("Result")
                .setMessage(Message)
                .setPositiveButton("Reset", (dialog, which) -> resetBoard())
                .setCancelable(false)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        setScoreCard();
    }

    private void setScoreCard() {
        if(!isSinglePlayer){
            binding.tvScoreCard.setText("ScoreCard:\n"+CROSS +": "+xWins+"\n"+CIRCLE+": "+oWins);
        }
        else {
            binding.tvScoreCard.setText("ScoreCard:\n"+YOU +": "+youWins+"\n"+COMP+": "+compWins);
        }
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


        Log.d("eee", "checkIfWin:xo00 "+xo00);
        Log.d("eee", "checkIfWin:xo01 "+xo01);
        Log.d("eee", "checkIfWin:xo02 "+xo02);
        Log.d("eee", "checkIfWin:xo10 "+xo10);
        Log.d("eee", "checkIfWin:xo11 "+xo11);
        Log.d("eee", "checkIfWin:xo12 "+xo12);
        Log.d("eee", "checkIfWin:xo20 "+xo20);
        Log.d("eee", "checkIfWin:xo21 "+xo21);
        Log.d("eee", "checkIfWin:xo22 "+xo22);

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

    @Override
    public void callbackMethod(Button toBePressed, ArrayList<Button> buttonsToBeTurnedOffOn, Handler handler) {
//        toBePressed.setText(COMP);
        currentTurn = YOU_TURN;
//        setTurnLabel();
        toBePressed.setText(COMP);

        handler.post(() -> {
            setTurnLabel();
        });

        for( Button button : buttonsToBeTurnedOffOn){
            button.setClickable(true);
        }
        buttonsToBeTurnedOffOn.clear();

        if(checkIfWin(COMP)){
            handler.post(() -> {
                compWins++;
//                Toast.makeText(getApplicationContext(),Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                askToResetWithAlertBox("COMP WON");


            });

        }
        else
        if(!movesLeft()){
            handler.post(() -> {
//                Toast.makeText(getApplicationContext(),Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                askToResetWithAlertBox("Draw \nUsers Out of valid moves");
            });
        }

    }

    boolean movesLeft(){
        for (Button button : allButtons){
            if(button.getText().toString().equals("")) return true;
        }
        return false;
    }


}
class ThreadedMoveMaker {
    CallbackInterface callbackInterface;

    public ThreadedMoveMaker(CallbackInterface callbackInterface, Button toBePressed, ArrayList<Button> buttonsToBeTurnedOffOn, Handler handler ) {
        this.callbackInterface = callbackInterface;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}

                Looper.prepare();
                callbackInterface.callbackMethod(toBePressed,  buttonsToBeTurnedOffOn, handler);
            }
        };
        new Thread(runnable).start();
    }
}

interface CallbackInterface{
    void callbackMethod(Button toBePressed, ArrayList<Button> buttonsToBeTurnedOffOn, Handler handler);
}

