package edu.usf.cse.physmotive;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
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
    private ActivityDBM activityDBM;
    private LocationDBM locationDBM;
    private Statistics statsLocation;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);
        
        
        retrieveBundleInfo();

        // Connect interface elements to properties
        diary_btn = (Button) findViewById(R.id.toDiaryButton);
        statistics_btn = (Button) findViewById(R.id.toStatisticsButton);
        raceId_tv = (TextView) findViewById(R.id.raceIdTextView);
        raceDate_tv = (TextView) findViewById(R.id.raceDateTextView);
        raceTotPace_tv = (TextView) findViewById(R.id.paceTextView);
        raceTotTime_tv = (TextView) findViewById(R.id.timeTextView);
        raceTotDist_tv = (TextView) findViewById(R.id.distanceTextView);

        activityDBM = new ActivityDBM(this);
       
        setOnClickListeners();
        
        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
        
        // TODO: Make sure buttons work properly
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        setupTextViews();
    }

    private void setupTextViews()
    {
        // Variables we will need at some point
        // float time, dist, pace;

        activityDBM.open();
        activityInfo = activityDBM.get(activityId);
        activityDBM.close();
    
        raceId_tv.setText("Race Id #" + activityInfo.getString(activityInfo.getColumnIndex(ID)));
        raceDate_tv.setText(activityInfo.getString(activityInfo.getColumnIndex(EDATE)));
        
        
        diaryId = Integer.valueOf(activityInfo.getString(activityInfo.getColumnIndex(DIARYID)));
        
        //updates the location databased part
        updateStatistics();
        initializeMap();
    }
    
    private void updateStatistics(){
    	int time = -1; //-1 defaults for all
    	locationDBM.open();
    	
    	locationCursor = locationDBM.getList(activityId, time);
    	statsLocation = new Statistics(locationCursor);
    	
    	locationDBM.close();
    	
    	raceTotTime_tv.setText("Total Time: " + statsLocation.getRaceTotalTime());
    	raceTotDist_tv.setText("Total Distance: " + statsLocation.getRaceTotalDistance());
    	raceTotPace_tv.setText("Speed: " + (statsLocation.getRaceTotalDistance()/statsLocation.getRaceTotalTime()));
    }

    private void initializeMap(){
    	mapController = mapView.getController();

    	locationCursor.moveToFirst();
    	for(; locationCursor.moveToNext(); locationCursor.moveToNext()){
    		point = new GeoPoint(Integer.valueOf(locationCursor.getString(locationCursor.getColumnIndex(LATITUDE)))
    				, Integer.valueOf(locationCursor.getString(locationCursor.getColumnIndex(LONGITUDE))));
                
        	addGeoPoint(point, "Current Location", "lat : lng");
    	}
    }
    
    
    private void bundleUserInformation(Intent mIntent)
    {
        Bundle b = new Bundle();
        b.putInt(USERID, userId);
        b.putInt(ACTIVITYID, activityId);
        mIntent.putExtras(b);
    }

    private void retrieveBundleInfo()
    {
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        activityId = b.getInt(ACTIVITYID);
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
        //Do Nothing. Static Map
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
    	if(diaryId<1)
    		myIntent = new Intent(w.getContext(), DiaryList.class);
    	else
	        myIntent = new Intent(w.getContext(), DiaryView.class);
	     
    	bundleUserInformation(myIntent);
        startActivity(myIntent);
    }
}
