package edu.usf.cse.physmotive.logic;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.database.Cursor;
import android.location.Location;
import android.util.Log;
import edu.usf.cse.physmotive.db.LocationDBM;

public class Statistics {
	static final String TABLENAME = "locations";
    static final String ID = "_id";
    static final String FKEY = "race_id";
    static final String LATITUDE = "lat";
    static final String LONGITUDE = "lng";
    static final String LTS = "locationTimeStamp";
    static final String EUSR = "entryUsr";
    static final String EDATE = "entryDate";
    static final String TTIME = "totalTime";
    static final String TDISTANCE = "totalDistance";
	
	protected LocationDBM dblManager;
	protected Cursor cursor;
	
	Statistics(Cursor cur){
		cursor = cur;
	}
	
	public float getTotalTime(){
		float diff;
		long endTime = 0, startTime = 0;
		Date date;
		DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);		
		
		try{
			cursor.moveToFirst();
			date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(LTS)));
			startTime = date.getTime();
			cursor.moveToLast();
			date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(LTS)));
			endTime = date.getTime();
		}
		catch(ParseException ex){
			Log.e("Date", ex.getMessage(), ex);
		}
		
		diff = endTime - startTime;
		
		return diff;
	}
	
	public float getTotalDistance(){
		float[] result = new float[3];
		float sum = 0;
		GeoPoint curr, prev;
		
		cursor.moveToFirst();
		curr = new GeoPoint(Integer.valueOf(cursor.getString(cursor.getColumnIndex(LATITUDE))), Integer.valueOf(cursor.getString(cursor.getColumnIndex(LONGITUDE))));
		
		for(;cursor.moveToNext();cursor.moveToNext()){
			prev = curr;
			curr =  new GeoPoint(Integer.valueOf(cursor.getString(cursor.getColumnIndex(LATITUDE))), Integer.valueOf(cursor.getString(cursor.getColumnIndex(LONGITUDE))));
			Location.distanceBetween(prev.getLatitudeE6()/1E6, prev.getLongitudeE6()/1E6, curr.getLatitudeE6()/1E6, curr.getLongitudeE6()/1E6, result);
			sum += result[0];
		}
		
		return sum;
	}
	
	public float getAverageTime(){
		float avg;
		long total = 0;
		Date date;
		DateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);	
		
		cursor.moveToFirst();
		
		for(;cursor.moveToNext();cursor.moveToNext()){
			try{
				date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(TTIME)));
				total += date.getTime();
			}
			catch(ParseException ex){
				Log.e("Date", ex.getMessage(), ex);
			}
		}
		
		avg = total/cursor.getCount();
		
		return avg;
	}
	
	public float getAverageDistance(){
		float avg;
		long total = 0;
		
		cursor.moveToFirst();
		
		for(;cursor.moveToNext();cursor.moveToNext()){
			total += cursor.getLong(cursor.getColumnIndex(TDISTANCE));
		}
		
		avg = total/cursor.getCount();
		
		return avg;
	}
	
	public int getTotalNumberActivities(){
		return cursor.getCount();
	}
}
