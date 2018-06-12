package com.example.voicelock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OneLockActivity extends AppCompatActivity {
    private TextView expTv, wordTv, unConnect;
    private ImageView lockimg, unlockimg;
    private LinearLayout lockLayout, unLockLayout, connectLayout;
    private Button openBtn;
    RecyclerItem mItem;

    public String name, position, address,ble;
    public int isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_lock);
        Intent intent=getIntent();
        name = intent.getExtras().getString("NAME");
        position=intent.getExtras().getString("POSITION");
        int isOpen=intent.getExtras().getInt("ISOPEN");
        address=intent.getExtras().getString("ADDRESS");
        ble=intent.getExtras().getString("BLUETOOTH");
        openBtn=(Button)findViewById(R.id.openButton1);
        expTv=(TextView)findViewById(R.id.explainTxt);
        wordTv=(TextView)findViewById(R.id.wordTxt);
        lockLayout=(LinearLayout)findViewById(R.id.lockTxt);
        unLockLayout=(LinearLayout)findViewById(R.id.unlockTxt);
        unConnect=(TextView)findViewById(R.id.unconnectTxt);
        connectLayout=(LinearLayout)findViewById(R.id.connectTxt);

        expTv.setText(position);
        wordTv.setText(name);
        if(isOpen==0){
            lockLayout.setVisibility(View.VISIBLE);
            unLockLayout.setVisibility(View.GONE);
            if(ble!=null){
            if(!ble.equals(address)){
                unConnect.setVisibility(View.VISIBLE);
                connectLayout.setVisibility(View.GONE);
            }
            else{
                unConnect.setVisibility(View.GONE);
                connectLayout.setVisibility(View.VISIBLE);
            }
            }else{
                unConnect.setVisibility(View.VISIBLE);
                connectLayout.setVisibility(View.GONE);
            }

        }else if(isOpen==1){
            lockLayout.setVisibility(View.GONE);
            unLockLayout.setVisibility(View.VISIBLE);
            if(ble!=null) {
                if (!ble.equals(address)) {
                    unConnect.setVisibility(View.VISIBLE);
                    connectLayout.setVisibility(View.GONE);
                } else {
                    unConnect.setVisibility(View.GONE);
                    connectLayout.setVisibility(View.VISIBLE);
                }
            }else{   unConnect.setVisibility(View.VISIBLE);
                connectLayout.setVisibility(View.GONE);
            }

        }
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bintent = new Intent(OneLockActivity.this, AddActivity.class);
                bintent.putExtra("mNAME", name);
                bintent.putExtra("mPOSITION", position);
                bintent.putExtra("ADDRESS", address);
                startActivity(bintent);
            }
        });
    }
}

