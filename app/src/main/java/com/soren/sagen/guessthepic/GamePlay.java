package com.soren.sagen.guessthepic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public  class GamePlay extends AppCompatActivity  {

    private SharedPreferences prefs; //SharedPreferences for keeping/geting user's data
    private ImageView[] blocks;		//Image array for all 36 blocks
    private TextView targetCoins;	//TextView that showing remained no. of coins
    private Integer[] randInt;		//random Integer array for blocks position
    private LinearLayout result;	//Layout that apears as a message if answer are/not correct
    private Button loadLevel;		//Button from result layout
    private TextView resultTxt;		//Text from resutl layout, showing small message as result
    private TextView resultCoins;	//Text from result layout, showing earned coins on level
    private int totalCoins;			//Number of coins acumulated by user
    private int correctAnswer;		//Id for correct button, first number from levels.lvl
    public boolean correct=false;	//Correct or not
    private ImageView resultImg;	//Image from result layout, coins or smile
    public int clvl=1;				//Curent level
    private TextView lvlTxt;		//Text that showing current level on top bar(centet)
    private LinearLayout myPic;		//Layout with our picture
    private TextView coins;			//Text showing acumulated coins
    private String[] levels;  		//String array that keept all levels from assets/levels.lvl
    private Button[] answers;		//4 buttons as answers
    private int hearts;				//No. of hearts/lives
    private TextView lives;			//Text showing no. of lives


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        prefs = this.getSharedPreferences("com.YOUR_PACKAGE_NAME", Context.MODE_PRIVATE);

        /*Get screen width for scaling main image and blocks*/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x-85;

        myPic = findViewById(R.id.myPic);
        LayoutParams params = myPic.getLayoutParams();
        params.height = width;
        params.width = width;

        blocks =new ImageView[] {findViewById(R.id.i0),findViewById(R.id.i1),findViewById(R.id.i2),findViewById(R.id.i3),findViewById(R.id.i4),findViewById(R.id.i5),
                findViewById(R.id.i6),findViewById(R.id.i7),findViewById(R.id.i8),findViewById(R.id.i9),findViewById(R.id.i10),findViewById(R.id.i11),
                findViewById(R.id.i12),findViewById(R.id.i13),findViewById(R.id.i14),findViewById(R.id.i15),findViewById(R.id.i16),findViewById(R.id.i17),
                findViewById(R.id.i18),findViewById(R.id.i19),findViewById(R.id.i20),findViewById(R.id.i21),findViewById(R.id.i22),findViewById(R.id.i23),
                findViewById(R.id.i24),findViewById(R.id.i25),findViewById(R.id.i26),findViewById(R.id.i27),findViewById(R.id.i28),findViewById(R.id.i29),
                findViewById(R.id.i30),findViewById(R.id.i31),findViewById(R.id.i32),findViewById(R.id.i33),findViewById(R.id.i34),findViewById(R.id.i35)};

        answers = new Button[]{findViewById(R.id.answer1),findViewById(R.id.answer2),
                findViewById(R.id.answer3),findViewById(R.id.answer4)};

        lvlTxt = findViewById(R.id.levelTxt);
        loadLevel = findViewById(R.id.loadLevelBtn);
        result = findViewById(R.id.resultLayout);
        resultTxt = findViewById(R.id.resultTxt);
        resultCoins = findViewById(R.id.resultCoins);
        resultImg = findViewById(R.id.resultImg);
        loadLevel.setOnClickListener(btnsClick);
        targetCoins = findViewById(R.id.cronoTxt);
        coins = findViewById(R.id.coinsTxt);
        lives = findViewById(R.id.lives);

        totalCoins = prefs.getInt("coins", 0);
        clvl = prefs.getInt("level", 1);
        hearts = prefs.getInt("hearts", 0);




    }

    /*Function that load new level and reseting blocks, words*/
    public void loadLevel(int lvl){
        correctAnswer = Integer.parseInt(levels[clvl-1].split(",")[0]);
        lives.setText(""+prefs.getInt("hearts", 0));
        resultCoins.setVisibility(View.VISIBLE);

        /*Set words for buttons*/
        for(int b = 0; b<4; b++){
            answers[b].setOnClickListener(btnsClick);
            answers[b].setId(b+1);
            answers[b].setText(levels[clvl-1].split(",")[b+1].toUpperCase());
        }

        coins.setText(""+totalCoins);
        lvlTxt.setText("Level: "+clvl);
        randInt = new Integer[36];
        for (int i = 0; i < randInt.length; i++) {randInt[i] = i;}
        Collections.shuffle(Arrays.asList(randInt));
        targetCoins.setText("30");
        for(int y=0; y<36; y++){
            blocks[y].setOnClickListener(blockClick);
            blocks[y].setId(y);
            blocks[y].setVisibility(View.VISIBLE);
            blocks[y].setImageResource(R.drawable.block);
            blocks[y].setAlpha(1.0f);
        }

        myPic.setBackgroundResource(this.getResources().getIdentifier("drawable/l"+clvl, null, this.getPackageName()));
        /*Hide 3 random blocks and show heart,bomb,transparent randomly*/
        blocks[randInt[0]].setVisibility(View.INVISIBLE);
        blocks[randInt[1]].setVisibility(View.INVISIBLE);
        blocks[randInt[2]].setVisibility(View.INVISIBLE);
        if(totalCoins%2==0){blocks[randInt[3]].setImageResource(R.drawable.bomb);}
        if(totalCoins%3==0){blocks[randInt[4]].setAlpha(.8f);}
        if(totalCoins%5==0){blocks[randInt[5]].setImageResource(R.drawable.liveplus);}
    }


    /*Answers buttons click listener*/
    View.OnClickListener btnsClick = new OnClickListener(){
        @SuppressLint("ResourceType")
        @Override	public void onClick(View btn) {
            playSound(R.raw.button_sound);

            if(Integer.parseInt((String)targetCoins.getText())==0){
                clvl = clvl+1;
            }

            if(btn.getId()<=4){
                if(btn.getId()==correctAnswer){
                    correct=true;
                    clvl = clvl+1;
                    showResult();
                }else{
                    correct=false;
                    if(hearts>0){hearts-=1; lives.setText(""+hearts);}
                    showResult();
                }
            }

            if(btn.getId()==R.id.loadLevelBtn&&correct){
                totalCoins+=Integer.parseInt((String) targetCoins.getText());
                hideResult();
                /*Check if is last level*/
                if(clvl<21){
                    loadLevel(clvl+1);
                }else{
                    showResult();
                }
                correct = false;
            }

            if(btn.getId()==R.id.loadLevelBtn&&!correct){
                loadLevel(clvl);
                hideResult();
            }

            if(btn.getId()==R.id.loadLevelBtn&&hearts==0){
                hideResult();
                hearts=3;
                lives.setText(""+hearts);
                clvl = clvl+1;
                loadLevel(clvl);

            }
            saveData();
        }
    };

    /*Blocks click listener. Hide clicked blocks*/
    View.OnClickListener blockClick = new OnClickListener(){
        @Override	public void onClick(View block) {
            playSound(R.raw.block_sound);
            if(Integer.parseInt((String) targetCoins.getText())==3){
                if(hearts>0){
                    hearts-=1; lives.setText(""+prefs.getInt("hearts", hearts));
                }

                for(int i = 0; i<36; i++){
                    blocks[i].setVisibility(View.INVISIBLE);
                }

                targetCoins.setText(Integer.parseInt((String) targetCoins.getText())-3+"");
                showResult();
            } else{
                if(block.getId()==randInt[3]&&totalCoins%2==0){explode(block.getId());}
                if(block.getId()==randInt[4]&&totalCoins%3==0){transparent();}
                if(block.getId()==randInt[5]&&totalCoins%5==0){hearts+=1; lives.setText(""+hearts);saveData();}
                targetCoins.setText(Integer.parseInt((String) targetCoins.getText())-3+"");
                block.setVisibility(View.INVISIBLE);
            }}};

    /*Function that show result: wrong or correct answer */
    private void showResult(){
        for(int b = 0; b<4; b++){
            answers[b].setOnClickListener(null);
        }

        int resultT = Integer.parseInt((String)targetCoins.getText());
        resultCoins.setText(" +"+resultT);

        if(correct){
            if(clvl<=21){
                playSound(R.raw.good);
                for(int i = 0; i<36; i++){blocks[i].setVisibility(View.INVISIBLE);}
                resultImg.setImageResource(R.drawable.coin_earn);
                if(resultT>20){resultTxt.setText("E X C E L L E N T !");}
                if(resultT>10&&resultT<20){resultTxt.setText("G O O D !");}
                if(resultT<10&&resultT>0){resultTxt.setText("N O    G O O D !");}
                loadLevel.setText("CONTINUE");
            }else{
                resultTxt.setText("CONGRATULATIONS!\nYou finished the game. Come back later for new levels!");
                resultCoins.setVisibility(View.GONE);
                resultImg.setImageResource(R.drawable.coupe);
                loadLevel.setVisibility(View.GONE);
            }
        } else {
            playSound(R.raw.bad);
            resultImg.setImageResource(R.drawable.smile);
            resultCoins.setText(" ");
            loadLevel.setText("TRY AGAIN");
            resultTxt.setText("W R O N G !");
        }

        if(resultT==0){
            loadLevel.setText("CONTINUE");
            resultTxt.setText("D O N' T   W O R R Y !\nCorrect is: "+answers[correctAnswer-1].getText());
        }

        if(hearts==0){
            for(int i = 0; i<36; i++){blocks[i].setVisibility(View.INVISIBLE);}
            resultCoins.setVisibility(View.GONE);
            resultImg.setImageResource(R.drawable.smile);
            loadLevel.setText("CONTINUE");
            resultTxt.setText("Correct is: "+answers[correctAnswer-1].getText());
        }
        /*Animation for result Layer*/
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        animation.setAnimationListener(new AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                result.setVisibility(View.VISIBLE);
            }});
        result.startAnimation(animation);
    }

    /*Function that hide result layer*/
    private void hideResult(){
        correct = false;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        animation.setAnimationListener(new AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                result.setVisibility(View.INVISIBLE);
            }});
        result.startAnimation(animation);
    }

    /*Function that save data on prefs*/
    private void saveData(){
        prefs.edit().putInt("coins", totalCoins).commit();
        prefs.edit().putInt("level", clvl).commit();
        prefs.edit().putInt("hearts", hearts).commit();
        prefs.edit().putLong("lastTime",(new Date()).getTime()).commit();
    }

   /* *//*Function that read data from assets/levels.lvl*//*
    private String getData(String file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br=null;
        try {br = new BufferedReader(new InputStreamReader(getAssets().open(file)));
            String temp;  while ((temp = br.readLine()) != null)sb.append(temp);
        } catch (IOException e) { e.printStackTrace();} finally { try {
            assert br != null;
            br.close(); } catch (IOException e) {e.printStackTrace();}}
        return sb.toString();
    }*/

    /*Function that hide blocks if block is a bomb*/
    private void explode(int x){
        playSound(R.raw.bomb_sound);
        Integer[] cArray = new Integer[]{7,8,9,10,13,14,15,16,19,20,21,22,25,26,27,28};
        Integer[] lArray = new Integer[]{6,12,18,24};
        Integer[] rArray = new Integer[]{11,17,23,29};
        if(x==0){blocks[x+1].setVisibility(View.INVISIBLE);blocks[x+6].setVisibility(View.INVISIBLE);}
        if(x==5){blocks[x-1].setVisibility(View.INVISIBLE);blocks[x+6].setVisibility(View.INVISIBLE);}
        if(x==30){blocks[x+1].setVisibility(View.INVISIBLE);blocks[x-6].setVisibility(View.INVISIBLE);}
        if(x==35){blocks[x-1].setVisibility(View.INVISIBLE);blocks[x-6].setVisibility(View.INVISIBLE);}
        if(x<5 && x>0){blocks[x-1].setVisibility(View.INVISIBLE);blocks[x+1].setVisibility(View.INVISIBLE);blocks[x+6].setVisibility(View.INVISIBLE);}
        if(x<35 && x>30){blocks[x-1].setVisibility(View.INVISIBLE);blocks[x+1].setVisibility(View.INVISIBLE);blocks[x-6].setVisibility(View.INVISIBLE);}
        if(Arrays.asList(cArray).contains(x)){
            blocks[x-1].setVisibility(View.INVISIBLE);
            blocks[x+1].setVisibility(View.INVISIBLE);
            blocks[x-6].setVisibility(View.INVISIBLE);
            blocks[x+6].setVisibility(View.INVISIBLE);
        }
        if(Arrays.asList(lArray).contains(x)){
            blocks[x+1].setVisibility(View.INVISIBLE);
            blocks[x-6].setVisibility(View.INVISIBLE);
            blocks[x+6].setVisibility(View.INVISIBLE);
        }
        if(Arrays.asList(rArray).contains(x)){
            blocks[x-1].setVisibility(View.INVISIBLE);
            blocks[x-6].setVisibility(View.INVISIBLE);
            blocks[x+6].setVisibility(View.INVISIBLE);
        }
    }

    private void playSound(int Rid){
        if(prefs.getInt("sounds", 0)>0){
            MediaPlayer mp = new MediaPlayer();
            mp = MediaPlayer.create(GamePlay.this, Rid);
            mp.setOnCompletionListener(new OnCompletionListener() {
                @Override  public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mp.start();
        }
    }

    /*Function that make all blocks semitransparent for 1 second*/
    private void transparent(){
        new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {
                for(int z=0;z<36;z++){blocks[z].setAlpha(.9f);}
            }
            public void onFinish() {for(int z=0;z<36;z++){blocks[z].setAlpha(1.0f);}}}.start();
    }



}