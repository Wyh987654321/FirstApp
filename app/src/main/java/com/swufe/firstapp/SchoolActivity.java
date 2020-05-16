package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.lang.StrictMath.abs;

public class SchoolActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener{
    EditText inp;
    Button select;
    Handler handler;
    ListView listView;
    private  String updateDate="";
    String Singledate="-1";    //表示个位数日期，及1-9号
    private ArrayList<HashMap<String,String>> listItem;  //存放文字，图片信息
    ArrayList<HashMap<String,String>> list2;
    private SimpleAdapter listItemAdapter;    //适配器
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        inp=findViewById(R.id.inp3);
        select=findViewById(R.id.btn5);
        listView=findViewById(R.id.school_list);
        TextView view =findViewById(R.id.school_view);
        view.setText("西南财经大学经济信息工程学院通知公告");
        SharedPreferences sp = getSharedPreferences("mytitle", Activity.MODE_PRIVATE);
        initListItem();
        //获取当前系统时间
        final Date today= Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        final String date =todayStr.substring(8,10); //从年月日中取出日
        if(date.substring(0,1).equals("0")){    //如果日期时0开头，则说明日期时各位数
            Singledate =date.substring(1,2);    //将日期变为个位数并赋给Singledate
            Log.i("school","Singledate="+Singledate);
        }
        updateDate=String.valueOf(sp.getInt("school_update",0));//从SP中获取上一次的更新日期，如果没有这位0

        if(updateDate.equals("0")){    //针对更新日期为0的情况，只出现一次
            Log.i("school","需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else if(!Singledate.equals("-1")){  //Singledate初值时-1，如果日期是个位数，此时就满足条件
            is_update(Singledate);          //调用is_update方法判断是否更新
        }
        else if(Singledate.equals("-1")){ //说明日期时两位数
            is_update(date);            //调用is_update方法判断是否更新
        }
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==5){
                    list2 =(ArrayList<HashMap<String,String>>)msg.obj;
                    listItemAdapter = new SimpleAdapter(SchoolActivity.this,list2,
                            R.layout.schoole_list_item,
                            new String []{"ItemTitle","ItemURL"},
                            new int[]{R.id.itemTitle2,R.id.school_url});
                    Toast.makeText(SchoolActivity.this, "新闻已更新", Toast.LENGTH_SHORT).show();
                    SharedPreferences sp = getSharedPreferences("mytitle", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    if(!Singledate.equals("-1")){
                        editor.putInt("school_update",Integer.valueOf(Singledate));
                        Log.i("school","放入Singledate="+Integer.valueOf(Singledate));
                    }else{
                        editor.putInt("school_update",Integer.valueOf(date));
                        Log.i("school","放入date="+Integer.valueOf(date));
                    }
                    editor.commit();
                    listView.setAdapter(listItemAdapter);
                }
            }
        };
        listView.setOnItemClickListener(this);
    }

    private void is_update(String date) {
        int dateInt =Integer.parseInt(date);    //变为整数
        int updateDateInt =Integer.parseInt(updateDate);
        Log.i("school","date="+date);
        Log.i("school","dateInt="+dateInt);
        Log.i("school","updateDateInt="+updateDateInt);
        if(abs(dateInt-updateDateInt)>=7){    //做差判断绝对值，因为又20+号和几号的区别
            Log.i("school","需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i("school","不需要更新");
            Log.i("school","时间差为"+abs(dateInt-updateDateInt)+"天");
        }
    }

    public void onclik(View btn){
        if(btn.getId()==R.id.btn5){
            SharedPreferences sp = getSharedPreferences("mytitle", Activity.MODE_PRIVATE);
            listItem = new ArrayList<HashMap<String,String>>();
            String key =inp.getText().toString();
            int count=0;
            int i=1;
            while(!sp.getString("新闻标题" + i, "没找到").equals("没找到")){
                String title = sp.getString("新闻标题" + i, "没找到");
                if(title.contains(key)){
                   String url = sp.getString("新闻网址"+i,"没找到");
                   HashMap<String,String> map = new HashMap<String,String>();
                   map.put("ItemTitle",""+title);            //标题
                   map.put("ItemURL"," "+url);
                   listItem.add(map);
                   count++;
               }
                i++;
            }
            if(count==0){
                Toast.makeText(SchoolActivity.this, "没有类似的新闻", Toast.LENGTH_SHORT).show();
            }else{
                listItemAdapter = new SimpleAdapter(this,listItem,  //数据源
                        R.layout.schoole_list_item,  //数据的布局xml实现
                        new String []{"ItemTitle","ItemURL"},   //数据项里的key
                        new int []{R.id.itemTitle2,R.id.school_url}); //布局文件里控件的ID
                listView.setAdapter(listItemAdapter);
            }
        }
    }
    public  void run(){
        //获取网络数据，放入List
        ArrayList<HashMap<String,String>> retList =get_webdata();
        //获取msg对象，用于返回主线程
        Message msg =handler.obtainMessage(5);
        msg.obj=retList;
        handler.sendMessage(msg);
        Log.i("school","线程启动");
    }
    private ArrayList<HashMap<String,String>> get_webdata() {
        ArrayList<HashMap<String,String>> retList = new ArrayList<HashMap<String,String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Elements hrefs =doc.getElementsByAttribute("href");
            Log.i("school","hrefs.size()="+hrefs.size());
            SharedPreferences sp = getSharedPreferences("mytitle", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            for(int i=77;i<hrefs.size();i++){
                HashMap<String,String> map = new HashMap<String,String>();
                Element href =hrefs.get(i);
                String url =href.attr("href");
                String title =href.attr("title");
                    editor.putString("新闻标题"+(i-76),title);
                    editor.putString("新闻网址"+(i-76),url);
                    map.put("ItemTitle",""+title);            //标题
                    //map.put("ItemDetail"," "+value);         //内容
                    map.put("ItemURL"," "+url);
                    retList.add(map);
                }
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retList;
    }

    private void initListItem(){
        SharedPreferences sp = getSharedPreferences("mytitle", Activity.MODE_PRIVATE);
        listItem = new ArrayList<HashMap<String,String>>();
        int i=1;
        while(!sp.getString("新闻标题" + i, "没找到").equals("没找到")){
            String title = sp.getString("新闻标题" + i, "没找到");
            String url = sp.getString("新闻网址" + i, "没找到");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "" + title);            //标题
            map.put("ItemURL", " " + url);
            listItem.add(map);
            i++;
        }
        listItemAdapter = new SimpleAdapter(this,listItem,  //数据源
                R.layout.schoole_list_item,  //数据的布局xml实现
                new String []{"ItemTitle","ItemURL"},   //数据项里的key
                new int []{R.id.itemTitle2,R.id.school_url}); //布局文件里控件的ID
        listView.setAdapter(listItemAdapter);
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String url_first="https://it.swufe.edu.cn";   //网页前缀，因为从网页中读取到的url值是相对网址
        TextView item_url =view.findViewById(R.id.school_url);
        String url =item_url.getText().toString();
        Log.i("school","url="+url);
        String trueurl =url_first+url.substring(3,url.length());//前缀+后缀，拼凑出正确的网址
        Uri uri = Uri.parse(trueurl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);//打开网页

    }
}
