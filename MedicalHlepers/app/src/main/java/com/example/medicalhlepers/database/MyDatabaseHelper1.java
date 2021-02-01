package com.example.medicalhlepers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper1 extends SQLiteOpenHelper {

    public static final String CREATE_HOSPITAL = "create table Hospital ("
            + "id integer primary key autoincrement, "
            + "hosName text, hosId integer, hosWeb text, hosLevel text," +
            "hosType text, hosAddre text, hosPhone text, hosPhoto text)";

    private Context mContext;

    public MyDatabaseHelper1(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HOSPITAL);
        Toast.makeText(mContext, "医院列表已载入", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}