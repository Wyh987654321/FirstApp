package com.swufe.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    Handler handler;
    private ArrayList<HashMap<String,String>> listItem;  //存放文字，图片信息
    private SimpleAdapter listItemAdapter;    //适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initListItem();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==5){
                    listItem=(ArrayList<HashMap<String,String>>)msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this,listItem,
                            R.layout.list_item,
                            new String []{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail});
                   setListAdapter(listItemAdapter);

                }
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        //子线程开启
        Thread t = new Thread(this);
        t.start();
    }

    private void initListItem(){
        listItem = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("ItemTitle"," "+i);            //标题
            map.put("ItemDetail"," "+i);         //内容
            listItem.add(map);                        //将map放进列表
        }
        //生成适配器，利用数组联系数据和布局
        listItemAdapter = new SimpleAdapter(this,listItem,  //数据源
        R.layout.list_item,  //数据的布局xml实现
        new String []{"ItemTitle","ItemDetail"},   //数据项里的key
        new int []{R.id.itemTitle,R.id.itemDetail}); //布局文件里控件的ID
    }

    public void run(){
        //获取网络数据，放入List
        ArrayList<HashMap<String,String>> retList =get_webdata();
        //获取msg对象，用于返回主线程
        Message msg =handler.obtainMessage(5);
        msg.obj=retList;
        handler.sendMessage(msg);
    }

    private ArrayList<HashMap<String,String>> get_webdata() {
        ArrayList<HashMap<String,String>> retList = new ArrayList<HashMap<String,String>>();
        Document doc = null;
        try {

            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Elements tables = doc.getElementsByTag("table");
            Element table1 = tables.get(0);
            //获取TD中的元素
            Elements tds =table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                HashMap<String,String> map = new HashMap<String,String>();
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                String country =td1.text();
                String value =td2.text();
                map.put("ItemTitle"," "+country);            //标题
                map.put("ItemDetail"," "+value);         //内容
                retList.add(map);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return retList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //获取控件
        TextView title =view.findViewById(R.id.itemTitle);
        TextView detail =view.findViewById(R.id.itemDetail);
        //获取控件内容
        String currency =title.getText().toString();
        String rate =detail.getText().toString();

        //打开页面RateSelect,传入参数
        Intent rateSel = new Intent(this,RateSelecteActivity.class);
        rateSel.putExtra("currency",currency);
        rateSel.putExtra("rate",Float.parseFloat(rate));
        startActivity(rateSel);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.i("run","我长按了");
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listItem.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("否",null);
        builder.create().show();
        return true;
    }
}
