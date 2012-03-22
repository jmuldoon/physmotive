package edu.usf.cse.physmotive.db;

import java.sql.Timestamp;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DiaryDBM {
	// Database
	static final String TABLENAME = "diary";
	static final String ID = "_id";
	static final String NAME = "name";
	static final String HEIGHT = "height";
	static final String WEIGHT = "weight";
	static final String AGE = "age";
	static final String GENDER = "gender";
	static final String NOTE = "note";
	static final String EUSR = "entryUsr";
	static final String EDATE = "entryDate";
	static final String UUSR = "updateUsr";
	static final String UDATE = "updateDate";
	static final String DEL = "deleted";

	private final Context androidContext;
	private PhysMotiveDBH dbHelper;
	private SQLiteDatabase db;

	public DiaryDBM(Context ctx) {
		this.androidContext = ctx;
	}

	public void open() throws SQLException {
		dbHelper = new PhysMotiveDBH(androidContext);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
		dbHelper.close();
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public long insert(String Name, long ht, long wt, long age, long gender,
			String note, long usr) {
		String timeStamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis()).toString();
		ContentValues values = new ContentValues();

		values.put(NAME, Name);
		values.put(HEIGHT, ht);
		values.put(WEIGHT, wt);
		values.put(AGE, age);
		values.put(GENDER, gender);
		values.put(NOTE, note);
		values.put(EUSR, usr);
		values.put(EDATE, timeStamp);
		values.put(UUSR, usr);
		values.put(UDATE, timeStamp);
		values.put(DEL, 0);

		return (int)db.insert(TABLENAME, null, values);
	}

	public boolean update(long id, String Name, long ht, long wt, long age,
			long gender, String note, long usr) {
		String timeStamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis()).toString();
		String whereClause;
		ContentValues values = new ContentValues();

		values.put(NAME, Name);
		values.put(HEIGHT, ht);
		values.put(WEIGHT, wt);
		values.put(AGE, age);
		values.put(GENDER, gender);
		values.put(NOTE, note);
		values.put(UUSR, usr);
		values.put(UDATE, timeStamp);

		whereClause = ID + "=" + id;

		return db.update(TABLENAME, values, whereClause, null) > 0;
	}

	public boolean delete(long id, long Usr) {
		ContentValues values = new ContentValues();
		String whereClause = ID + "=" + id;
		String timeStamp = new Timestamp(Calendar.getInstance()
				.getTimeInMillis()).toString();

		values.put(DEL, 1);
		values.put(UUSR, Usr);
		values.put(UDATE, timeStamp);

		return db.update(TABLENAME, values, whereClause, null) > 0;
	}

	public Cursor get(long id) {
		String[] columns = new String[] { ID, NAME, EUSR, EDATE, UUSR, UDATE,
				DEL };
		String whereClause = ID + "=" + id;
		Cursor mCursor = db.query(TABLENAME, columns, whereClause, null, null,
				null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();

		return mCursor;
	}

	public Cursor getList(long userId) {
		String[] columns = new String[] { ID, NAME, UDATE };
		String whereClause = EUSR + " = " + userId + " and " + DEL + " <> 1";
		Cursor c = db.query(TABLENAME, columns, whereClause, null, null, null,
				null, null);
		c.moveToFirst();

		return c;
	}

}
