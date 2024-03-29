package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

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
    static final String SPEED = "speed";
    static final String LTS = "locationTimeStamp";
    static final String NOTES = "notes";
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
    public int insert(int raceID, int lat, int lng, double spd, int tmStmp, String notes, int usr)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        values.put(FKEY, raceID);
        values.put(LATITUDE, lat);
        values.put(LONGITUDE, lng);
        values.put(SPEED, spd);
        values.put(LTS, Integer.toString(tmStmp));
        values.put(NOTES, notes);
        values.put(EUSR, usr);
        values.put(EDATE, timeStamp);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        return (int) db.insert(TABLENAME, null, values);
    }

    public int delete(int raceID)
    {
        String whereClause = FKEY + "=" + raceID;

        return db.delete(TABLENAME, whereClause, null);
    }

    public Cursor get(int id)
    {
        String[] columns = new String[] { ID, EUSR, EDATE, UUSR, UDATE };
        String whereClause = ID + "=" + id;
        Cursor mCursor = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    public Cursor getList(int raceID, int day, int month, int year)
    {
        String[] columns = new String[] { ID, FKEY, LATITUDE, LONGITUDE, SPEED, LTS };
        String whereClause;
        Date cDate = new Date(), date = new Date();
        Calendar cal = Calendar.getInstance();

        // initiates the setting of calendar to the current date
        cal.setTime(date);

        // just sets the date for date chosen with the filter.
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);
        if (day < 0 || month < 0 || year < 0)
        {
            whereClause = FKEY + "=" + raceID;
        } else
        {
            whereClause = FKEY + "=" + raceID + " and " + date + " <= " + cDate;
        }
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getAllList(int userID, int day, int month, int year)
    {
        String[] columns = new String[] { ID, FKEY, LATITUDE, LONGITUDE, SPEED, LTS };
        String whereClause;
        Date cDate = new Date(), date = new Date();
        Calendar cal = Calendar.getInstance();

        // initiates the setting of calendar to the current date
        cal.setTime(date);

        // just sets the date for date chosen with the filter.
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);
        if (day < 0 || month < 0 || year < 0)
        {
            whereClause = EUSR + "=" + userID;
        } else
        {
            whereClause = EUSR + "=" + userID + " and " + date + " <= " + cDate;
        }
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
