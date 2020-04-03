package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {

    EditText inp;
    TextView out;
    float result;
    float dollar_rate=0.13f;
    float euro_rate=0.14f;
    float won_rate=173.64f;
    String TAG="RateActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        inp=findViewById(R.id.input);
        out=findViewById(R.id.out);
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
        Log.i(TAG, "config:dollar_rate=" + dollar_rate);
        Log.i(TAG, "config:won_rate=" + won_rate);
        Log.i(TAG, "config:euro_rate=" + euro_rate);
        startActivityForResult(config, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&&resultCode==2){
            Bundle bundle = data.getExtras();
            dollar_rate=bundle.getFloat("ndr");
            won_rate=bundle.getFloat("nwr");
            euro_rate=bundle.getFloat("ner");

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
}
