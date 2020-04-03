package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    EditText dr;
    EditText wr;
    EditText er;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();
        float dollar2 =intent.getFloatExtra("dollar_rate",0.0f);
        float won2 =intent.getFloatExtra("won_rate",0.0f);
        float euro2 =intent.getFloatExtra("euro_rate",0.0f);
        dr=findViewById(R.id.dollar_cfg);
        wr=findViewById(R.id.won_cfg);
        er=findViewById(R.id.euro_cfg);
        dr.setText(""+dollar2);
        er.setText(""+euro2);
        wr.setText(""+won2);
    }

    public void conversion(View btn){
        float ndr =Float.parseFloat(dr.getText().toString());
        float nwr =Float.parseFloat(wr.getText().toString());
        float ner =Float.parseFloat(er.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putFloat("ndr",ndr);
        bundle.putFloat("ner",ner);
        bundle.putFloat("nwr",nwr);
        Intent intent = getIntent();
        intent.putExtras(bundle);
        setResult(2,intent);
        finish();
    }
}
