package com.swufe.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    TextView score;
    TextView scoreb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        score =findViewById(R.id.score);
        scoreb =findViewById(R.id.scoreb);
    }
    public void add(View btn) {
        if (btn.getId() == R.id.btn3b) {
            String olds = scoreb.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=3;
                scoreb.setText("" + trues);
            }
        }
        if (btn.getId() == R.id.btn2b) {
            String olds = scoreb.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=2;
                scoreb.setText("" + trues);
            }
        }
        if (btn.getId() == R.id.btn1b) {
            String olds = scoreb.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=1;
                scoreb.setText("" + trues);
            }
        }if (btn.getId() == R.id.btn3) {
            String olds = score.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=3;
                score.setText("" + trues);
            }
        }
        if (btn.getId() == R.id.btn2) {
            String olds = score.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=2;
                score.setText("" + trues);
            }
        }
        if (btn.getId() == R.id.btn1) {
            String olds = score.getText().toString();
            if (olds.length() > 0) {
                int trues = Integer.parseInt(olds);
                trues+=1;
                score.setText("" + trues);
            }
        }
    }

    public  void reset(View btn){
        if(btn.getId()==R.id.btn4){
            score.setText("0");
        }
        if(btn.getId()==R.id.btn4b){
            scoreb.setText("0");
        }
    }
}