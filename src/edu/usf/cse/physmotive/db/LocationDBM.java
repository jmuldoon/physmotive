package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocationDBM
{
    static final String TABLENAME = "locations";
    static final String ID = "_id";
    static final String FKEY = "race_id";
    static final String LATITUDE = "lat";
    static final String LONGITUDE = "lng";
    static final String LTS = "locationTimeStamp";
    static final String EUSR = "entryUsr";
    static final String EDATE = "entryDate";
    static final String UUSR = "updateUsr";
    static final String UDATE = "updateDate";

    private final Context androidContext;
    private PhysMotiveDBH dbHelper;
    private SQLiteDatabase db;

    public LocationDBM(Context ctx) {
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

    public SQLiteDatabase getDB()
    {
        return db;
    }

    // TODO: add column for status text.
    public int insert(int raceID, String lat, String lng, int tmStmp, long usr)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        values.put(FKEY, raceID);
        values.put(LATITUDE, lat);
        values.put(LONGITUDE, lng);
        values.put(LTS, Integer.toString(tmStmp));
        values.put(EUSR, usr);
        values.put(EDATE, timeStamp);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        return (int) db.insert(TABLENAME, null, values);
    }

    public boolean delete(long raceID, long usr)
    {
        ContentValues values = new ContentValues();
        String whereClause = FKEY + "=" + raceID;
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();

        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

    public Cursor get(long id)
    {
        String[] columns = new String[] { ID, EUSR, EDATE, UUSR, UDATE };
        String whereClause = ID + "=" + id;
        Cursor mCursor = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    public Cursor getList(long userId, long raceID)
    {
        String[] columns = new String[] { ID, FKEY, LATITUDE, LONGITUDE, LTS, UDATE };
        String whereClause = FKEY + "=" + raceID;
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }
    
    public Cursor getForExport(int id)
    {
        String whereClause = EUSR + "=" + id;
        Cursor c = db.query(TABLENAME, null, whereClause, null, null, null, null);
        c.moveToFirst();
        
        return c;
    }
}
