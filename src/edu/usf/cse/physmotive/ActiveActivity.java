package edu.usf.cse.physmotive;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.usf.cse.physmotive.db.ActivityDBM;
import edu.usf.cse.physmotive.db.LocationDBM;

public class ActiveActivity extends MapActivity implements LocationListener
{
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    public static final String USERID = "userId";
    public static final String STARTTYPE_MAN = "manual";
    public static final String STARTTYPE_AUTO = "automatic";
    public static final int UNITTYPE_TIME = 0;
    public static final int UNITYPE_DIST = 1;

    protected MapView mapView;
    protected MapController mapController;
    protected MapItemizedOverlay itemizedOverlay;
    protected GeoPoint point, curr, prev;
    protected LocationManager locationManager;
    protected List<Overlay> mapOverlays;
    protected Button endActivityButton;
    protected TextView currentDistanceTextView;
    protected TextView currentSpeedTextView;
    
    
    private LocationDBM dblManager;
    private ActivityDBM dbaManager;
    private int userId, raceId, unitType, unitValue;
    private long tTime = 0, tDistance = 0;
    private String startType;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_activity);

        // pulling in bundle information
        Bundle b = getIntent().getExtras();
        userId = b.getInt(USERID);
        raceId = b.getInt("activityId");
        startType = b.getString("startType");
        unitType = b.getInt("unitType");
        unitValue = b.getInt("unitValue");

        Toast.makeText(this, startType, Toast.LENGTH_SHORT).show();

        dblManager = new LocationDBM(this);
        dbaManager = new ActivityDBM(this);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setStreetView(true);

        
        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeedTextView);
        currentDistanceTextView = (TextView) findViewById(R.id.currentDistanceTextView);
        endActivityButton = (Button) findViewById(R.id.endActivityButton);
        afterInit();

        // On Clicks
        setOnClickListeners();

        // TODO: also give me time info etc!!
        // TODO: on update put the totalTime and totalDistance with the
        // activity.

        // Checks from previous screen if it starts manually or automatically.
        // if automatic it will just call for location services, otherwise
        // a dialog box will pop-up and wait till ready before starting them.
        if (startType.equals(STARTTYPE_MAN))
        {
            showDialog(0);
        } else
        {
            initiateLocationServices();
        }

        mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new MapItemizedOverlay(drawable, this);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        return new AlertDialog.Builder(this).setTitle("Manual Start")
                .setPositiveButton("Ready!", new DialogButtonClickHandler()).create();
    }

    private void onFinish()
    {
        stopCounting();
        // If not complete
        // TODO: delete race.
        // Only delete(raceId) needs be called.

        // if Complete:
        // TODO: Get Final GPS pull save with Finished note.
        // dblManager.insert(raceId, lat, lng, spd, lts, "finished", userId);
        // TODO: get tTime to insert int seconds for total time.
        dbaManager.update(raceId, userId, (int)tTime, (int)tDistance);
        

        // If ended early
        // TODO: make sure it saves current info
    }

    private void setOnClickListeners()
    {
        endActivityButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                onButtonClickEndActivityButton(v);
            }
        });
    }

    private void onButtonClickEndActivityButton(View v)
    {
        finish();
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener
    {
        public void onClick(DialogInterface dialog, int clicked)
        {
            switch (clicked) {
            case DialogInterface.BUTTON_POSITIVE:
                initiateLocationServices();
                break;
            }
        }
    }

    private void initiateLocationServices()
    {
        // Use the LocationManager class to obtain GPS locations.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 3, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 3, this);
        dblManager.open();
        dblManager.insert(raceId, "0", "0", "0", 0, "start", userId);
        dblManager.close();

        // TODO: Intitiate Timer. Set sTime (startTime)
        startCounting();

    }

    public void addGeoPoint(GeoPoint p, String greeting, String message)
    {
        OverlayItem overlayitem = new OverlayItem(p, greeting, message);

        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        onFinish();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        initiateLocationServices();
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }
    
    private void updateGeoPoints(GeoPoint p){
    	prev = curr;
    	curr = p;
    }
    
    private void updateStatistics(Location loc){
    	currentSpeedTextView.setText(String.valueOf(loc.getSpeed()));
    	currentDistanceTextView.setText(String.valueOf(tDistance));
    }
    
    @Override
    public void onLocationChanged(Location loc)
    {
    	float[] result = new float[3];
        mapController = mapView.getController();
        
        point = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
        
        
        // TODO: Make sure this logic works.
        // Update GeoPoints to keep current Stats
        updateGeoPoints(point);
        
        // Update the distance for total Distance
        Location.distanceBetween(prev.getLatitudeE6()/1E6, prev.getLongitudeE6()/1E6, curr.getLatitudeE6()/1E6, curr.getLongitudeE6()/1E6, result);
		tDistance += result[0];
		
        // Update Stats on Page
		updateStatistics(loc);
        
        mapController.animateTo(point);
        mapController.setZoom(20);
        mapView.invalidate();

        addGeoPoint(point, "Current Location", loc.getLatitude() + " : " + loc.getLongitude());
        Log.d("lat:long", loc.getLatitude() + ":" + loc.getLongitude());

        dblManager.open();
        dblManager.insert(raceId, String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), String.valueOf(loc.getSpeed()),
                (int) loc.getTime(), "", userId);
        dblManager.close();
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
        // DO Nothing
    }

    // //////////////////////////////////
    // This is about to be really ugly //
    // This is the start of the timer. //
    // //////////////////////////////////
    protected TextView time_tv;
    private long startTimePoint;
    private static long DELAY = 100;
    private String applicationState;

    private void afterInit()
    {
        time_tv = (TextView) findViewById(R.id.timeTextView);
        setLabelText("00:00:00");
        startTimePoint = Long.valueOf(0);
        stopCounting();
    }

    private Handler tasksHandler = new Handler();

    public void startCounting()
    {
        applicationState = StopWatchStates.IN_COUNTING;

        tasksHandler.removeCallbacks(timeTickRunnable);
        tasksHandler.postDelayed(timeTickRunnable, DELAY);

        startTimePoint = System.nanoTime();
    }

    public void stopCounting()
    {
        applicationState = StopWatchStates.IN_WAITING;
    }

    public String currentTimeString()
    {
        long interval = System.nanoTime() - startTimePoint;
        int seconds = (int) (interval / 1000000000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes % 60) + ":"
                + String.format("%02d", seconds % 60);
    }

    public void setLabelText(String string)
    {
        // Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        time_tv.setText(string);
    }

    private Runnable timeTickRunnable = new Runnable() {
        public void run()
        {
            if (applicationState == StopWatchStates.IN_COUNTING)
            {
                setLabelText(currentTimeString());
                tasksHandler.postDelayed(timeTickRunnable, DELAY);
            }
        }
    };

    public void stopButtonClick(View button)
    {
        if (applicationState == StopWatchStates.IN_COUNTING)
        {
            stopCounting();
        } else if (applicationState == StopWatchStates.IN_WAITING)
        {
            startCounting();
        }
    }

    public class StopWatchStates
    {
        public static final String IN_COUNTING = "StopWatchStates.IN_COUNTING";
        public static final String IN_WAITING = "StopWatchStates.IN_WAITING";
    }
    // ///////////////////////////////
    // this is the end of the timer //
    // ///////////////////////////////
}