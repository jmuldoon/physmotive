package edu.usf.cse.physmotive.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class GeneralDBM
{
    private final Context androidContext;
    private PhysMotiveDBH dbHelper;
    private SQLiteDatabase db;
    
    public GeneralDBM(Context ctx)
    {
        this.androidContext = ctx;
    }
    
    public void open() throws SQLException
    {
        dbHelper = new PhysMotiveDBH(androidContext);
        db = dbHelper.getWritableDatabase();
    }
    
    public void close()
    {
        db.close();
        dbHelper.close();
    }
    
    public abstract int insert();
    public abstract boolean update();
    public abstract boolean delete();
    public abstract Cursor get();
    public abstract Cursor getList();
    // TODO: make this a nonabstract method
    public abstract Cursor getForExport(); 
}