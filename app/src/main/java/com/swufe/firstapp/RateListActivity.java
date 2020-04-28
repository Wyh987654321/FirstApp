package com.swufe.firstapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data [] = new String[]{"one","two","three"};
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_rate_list);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==5){
                    List<String> list2 =(List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);

                }
            }
        };
        //子线程开启
        Thread t = new Thread(this);
        t.start();
    }

    public void run(){
        //获取网络数据，放入List
        List<String> retList =get_webdata();
        //获取msg对象，用于返回主线程
        Message msg =handler.obtainMessage(5);
        msg.obj=retList;
        handler.sendMessage(msg);

    }

    private List<String> get_webdata() {
        List<String> retList = new ArrayList<String>();
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
                String country =td1.text();
                String value =td2.text();
                retList.add(country+"==>"+value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
       return retList;
    }

}
