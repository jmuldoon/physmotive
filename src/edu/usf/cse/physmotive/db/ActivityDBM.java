package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

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

    public int insert(int usr)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        ContentValues values = new ContentValues();

        values.put(EUSR, usr);
        values.put(EDATE, timeStamp);
        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);
        values.put(DEL, 0);

        return (int) db.insert(TABLENAME, null, values);
    }

    public boolean update(int id, int usr)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(UUSR, usr);
        values.put(UDATE, timeStamp);

        whereClause = ID + "=" + id;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

    public boolean delete(int id, int Usr)
    {
        ContentValues values = new ContentValues();
        String whereClause = ID + "=" + id;
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();

        values.put(DEL, 1);
        values.put(UUSR, Usr);
        values.put(UDATE, timeStamp);

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }

    public Cursor get(int id)
    {
        String[] columns = new String[] { ID, EUSR, EDATE, UUSR, UDATE, DEL };
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

    public Cursor getBindingList(int userId, int diaryId)
    {
        String[] columns = new String[] { ID, DID, EDATE, CHECK };
        String whereClause = DID + " = " + diaryId + " or " + DID + " is null";
        Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null, null, null);
        c.moveToFirst();

        return c;
    }
    
    public Cursor getStatisticsList(int userID, int activityID, int time){
    	String[] columns = new String[] { ID, TTIME, TDISTANCE, EDATE };
    	Date cDate = new Date(), date = new Date();
    	Calendar cal = Calendar.getInstance();
        
        cal.setTime(date);
        
        switch(time){
        	case 0: cal.add(Calendar.DAY_OF_MONTH, -1);
        		break;
        	case 1: cal.add(Calendar.WEEK_OF_YEAR, -1); 
    			break;
        	case 2: cal.add(Calendar.MONTH, -1); 
    			break;
        	case 3: cal.add(Calendar.YEAR, -1); 
    			break;
    		default: //NOP pull all dates
    			break;
        }
        
        date.setTime(cal.getTime().getTime());
    	
        String whereClause = EUSR + " = " + userID + " and " + ID + " = " + activityID + " and " + date + " <= " + cDate;;
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

    public boolean setChecked(int raceId, int diaryId, int userId, int value)
    {
        String timeStamp = new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
        String whereClause;
        ContentValues values = new ContentValues();

        values.put(UUSR, userId);
        values.put(UDATE, timeStamp);
        values.put(DID, diaryId);
        values.put(CHECK, value);

        whereClause = ID + "=" + raceId;

        return db.update(TABLENAME, values, whereClause, null) > 0;
    }
}
