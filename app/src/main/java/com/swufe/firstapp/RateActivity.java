package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText inp;
    TextView out;
    float result;
    Handler handler;
    float dollar_rate=0.13f;
    float euro_rate=0.14f;
    float won_rate=173.64f;
    private  String updateDate="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        inp = findViewById(R.id.input);
        out = findViewById(R.id.out);
        //获取sp里保存的数据
        SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollar_rate = sp.getFloat("dollar_rate", 0.0f);
        euro_rate = sp.getFloat("euro_rate", 0.0f);
        won_rate = sp.getFloat("won_rate", 0.0f);
        updateDate=sp.getString("update_date","");
        //获取当前系统时间
        final Date today=Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        Log.i("run","todayStr="+todayStr);
        Log.i("run","updateDate="+updateDate);
        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i("run","需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();

        }
        else{
            Log.i("run","不需要更新");
        }


        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    Bundle bundle = (Bundle) msg.obj;
                    dollar_rate = bundle.getFloat("dollar-rate");
                    euro_rate = bundle.getFloat("euro-rate");
                    won_rate = bundle.getFloat("won-rate");
                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();

                    //保存更新的日期
                    SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollar_rate",dollar_rate);
                    editor.putFloat("euro_rate",euro_rate);
                    editor.putFloat("won_rate",won_rate);
                    editor.commit();
                    }
                super.handleMessage(msg);
            }
        };

    }
    public void onclik(View btn){
        String inp2=inp.getText().toString();
        if(inp2.length()>0){
            float inp3 =Float.parseFloat(inp2);
            if(btn.getId()==R.id.dollar){
                result=dollar_rate*inp3;
            }
            if(btn.getId()==R.id.euro) {
                result=euro_rate*inp3;
            }
            if(btn.getId()==R.id.won){
                result=won_rate*inp3;
            }
        }
        else{
            Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
        }
        out.setText(String.format("%.2f",result));
    }
    public void config(View btn){
        openconfig();
    }

    private void openconfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate", dollar_rate);
        config.putExtra("won_rate", won_rate);
        config.putExtra("euro_rate", euro_rate);
        startActivityForResult(config, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&&resultCode==2){
            Bundle bundle = data.getExtras();
            dollar_rate=bundle.getFloat("ndr");
            won_rate=bundle.getFloat("nwr");
            euro_rate=bundle.getFloat("ner");

            //将新设置的汇率写进SP里
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("dollar_rate",dollar_rate);
            editor.putFloat("euro_rate",euro_rate);
            editor.putFloat("won_rate",won_rate);
            editor.commit();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openconfig();
        }
        else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this, RateListActivity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }
    public void run(){

        //用于保存获取的汇率
        Bundle bundle = get_webdata();
        //获取msg对象，用于返回主线程
        Message msg =handler.obtainMessage(5);
        msg.obj=bundle;
        handler.sendMessage(msg);
    }

    private Bundle get_webdata() {
        Bundle bundle =new Bundle();
        Document doc = null;
        try {

            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Elements tables = doc.getElementsByTag("table");
            Element table1 = tables.get(0);
            //获取TD中的元素
            Elements tds =table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                if(td1.text().equals("美元")){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(td2.text()));
                }
                else if(td1.text().equals("欧元")){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(td2.text()));
                }
                else if(td1.text().equals("韩元")){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(td2.text()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    public static String inputStream2String (InputStream inputStream) throws IOException   {
        final int buffferSize=1024;
        final char[] buffer = new char[buffferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(; ;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0){
                break;
            }
            out.append(buffer,0,rsz);
        }
        return out.toString();


    }

}

