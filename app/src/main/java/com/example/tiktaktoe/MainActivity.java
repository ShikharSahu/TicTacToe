package com.example.tiktaktoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tiktaktoe.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public final static String IS_SINGLE_PLAYER_KEY = "com.example.tiktaktoe.isSinglePlayerKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSinglerPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, TicTacToe.class);
                intent.putExtra(IS_SINGLE_PLAYER_KEY,true);
                startActivity(intent);
            }
        });

        binding.btnTwoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, TicTacToe.class);
                intent.putExtra(IS_SINGLE_PLAYER_KEY,false);
                startActivity(intent);
            }
        });

    }
}