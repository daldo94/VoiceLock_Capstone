package com.example.voicelock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Hp on 3/18/2016.
 */
public class DBAdapter {
    Context c;

    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper=new DBHelper(c);
    }

    //OPEN DATABASE
    public DBAdapter openDB()
    {
        try {
            db=helper.getWritableDatabase();

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return this;
    }

    //CLOSE DATABASE
    public void closeDB()
    {
        try {
          helper.close();

        }catch (SQLException e)
        {
            e.printStackTrace();
        }


    }

    //INSERT
    public long add(String name,String pos, Integer open, String address)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(Constants.NAME,name);
            cv.put(Constants.POSITION, pos);
            cv.put(Constants.OPEN, open);
            if(!address.equals("98:D3:61:F9:3E:75")){
                cv.put(Constants.UUID, "98:D3:61:F9:3E:75");
            }else{
                cv.put(Constants.UUID, "98:D3:31:F7:37:38");
            }
            return db.insert(Constants.TB_NAME,Constants.ROW_ID,cv);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    //RETRIEVE
    public Cursor getAllPlayers()
    {
        String[] columns={Constants.ROW_ID,Constants.NAME,Constants.POSITION,Constants.OPEN, Constants.UUID};

        return db.query(Constants.TB_NAME,columns,null,null,null,null,null);

    }
    public long update(int open,String voice){
        ContentValues values =new ContentValues();
        if(open==0||open==1) {
            values.put("open", open);
            return db.update(Constants.TB_NAME, values, "name" + "=?", new String[]{voice});
        }else{
            values.put("ble", open);
            return db.update(Constants.TB_NAME, values, "name" + "=?", new String[]{voice});
        }
    }
    public long update(String name,String position, String address){
        ContentValues values =new ContentValues();
        values.put("name", name);
        values.put("position", position);
        return db.update(Constants.TB_NAME,values,"uuid"+"=?", new String[]{address});
    }

    public long delete(String voice){
        ContentValues values =new ContentValues();
        return db.delete(Constants.TB_NAME,"name"+"=?", new String[]{voice});
    }
}














