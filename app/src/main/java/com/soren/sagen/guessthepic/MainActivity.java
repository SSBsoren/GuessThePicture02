package com.soren.sagen.guessthepic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements  OnClickListener {


    /*SharedPreferences: prefs keep game's data, eg:no. of completed levels, sound on/off, lives,coins*/
    private SharedPreferences prefs;
    private ImageView sounds, fb, reset;
    private Button start, lboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Change com.YOUR_PACKAGE_NAME to your package name*/
        prefs = this.getSharedPreferences("com.YOUR_PACKAGE_NAME", Context.MODE_PRIVATE);


        sounds =  findViewById(R.id.sounds);
        fb =  findViewById(R.id.fb);
        reset =  findViewById(R.id.reset);
        start =  findViewById(R.id.start_btn);
        lboard =  findViewById(R.id.leaders_btn);
        lboard.setOnClickListener(this);
        sounds.setOnClickListener(this);
        fb.setOnClickListener(this);
        reset.setOnClickListener(this);
        start.setOnClickListener(this);

        /*Check if sounds on/off*/
        if (prefs.getInt("sounds", 0) > 0) {
            sounds.setImageResource(R.drawable.sound_on);
        } else {
            sounds.setImageResource(R.drawable.sound_off);
        }
    }

    /*Click listener for all buttons*/
    @Override	public void onClick(View v) {
        playSound();

        switch(v.getId()){

            case R.id.start_btn:
                /*Start game play activity*/
                Intent i2 = new Intent(MainActivity.this,GamePlay.class);
                startActivity(i2);
                break;

            case R.id.leaders_btn:
                /*Start leaderboard activity*/
                startActivity(new Intent(MainActivity.this,GLeaderboard.class));
                break;

            case R.id.fb:
                /*Load FB page on browser. Change s string (YOUR_PAGE_NAME) with your facebook page name*/
                String s = "https://www.facebook.com/pages/YOUR_PAGE_NAME";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                startActivity(browserIntent);
                break;

            case R.id.reset:
                /*Show dialog that ask for reset game*/
                showDialog("Are you sure you want to reset the game?\n");
                break;

            case R.id.sounds:
                /*Enable/Disable sounds*/
                if(prefs.getInt("sounds", 0)>0){
                    prefs.edit().putInt("sounds",0).commit();
                    sounds.setImageResource(R.drawable.sound_off);
                }else{
                    prefs.edit().putInt("sounds",1).commit();
                    sounds.setImageResource(R.drawable.sound_on);
                }
                break;
        }
    }

    /*Function that show reset game Dialog. Here if user press OK all data from prefs are deleted*/
    private void showDialog(String mess){
        final Dialog dialog = new Dialog(MainActivity.this,R.style.MyDialog);
        dialog.setContentView(R.layout.alert_dialog);
        TextView text =  dialog.findViewById(R.id.levelTxt);
        text.setText(mess);
        Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);

        dialogButtonOK.setOnClickListener(new OnClickListener() {
            @Override	public void onClick(View v) {
                prefs.edit().clear().commit();
                prefs.edit().putInt("hearts", 3).commit();
                prefs.edit().putInt("sounds",1).commit();
                finish();
                startActivity(getIntent());
                dialog.dismiss();
            }
        });
        dialogButtonCancel.setOnClickListener(new OnClickListener() {
            @Override	public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    /*Function that play sounds if sounds are enabled on prefs*/
    private void playSound(){
        if(prefs.getInt("sounds", 0)>0){
            MediaPlayer mp = new MediaPlayer();
            mp = MediaPlayer.create(MainActivity.this, R.raw.button_sound);
            mp.setVolume(.5f, .5f);
            mp.setOnCompletionListener(new OnCompletionListener() {
                @Override  public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mp.start();
        }
    }

}