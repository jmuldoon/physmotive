package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ActivityDBM
{
    // Database Values, NOT COMPLETE LIST PLEASE ADD WHERE NEEDED
    static final String TABLENAME = "activity";
    static final String ID = "_id";
    static final String DID = "diaryId";
    static final String AID = "activityId";
    static final String TTIME = "totalTime";
    static final String TDISTANCE = "totalDistance";
    static final String CHECK = "checked";
    static final String EUSR = "entryUsr";
    static final String EDATE = "entryDate";
    static final String UUSR = "updateUsr";
    static final String UDATE = "updateDate";
    static final String DEL = "deleted";

    private final Context androidContext;
    private PhysMotiveDBH dbHelper;
    private SQLiteDatabase db;

    public ActivityDBM(Context ctx) {
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

    public int insert(int usr, int activitySelection)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        values.put(AID, activitySelection);
        values.put(DID, 0);
        values.put(CHECK, 0);
        values.put(EUSR, usr);
        values.put(EDATE, timeStamp);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);
        values.put(DEL, 0);

        return (int) db.insert(TABLENAME, null, values);
    }

    public boolean update(int id, int usr, int time, int distance)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(TTIME, time);
        values.put(TDISTANCE, distance);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        whereClause = ID + "=" + id;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

    public boolean delete(int id)
    {
        String whereClause = ID + " = " + id;

        db.delete("locations", "race_id = " + id, null);
        return db.delete(TABLENAME, whereClause, null) > 0;
    }

    public Cursor get(int id)
    {
        String[] columns = new String[] { ID, DID, EUSR, EDATE, UUSR, UDATE, DEL };
        String whereClause = ID + "=" + id;
        Cursor mCursor = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    public Cursor getList(int userId)
    {
        String[] columns = new String[] { ID, EDATE, UDATE };
        String whereClause = EUSR + " = " + userId + " and " + DEL + " <> 1";
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getBindingList(int diaryId)
    {
        String[] columns = new String[] { ID, DID, EDATE, CHECK };
        String whereClause = DID + " = " + diaryId + " or " + DID + " < 1";
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getBoundList(int diaryId)
    {
        String[] columns = new String[] { ID, DID, EDATE, CHECK };
        String whereClause = DID + " = " + diaryId;
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getStatisticsList(int userID, int activityID, int day, int month, int year)
    {
        String[] columns = new String[] { ID, TTIME, TDISTANCE, EDATE };

        // this is for past week.
        String whereClause = EUSR + " = " + userID + " and " + AID + " = " + activityID + " and strftime('%s', " + EDATE
                + ") >= strftime('%s', date('" + Integer.toString(year) + "-" + String.format("%02d", month) + "-"
                + String.format("%02d", day) + "'))";

        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }
    
//<<<<<<< HEAD
//    public Cursor getStatisticsList(int userID, int activityID)
//    {
//        String[] columns = new String[] { ID, TTIME, TDISTANCE, EDATE };
//
//        // this is for past week.
//        String whereClause = EUSR + " = " + userID + " and " + AID + " = " + activityID;
//=======
    public Cursor getRaceStats(int userID, int raceID)
    {
        String[] columns = new String[] { ID, TTIME, TDISTANCE, EDATE };

        String whereClause = EUSR + " = " + userID + " and " + ID + " = " + raceID;

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

    public boolean setChecked(int raceId, int diaryId, int userId)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(UUSR, userId);
        values.put(UDATE, timeStamp);
        values.put(DID, diaryId);
        values.put(CHECK, 1);

        whereClause = ID + "=" + raceId;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

    public boolean setUnChecked(int raceId, int userId)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(UUSR, userId);
        values.put(UDATE, timeStamp);
        values.put(DID, 0);
        values.put(CHECK, 0);

        whereClause = ID + "=" + raceId;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }
}
