package com.example.mymoviepartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private TextView tv1,tv2;
    private ImageView iv1;
    private ImageView iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        iv1 = (ImageView) findViewById(R.id.image1);
        iv2 = (ImageView) findViewById(R.id.image2);
        tv1 = (TextView) findViewById(R.id.text1);
        tv2 = (TextView) findViewById(R.id.text2);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        tv1.startAnimation(animation);
        tv2.startAnimation(animation);
        iv1.startAnimation(animation);
        iv2.startAnimation(animation);

        final Intent intent = new Intent(getApplicationContext(),FirstScreen.class);

        Thread  timer = new Thread(){
            public void run(){
                try{
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }

            }
        };
        timer.start();
    }
}
