package edu.usf.cse.physmotive.logic;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Statistics
{
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

    public Statistics(Cursor cur) {
        cursor = cur;
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
    
    public double getBMI()
    {
        double bmi = 0, weight = 0, height = 0;

        if (cursor.getCount() > 0)
        {
            // Assuming standard Metric units
            cursor.moveToFirst();
            height = cursor.getInt(cursor.getColumnIndex(HEIGHT));
            weight = cursor.getInt(cursor.getColumnIndex(WEIGHT));

            // Height is divided by 100 to convert from cm to m
            bmi = weight / Math.pow((height / 100), 2);
        }

        return bmi;
    }

    // initial = ini, final = fin, and time is the time between both points
    public static double getSpeed(float distance, double time){
    	double spd = 0;
    	if (time != 0){
    		spd = (Math.abs(distance) / Math.abs(time));
    	}
    	return spd;
    }
    
    public float getRaceTotalTime()
    {
        float diff = 0;
        long endTime = 0, startTime = 0;
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);

        if (cursor.getCount() > 0)
        {
            try
            {
                cursor.moveToFirst();
                date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(LTS)));
                startTime = date.getTime();
                cursor.moveToLast();
                date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(LTS)));
                endTime = date.getTime();
            } catch (ParseException ex)
            {
                Log.e("Date", ex.getMessage(), ex);
            }

            diff = endTime - startTime;
        }
        return diff;
    }

    public float getRaceTotalDistance()
    {
        float[] result = new float[3];
        float sum = 0;
        GeoPoint curr, prev;

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            curr = new GeoPoint((int) (Integer.valueOf(cursor.getString(cursor.getColumnIndex(LATITUDE))) * 1E6),
                    (int) (Integer.valueOf(cursor.getString(cursor.getColumnIndex(LONGITUDE))) * 1E6));

            do{
                prev = curr;
                // Integer.parseInt(cursor.getString(cursor.getColumnIndex(LATITUDE)));
                curr = new GeoPoint(Integer.parseInt(cursor.getString(cursor.getColumnIndex(LATITUDE))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex(LONGITUDE))));
                Location.distanceBetween(prev.getLatitudeE6() / 1E6, prev.getLongitudeE6() / 1E6,
                        curr.getLatitudeE6() / 1E6, curr.getLongitudeE6() / 1E6, result);
                sum += result[0];
            }while(cursor.moveToNext());
        }

        return sum;
    }

    public float getActivityTotalTime()
    {
        long total = 0;
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do{
                try
                {
                    date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(TTIME)));
                    total += date.getTime();
                } catch (ParseException ex)
                {
                    Log.e("Date", ex.getMessage(), ex);
                }
            }while(cursor.moveToNext());
        }

        return total;
    }

    public float getActivityTotalDistance()
    {
        long total = 0;

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do{
                total += cursor.getLong(cursor.getColumnIndex(TDISTANCE));
            }while(cursor.moveToNext());
        }

        return total;
    }

    public float getAverageTime()
    {
        float avg = 0;
        long total = 0;
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.US);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do{
                try
                {
                    date = dateFormat.parse(cursor.getString(cursor.getColumnIndex(TTIME)));
                    total += date.getTime();
                } catch (ParseException ex)
                {
                    Log.e("Date", ex.getMessage(), ex);
                }
            }while(cursor.moveToNext());

            avg = total / cursor.getCount();
        }

        return avg;
    }

    public float getAverageDistance()
    {
        float avg = 0;
        long total = 0;

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do{
                total += cursor.getLong(cursor.getColumnIndex(TDISTANCE));
            }while(cursor.moveToNext());

            if (cursor.getCount() > 0)
                avg = total / cursor.getCount();
        }
        return avg;
    }

    public int getTotalNumberActivities()
    {
        return cursor.getCount();
    }
}
