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
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText inp;
    TextView out;
    float result;
    Handler handler;
    float dollar_rate=0.13f;
    float euro_rate=0.14f;
    float won_rate=173.64f;
    private  int date;  //用于存储日期，当日期发生改变时，值也会改变
    SharedPreferences sp2;    //用于从SharedPreferences中取出date日期
    SharedPreferences share;  //用于存储date
    SharedPreferences.Editor editor;//share的editor对象
    int truedate;
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
        sp2 = getSharedPreferences("mydate", Activity.MODE_PRIVATE);//实例化
        share= super.getSharedPreferences("mydate", MODE_PRIVATE);//实例化
        editor= share.edit();    //使处于可编辑状态
        //开启子线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                date=sp2.getInt("date",0);   //取出SharedPreferences中的日期
                if (msg.what == 5) {
                    Bundle bundle = (Bundle) msg.obj;
                    /*
                    两个日期进行比较也就是date和truedate，如果相同，则代表是同一天
                    那么数据不用更新，直接return;
                    如果不同，则代表不是同一天，那么更新数据，然后让date=truedate,
                    至此完成一天的更新，直到第二天打开app获取truedate发现日期不匹配，
                    再进行数据更新
                     */
                    if(date==bundle.getInt("date")){
                        return;
                    }
                    else{
                        date=bundle.getInt("date");
                        Log.i("run","不相等"+date);
                        dollar_rate = bundle.getFloat("dollar-rate");
                        euro_rate = bundle.getFloat("euro-rate");
                        won_rate = bundle.getFloat("won-rate");
                        Log.i("run","数据更新了");

                        editor.putInt("date", date);
                        editor.commit();
                        Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                    }

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
        return super.onOptionsItemSelected(item);
    }
    public void run(){


        //获取网络数据
//        try {
//              URL url = new URL("http://www.usd-cny.com/");
//
//            HttpURLConnection http =(HttpURLConnection)url.openConnection();
//            InputStream inp = http.getInputStream();
//
//            String html=inputStream2String(inp);
//            Log.i("run","htmls "+html);
//            Document doc= Jsoup.parse(html);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i("run",e+"");
//        }
        //用于保存获取的汇率
        Bundle bundle = new Bundle();
        Document doc = null;
        /*
            获取时间，每次执行run方法都会获取时间，用于之后比较
         */
        SimpleDateFormat formatter= new SimpleDateFormat("dd 'at' HH:mm:ss z");
        Date date2 = new Date(System.currentTimeMillis());
        String time =date2.toString();
        truedate =Integer.parseInt(time.substring(8,10).trim());  //从时间中取出日期
        Log.i("run","truedate="+truedate);
        date=sp2.getInt("date",0); //取出SharedPreferences中的date
        /*
        理论上这个if条件只会执行一次，就是代码修改后的第一次editor还没有还没有向SP中放数据的时候
         */
        if(date==0) {     //向SP中存放date数据，其值为0，意味着第一次一定会更新数据
            editor.putInt("date", truedate);
            editor.commit();
        }

        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();

            Elements tables = doc.getElementsByTag("table");
            Element  table1 = tables.get(0);
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
            //获取msg对象，用于返回主线程
            bundle.putInt("date",truedate);   //将日期放在bundle中
            Message msg =handler.obtainMessage(5);
            msg.obj=bundle;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

