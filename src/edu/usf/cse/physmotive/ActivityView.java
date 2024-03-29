package edu.usf.cse.physmotive;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.LocationDBM;
import edu.usf.cse.physmotive.logic.Statistics;

public class ActivityView extends MapActivity implements LocationListener
{
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    static final String USERID = "userId";
    static final String ACTIVITYID = "activityId";
    static final String DIARYID = "diaryId";
    static final String LATITUDE = "lat";
    static final String LONGITUDE = "lng";
    static final String TOTALTIME = "totalTime";
    static final String TOTALDISTANCE = "totalDistance";
    static final String ID = "_id";
    static final String EDATE = "entryDate";

    protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
    protected GeoPoint point;
    protected List<Overlay> mapOverlays;

    protected Button statistics_btn;
    protected Button diary_btn;
    protected TextView raceId_tv;
    protected TextView raceDate_tv;
    protected TextView raceTotPace_tv;
    protected TextView raceTotTime_tv;
    protected TextView raceTotDist_tv;

    // Future implementation
    // protected TextView raceTarPace_tv;
    // protected TextView raceTarTime_tv;
    // protected TextView raceTarDist_tv;

    private int userId, activityId, diaryId = 0;
    private Cursor activityInfo;
    private Cursor locationCursor;
    private Cursor activityCursor;
    private ActivityDBM activityDBM;
    private LocationDBM locationDBM;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);

        // Connect interface elements to properties
        diary_btn = (Button) findViewById(R.id.toDiaryButton);
        statistics_btn = (Button) findViewById(R.id.toStatisticsButton);
        raceId_tv = (TextView) findViewById(R.id.raceIdTextView);
        raceDate_tv = (TextView) findViewById(R.id.raceDateTextView);
        raceTotPace_tv = (TextView) findViewById(R.id.paceTextView);
        raceTotTime_tv = (TextView) findViewById(R.id.timeTextView);
        raceTotDist_tv = (TextView) findViewById(R.id.distanceTextView);

        activityDBM = new ActivityDBM(this);
        locationDBM = new LocationDBM(this);

        retrieveBundleInfo();
        setOnClickListeners();

        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);

        // TODO: Make logic so you cannot infinite loop
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupTextViews();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        activityCursor.close();
        activityInfo.close();
        locationCursor.close();
        activityDBM.close();
    }

    private void setupTextViews()
    {
        // Variables we will need at some point
        // float time, dist, pace;

        activityDBM.open();
        activityInfo = activityDBM.get(activityId);
        startManagingCursor(activityInfo);
        activityDBM.close();

        raceId_tv.setText("Race Id #" + activityInfo.getString(activityInfo.getColumnIndex(ID)));
        raceDate_tv.setText(activityInfo.getString(activityInfo.getColumnIndex(EDATE)));

        diaryId = activityInfo.getInt(activityInfo.getColumnIndex(DIARYID));

        // updates the location databased part
        updateStatistics();
        initializeMap();
    }

    private void updateStatistics()
    {
        int day = -1, month = -1, year = -1; // -1 defaults for all

        activityDBM.open();
        activityCursor = activityDBM.getRaceStats(userId, activityId);
        startManagingCursor(activityCursor);
        activityDBM.close();

        locationDBM.open();
        locationCursor = locationDBM.getList(activityId, day, month, year);
        startManagingCursor(locationCursor);
        locationDBM.close();

        raceTotTime_tv.setText("Total Time: " + activityCursor.getDouble(activityCursor.getColumnIndex(TOTALTIME)) + " s");
        raceTotDist_tv.setText("Total Distance: " + activityCursor.getFloat(activityCursor.getColumnIndex(TOTALDISTANCE))
                + " m");
        raceTotPace_tv
                .setText("Speed: "
                        + Statistics.roundTwoDecimals(((double) activityCursor.getFloat(activityCursor
                                .getColumnIndex(TOTALDISTANCE)) / (double) activityCursor.getInt(activityCursor
                                .getColumnIndex(TOTALTIME)))) + " m/s");
    }

    private void initializeMap()
    {
        mapController = mapView.getController();

        locationCursor.moveToFirst();
        do
        {
            point = new GeoPoint(Integer.valueOf(locationCursor.getString(locationCursor.getColumnIndex(LATITUDE))),
                    Integer.valueOf(locationCursor.getString(locationCursor.getColumnIndex(LONGITUDE))));

            addGeoPoint(point, "Current Location", "lat : lng");
        } while (locationCursor.moveToNext());
    }

    private void bundleUserInformation(Intent mIntent)
    {
        Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putInt(ACTIVITYID, activityId);
        b.putInt(DIARYID, diaryId);
        mIntent.putExtras(b);
    }

    private void retrieveBundleInfo()
    {
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        activityId = b.getInt(ACTIVITYID);

        activityDBM.open();
        Cursor c = activityDBM.getDiaryId(activityId);
        startManagingCursor(c);
        activityDBM.close();

        diaryId = c.getInt(c.getColumnIndex(DIARYID));
    }

    public void addGeoPoint(GeoPoint p, String greeting, String message)
    {
        OverlayItem overlayitem = new OverlayItem(p, greeting, message);

        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }

    @Override
    public void onLocationChanged(Location loc)
    {
        // Do Nothing. Static Map
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("onProviderDisabled", "GPS Disabled");
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d("onProviderEnabled", "GPS Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // DO NOTHING
    }

    private void setOnClickListeners()
    {
        statistics_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickStatistics(v);
            }
        });
        diary_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickDiary(v);
            }
        });
    }

    private void onButtonClickStatistics(View w)
    {
        Intent myIntent = new Intent(w.getContext(), StatisticsMenu.class);
        bundleUserInformation(myIntent);
        startActivity(myIntent);
    }

    private void onButtonClickDiary(View w)
    {
        Intent myIntent;
        if (diaryId < 1)
            myIntent = new Intent(w.getContext(), DiaryList.class);
        else
            myIntent = new Intent(w.getContext(), DiaryView.class);

        bundleUserInformation(myIntent);
        startActivity(myIntent);
    }
}
