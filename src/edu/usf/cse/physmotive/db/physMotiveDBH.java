package edu.usf.cse.physmotive.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhysMotiveDBH extends SQLiteOpenHelper
{

    static final String DATABASE_NAME = "physmotive";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_DIARY = "create table diary (_id integer primary key autoincrement,"
	    + " name text not null, height float, weight float, age integer, gender integer,"
	    + " note text, entryUsr integer not null, entryDate integer not null,"
	    + " updateUsr integer, updateDate integer, deleted integer);";

    // Only created so I can make a list of activities, Please update as
    // necessary. I will be using EntryDate as the name of the activity.
    static final String CREATE_ACTIVITY = "create table activity (_id integer primary key autoincrement,"
	    + " name text not null, entryUsr integer not null, entryDate integer not null,"
	    + " updateUsr integer, updateDate integer, deleted integer);";

    static final String CREATE_USER = "create table users (_id integer primary key autoincrement,"
	    + " firstName text not null, lastName text not null, height float, weight float,"
	    + " age integer, gender integer, units integer, orientation integer, dareFormat integer,"
	    + " entryUsr integer not null, entryDate integer not null, updateUsr integer,"
	    + " updateDate integer, deleted integer);";

    PhysMotiveDBH(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
	db.execSQL(CREATE_DIARY);
	db.execSQL(CREATE_ACTIVITY);
	db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVerison)
    {
	db.execSQL("Drop Table If Exists MT_Collections");
	onCreate(db);
    }

}
