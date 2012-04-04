package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhysMotiveDBH extends SQLiteOpenHelper
{

    static final String DATABASE_NAME = "physmotive";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_DIARY = "create table diary (_id integer primary key autoincrement,"
            + " name text not null, height float DEFAULT 0, weight float DEFAULT 0, age integer DEFAULT 0, gender integer DEFAULT 0,"
            + " note text, entryUsr integer not null, entryDate integer not null,"
            + " updateUsr integer, updateDate integer, deleted integer);";

    // Only created so I can make a list of activities, Please update as
    // necessary. I will be using EntryDate as the name of the activity.
    static final String CREATE_ACTIVITY = "create table activity (_id integer primary key autoincrement,"
            + " diaryId integer DEFAULT 0, activityId integer not null, totalTime integer DEFAULT 0, "
            + " totalDistance float DEFAULT 0, checked integer DEFAULT 0,"
            + " entryUsr integer not null, entryDate integer not null,"
            + " updateUsr integer, updateDate integer, deleted integer);";

    static final String CREATE_USER = "create table users (_id integer primary key autoincrement,"
            + " firstName text not null, lastName text not null, height float DEFAULT 0, weight float DEFAULT 0,"
            + " age integer DEFAULT 0, gender integer DEFAULT 0, units integer DEFAULT 0, "
            + " orientation integer DEFAULT 0, dateFormat integer DEFAULT 0, entryUsr integer not null,"
            + " entryDate integer not null, updateUsr integer, updateDate integer, deleted integer);";

    static final String CREATE_LOCATION = "create table locations (_id integer primary key autoincrement,"
            + " race_id integer not null, lat integer not null, lng integer not null, speed float not null, locationTimeStamp integer not null, notes text, entryUsr integer not null,"
            + " entryDate integer not null, updateUsr integer, updateDate integer);";

    PhysMotiveDBH(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_DIARY);
        db.execSQL(CREATE_ACTIVITY);
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_LOCATION);
        createDefaults(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVerison)
    {
        db.execSQL("Drop Table If Exists MT_Collections");
        onCreate(db);
    }

    private void createDefaults(SQLiteDatabase db)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();
        values.put("firstName", "Default");
        values.put("lastName", "User");
        values.put("height", 0);
        values.put("weight", 0);
        values.put("age", 0);

        values.put("gender", 1);
        values.put("units", 1);
        values.put("orientation", 1);
        values.put("dateFormat", 0);
        values.put("entryUsr", 1);
        values.put("entryDate", timeStamp);
        values.put("updateUsr", 0);
        values.put("updateDate", timeStamp);
        values.put("deleted", 0);
        db.insert("users", null, values);
    }
}
