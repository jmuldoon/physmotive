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
import edu.usf.cse.physmotive.db.UserDBM;

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
    
    static final String HEIGHT = "height";
    static final String WEIGHT = "weight";
	
	protected Cursor cursor;
	
	public Statistics(Cursor cur){
		cursor = cur;
	}
	
	public double getBMI(){
		double weight = 0, height = 0;
		
		// Assuming standard Metric units
		cursor.moveToFirst();
		height = cursor.getInt(cursor.getColumnIndex(HEIGHT));
		weight = cursor.getInt(cursor.getColumnIndex(WEIGHT));
		
		// Height is divided by 100 to convert from cm to m
		return weight/Math.pow((height/100), 2);
	}
	
	
	public float getRaceTotalTime(){
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
	
	public float getRaceTotalDistance(){
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
	
	
	public float getActivityTotalTime(){
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
		
		return total;
	}
	
	public float getActivityTotalDistance(){
		long total = 0;
		
		cursor.moveToFirst();
		
		for(;cursor.moveToNext();cursor.moveToNext()){
			total += cursor.getLong(cursor.getColumnIndex(TDISTANCE));
		}
		
		return total;
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
