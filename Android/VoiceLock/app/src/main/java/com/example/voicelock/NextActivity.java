package com.example.voicelock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NextActivity extends AppCompatActivity {


    private BTService _btService1, _btService2;
    private Button btn, btn2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
//        _btService1=MainActivity._btService1;
//        _btService2=MainActivity._btService1;
//        btn=(Button)findViewById(R.id.test);
//        btn.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view){
//                _btService1.deleteMessage(_btService1.getSocket());
//            }
//        });
//        btn2=(Button)findViewById(R.id.test2);
//        btn2.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View view){
//                _btService2.deleteMessage(_btService2.getSocket());
//            }
//        });
    }
}
