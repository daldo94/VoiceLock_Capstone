package com.example.voicelock;

/**
 * Created by Hp on 3/18/2016.
 */
public class Constants {
    //COLUMNS
    static final String ROW_ID="id";
    static final String NAME = "name";
    static final String POSITION = "position";
    static final String OPEN="open";
    static final String UUID="uuid";
    static String BLUETOOTH="ble";

    //DB PROPERTIES
    static final String DB_NAME="d_DB";
    static final String TB_NAME="d_TB";
    static final int DB_VERSION='1';

    static final String CREATE_TB="CREATE TABLE d_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,position TEXT NOT NULL,open INT, uuid TEXT, ble TEXT);";
}
