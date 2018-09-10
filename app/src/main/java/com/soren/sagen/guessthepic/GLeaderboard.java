package com.soren.sagen.guessthepic;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




public class GLeaderboard extends AppCompatActivity {
    private SharedPreferences prefs;
    private TextView mess;
    private int coins;
    private Button show_leaders;
    private Button share_score;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);
        prefs = this.getSharedPreferences("com.YOUR_PACKAGE_NAME", Context.MODE_PRIVATE);
        coins = prefs.getInt("coins", 0);
        share_score = findViewById(R.id.share_score);
        show_leaders = findViewById(R.id.view_leaders);



        mess = findViewById(R.id.leader_txt);
        mess.setText("You have earned: "+coins+" coins!");
        share_score.setText("Share Results");
        show_leaders.setText("Top Players");
    }


}