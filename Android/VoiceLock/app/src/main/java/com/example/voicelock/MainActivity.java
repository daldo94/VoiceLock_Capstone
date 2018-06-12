package com.example.voicelock;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button signBtn, loginBtn;
    EditText idText, emailText;

    //bt

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 200;
    private BluetoothAdapter mBTAdapter;
   // static BTService _btService1, _btService2;
    //

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static String address = "00:00:00:00:00:00";
    private String address1 = "98:D3:61:F9:3E:75";
    private String address2 = "98:D3:31:F7:37:38";

    String sql;
    Cursor cursor;
    int version = 1;
    DatabaseOpenHelper helper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signBtn = (Button) findViewById(R.id.signUpButton);
        loginBtn = (Button) findViewById(R.id.loginButton);
        idText = (EditText) findViewById(R.id.idEditText);
        emailText = (EditText) findViewById(R.id.emailEnrollText);
        helper = new DatabaseOpenHelper(MainActivity.this, DatabaseOpenHelper.tableName, null, version);
        database = helper.getWritableDatabase();
        ContextUtil.CONTEXT = getApplicationContext();


        idText.setBackgroundResource(R.drawable.border);
        emailText.setBackgroundResource(R.drawable.border);
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //다음 전환
                Intent intent =
                        new Intent(MainActivity.this, enrollActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = idText.getText().toString();
                String email = emailText.getText().toString();
                //Intent intent = new Intent(MainActivity.this, lockActivity.class);
                //startActivity(intent);


                if (id.length() == 0 || email.length() == 0) {
                    Toast toast = Toast.makeText(MainActivity.this, "아이디와 이메일은 필수 입력사항입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                sql = "SELECT id FROM " + helper.tableName + " WHERE id = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                if (cursor.getCount() != 1) {
                    //아이디가 틀렸습니다.
                    Toast toast = Toast.makeText(MainActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                sql = "SELECT email FROM " + helper.tableName + " WHERE id = '" + id + "'";
                cursor = database.rawQuery(sql, null);

                cursor.moveToNext();
                if (!email.equals(cursor.getString(0))) {
                    //이메일이 틀렸습니다.
                    Toast toast = Toast.makeText(MainActivity.this, "이메일이 틀렸습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //로그인성공
                 //   Toast toast = Toast.makeText(MainActivity.this, "로그인성공", Toast.LENGTH_SHORT);
                    //toast.show();
                    //인텐트 생성 및 호출

                    sql = "SELECT profile FROM " + helper.tableName + " WHERE id = '" + id + "'";
                    cursor = database.rawQuery(sql, null);
                    cursor.moveToLast();

                    //Toast.makeText(MainActivity.this,cursor.getString(0),Toast.LENGTH_LONG).show();

                    MyProfile.getInstance().setProfile(cursor.getString(0));

                    Intent intent = new Intent(MainActivity.this, lockActivity.class);
                    intent.putExtra("caller", "0");
                    startActivity(intent);

                    finish();
                }
                cursor.close();
            }
        });


    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.i("MainActivity.java | onActivityResult", "|==" + requestCode + "|" + resultCode + "(ok = " + RESULT_OK + ")|" + data);
//        if (resultCode != RESULT_OK)
//            return;
//
//        if (requestCode == REQUEST_ENABLE_BT)
//        {
//            discovery();
//        }
//        else if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE)
//        {
//            String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//            Log.i("MainActivity.java | onActivityResult", "|==" + address + "|");
//            if (TextUtils.isEmpty(address))
//                return;
//
//            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
//            _btService1.connect(device);
//            if(address.equals("98:D3:31:F7:37:38")){
//            _btService1.connect(device);
//            }else{
//                _btService2.connect(device);
//            }
//        }
//    }
//
//
//    private void checkIntent(Intent $intent)
//    {
//        Log.i("MainActivity.java | checkIntent", "|==" + $intent.getAction() + "|");
//        if ("kr.mint.bluetooth.receive".equals($intent.getAction()))
//        {
//            Log.i("MainActivity.java | checkIntent", "|==" + $intent.getStringExtra("msg") + "|");
//        }
//    }
//
//
//    public void onBtnClick(View v)
//    {
//        discovery();
//    }
//
//
//    private void discovery()
//    {
//        Intent serverIntent = new Intent(this, DeviceListActivity.class);
//        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//    }
//    public BTService get_btService1(){return _btService1;}
//    public BTService get_btService2(){return _btService2;}

}