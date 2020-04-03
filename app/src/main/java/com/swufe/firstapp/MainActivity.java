package com.swufe.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
     EditText inp;
     TextView out;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inp=(EditText)findViewById(R.id.inp);
        out=findViewById(R.id.out);
    }
    public void clik(View x){
        String oldtem = inp.getText().toString();

        if(oldtem!=null){
                out.setText(gettem(oldtem));
        }
    }
    //获取温度
    public String gettem(String str){
        boolean  isC=(str.contains("c")||str.contains("C"));
        boolean  isF=(str.contains("f")||str.contains("F"));
        int result;
        String res;
        int Truetem;
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String tem =m.replaceAll("").trim();
        Truetem=Integer.parseInt(tem);
        if(isC){
            result=Truetem*9/5+32;
            res =Integer.toString(result)+"F";
        }
        else if(isF){
            result =(Truetem-32)*5/9;
            res =Integer.toString(result)+"C";
        }
        else{
            out.setText("请输入单位C/c或F/f");
            return null;
        }
        return res;

    }
        //忘记加标签了

}
