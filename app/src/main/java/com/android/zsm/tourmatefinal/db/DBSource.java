package com.android.zsm.tourmatefinal.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSource extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "take_tour_db";
    public static final int DATABASE_VERSION = 1;

    public static final String USER_TABLE = "tbl_user";
    public static final String COL_USR_ID = "user_id";
    public static final String COL_USR_NAME = "user_name";
    public static final String COL_USR_EMAIL = "user_email";
    public static final String COL_USR_PASSWORD = "user_password";
    public static final String COL_USR_PHONE = "user_phone";
    public static final String COL_USR_STATUS = "user_status";

    public static final String CREATE_TABLE_USER = "create table if not exists "+USER_TABLE+"("+
            COL_USR_ID+" INTEGER primary key, "+
            COL_USR_NAME+" TEXT, "+
            COL_USR_EMAIL+" TEXT, "+
            COL_USR_PHONE+" TEXT, "+
            COL_USR_PASSWORD+" TEXT, "+
            COL_USR_STATUS+" INTEGER DEFAULT 1);";

    public static final String EVENT_TABLE = "tbl_event";
    public static final String COL_EVENT_ID = "event_id";
    public static final String COL_EVENT_NAME = "event_name";
    public static final String COL_START_DATE = "start_date";
    public static final String COL_END_DATE = "end_date";
    public static final String COL_TOTAL_BUDGETS = "total_budget";
    public static final String COL_EVENT_STATUS = "event_status";

    public static final String CREATE_TABLE_EVENT = "create table if not exists "+EVENT_TABLE+"("+
            COL_EVENT_ID+" INTEGER primary key, "+
            COL_USR_ID+" INTEGER primary key, "+
            COL_EVENT_NAME+" TEXT, "+
            COL_START_DATE+" TEXT, "+
            COL_END_DATE+" TEXT, "+
            COL_TOTAL_BUDGETS+" TEXT, "+
            COL_EVENT_STATUS+" INTEGER DEFAULT 0) , FOREIGN KEY(" + COL_USR_ID +") REFERENCES " + USER_TABLE + "("+ COL_USR_ID+ ")); ";

    public DBSource(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
